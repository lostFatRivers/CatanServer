package com.jokerbee.verticle;

import com.jokerbee.consts.Constants;
import com.jokerbee.consts.GameStatus;
import com.jokerbee.consts.MessageType;
import com.jokerbee.model.RoomModel;
import com.jokerbee.player.Player;
import com.jokerbee.player.PlayerManager;
import com.jokerbee.util.RandomUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("Room");

    private final Map<Integer, RoomModel> rooms = new HashMap<>();

    @Override
    public void start() {
        vertx.eventBus().consumer(Constants.API_SYNC_ROOM, this::syncRoom);
        vertx.eventBus().consumer(Constants.API_CREATE_ROOM, this::createRoom);
    }

    private void syncRoom(Message<String> tMessage) {
        String playerId = tMessage.body();
        if (StringUtils.isEmpty(playerId)) {
            logger.error("sync room error, player is empty.");
            return;
        }
        Player player = PlayerManager.getInstance().getPlayer(playerId);
        if (player == null) {
            logger.error("sync room error, player not exist.");
            return;
        }
        JsonArray roomArray = new JsonArray();
        rooms.values().stream()
                .filter(room -> room.checkStatus(GameStatus.PREPARING))
                .map(this::buildRoomData)
                .forEach(roomArray::add);

        JsonObject result = new JsonObject();
        result.put("type", MessageType.SC_SYNC_ROOM)
                .put("rooms", roomArray);
        player.sendMessage(result);
    }

    private JsonObject buildRoomData(RoomModel room) {
        JsonObject roomData = new JsonObject();
        JsonObject members = new JsonObject();
        room.getMembers().forEach(members::put);

        roomData.put("roomId", room.getRoomId())
                .put("masterId", room.getMasterId())
                .put("members", members);
        return roomData;
    }

    private void createRoom(Message<String> tMessage) {
        String playerId = tMessage.body();
        int roomId = PlayerManager.getInstance().nextRoomId();
        RoomModel room = new RoomModel();
        room.setRoomId(roomId);
        room.setMasterId(playerId);
        room.setStatus(GameStatus.PREPARING);

        Player player = PlayerManager.getInstance().getPlayer(playerId);
        room.getMembers().put(player.getPlayerId(), player.getPlayerName());

        rooms.put(roomId, room);

        registerRoomConsumers(room);

        syncToAllPlayer(room);

        tMessage.reply(roomId);
    }

    private void registerRoomConsumers(RoomModel room) {
        int roomId = room.getRoomId();
        this.<String>registerConsumer(room, Constants.API_DELETE_ROOM_PRE + roomId, msg -> this.deleteRoom(roomId, msg));
        this.<String>registerConsumer(room, Constants.API_JOIN_ROOM_PRE + roomId, msg -> this.joinRoom(roomId, msg));
        this.<String>registerConsumer(room, Constants.API_EXIT_ROOM_PRE + roomId, msg -> this.exitRoom(roomId, msg));
        this.<String>registerConsumer(room, Constants.API_START_GAME_PRE + roomId, msg -> this.startGame(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_SELECT_COLOR_PRE + roomId, msg -> this.selectColor(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_BUILD_ROAD_PRE + roomId, msg -> this.buildRoad(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_BUILD_CITY_PRE + roomId, msg -> this.buildCity(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_THROW_DICE_PRE + roomId, msg -> this.syncDice(roomId, msg));
        this.<String>registerConsumer(room, Constants.API_TURN_NEXT_PRE + roomId, msg -> this.turnNext(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_SYNC_ROLE_PRE + roomId, msg -> this.syncRole(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_START_EXCHANGE_PRE + roomId, msg -> this.startExchange(roomId, msg));
        this.<String>registerConsumer(room, Constants.API_CLOSE_EXCHANGE_PRE + roomId, msg -> this.closeExchange(roomId, msg));
        this.<String>registerConsumer(room, Constants.API_ACCEPT_EXCHANGE_PRE + roomId, msg -> this.acceptExchange(roomId, msg));
        this.<String>registerConsumer(room, Constants.API_RESUME_EXCHANGE_PRE + roomId, msg -> this.resumeExchange(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_CONFIRM_EXCHANGE_PRE + roomId, msg -> this.confirmExchange(roomId, msg));
        this.<JsonObject>registerConsumer(room, Constants.API_SEND_CHAT_PRE + roomId, msg -> this.sendChat(roomId, msg));
    }

    private <T> void registerConsumer(RoomModel room, String address, Handler<Message<T>> handler) {
        MessageConsumer<T> consumer = vertx.eventBus().consumer(address, handler);
        room.getConsumers().add(consumer);
    }

    private void cancelRoomConsumers(RoomModel room) {
        room.getConsumers().forEach(MessageConsumer::unregister);
    }

    private void joinRoom(int roomId, Message<String> msg) {
        RoomModel roomModel = rooms.get(roomId);
        Player player = PlayerManager.getInstance().getPlayer(msg.body());
        if (roomModel == null || player == null) {
            logger.info("not exist room:{}, player:{}", roomId, player);
            return;
        }
        player.setRoomId(roomId);
        roomModel.getMembers().put(player.getPlayerId(), player.getPlayerName());

        JsonObject result = new JsonObject();
        result.put("type", MessageType.SC_JOIN_ROOM)
                .put("roomData", buildRoomData(roomModel));
        player.sendMessage(result);

        this.syncToAllPlayer(roomModel);
    }

    private void exitRoom(int roomId, Message<String> msg) {
        RoomModel roomModel = rooms.get(roomId);
        if (roomModel == null) {
            logger.info("not exist room:{}", roomId);
            return;
        }
        roomModel.getMembers().remove(msg.body());
        this.exitRoom(roomId, msg.body());
        this.syncToAllPlayer(roomModel);
    }

    private void deleteRoom(int roomId, Message<String> msg) {
        RoomModel removeRoom = rooms.remove(roomId);
        if (removeRoom == null) {
            return;
        }
        cancelRoomConsumers(removeRoom);
        removeRoom.getMembers().keySet().forEach(eachMemberId -> this.exitRoom(roomId, eachMemberId));

        Player player = PlayerManager.getInstance().getPlayer(msg.body());
        if (player != null) {
            player.setCreateRoom(0);
        }

        JsonObject result = new JsonObject();
        result.put("type", MessageType.SC_DELETE_ROOM)
                .put("roomId", roomId);
        PlayerManager.getInstance().sendToAll(result);
    }

    private void exitRoom(int roomId, String memberId) {
        Player player = PlayerManager.getInstance().getPlayer(memberId);
        if (player == null) {
            return;
        }
        if (player.getRoomId() == roomId) {
            player.setRoomId(0);
        }
        JsonObject result = new JsonObject();
        result.put("type", MessageType.SC_EXIT_ROOM)
            .put("roomId", roomId);
        player.sendMessage(result);
    }

    private void syncToAllPlayer(RoomModel room) {
        JsonArray roomArray = new JsonArray();
        roomArray.add(buildRoomData(room));

        JsonObject result = new JsonObject();
        result.put("type", MessageType.SC_SYNC_ROOM)
                .put("rooms", roomArray);
        PlayerManager.getInstance().sendToAll(result);
    }

    private void startGame(int roomId, Message<String> msg) {
        String playerId = msg.body();
        Player player = PlayerManager.getInstance().getPlayer(playerId);
        RoomModel room = rooms.get(roomId);
        if (room == null || !room.checkStatus(GameStatus.PREPARING)) {
            player.sendErrorMessage("房间号错误.");
            return;
        }
        if (!room.getMasterId().equals(playerId)) {
            player.sendErrorMessage("你不是房主, 不能开启.");
            return;
        }
        room.setStatus(GameStatus.COLOR_CHOOSING);
        sendInitRoomData(room);

        vertx.setTimer(100, tid -> {
            JsonObject result = new JsonObject();
            result.put("type", MessageType.SC_DELETE_ROOM)
                    .put("roomId", roomId);
            PlayerManager.getInstance().sendToAll(result);
        });
    }

    private void sendInitRoomData(RoomModel room) {
        JsonObject gameRoomData = new JsonObject();
        gameRoomData.put("type", MessageType.SC_START_GAME)
                .put("roomId", room.getRoomId())
                .put("seed", randomGameSeed());

        Map<String, String> members = room.getMembers();
        List<Integer> roleIndexes = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            roleIndexes.add(i);
        }
        Collections.shuffle(roleIndexes);
        AtomicInteger index = new AtomicInteger();
        JsonArray roles = new JsonArray();
        members.keySet().forEach(eachPlayerId -> {
            Player player = PlayerManager.getInstance().getPlayer(eachPlayerId);
            int roleIndex = roleIndexes.get(index.getAndIncrement());
            player.setRoleIndex(roleIndex);
            roles.add(player.buildRoleData());
        });

        gameRoomData.put("allGameRoles", roles);

        members.keySet().forEach(eachPlayerId -> {
            Player player = PlayerManager.getInstance().getPlayer(eachPlayerId);
            gameRoomData.put("playerIndex", player.getRoleIndex());
            player.sendMessage(gameRoomData);
        });
    }

    private int randomGameSeed() {
        return RandomUtil.getRandom(0, 5000);
    }

    private void selectColor(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        String playerId = data.getString("playerId");
        String colorStr = data.getString("colorStr");
        Player player = PlayerManager.getInstance().getPlayer(playerId);
        RoomModel room = rooms.get(roomId);
        if (room == null || !room.checkStatus(GameStatus.COLOR_CHOOSING)) {
            player.sendErrorMessage("房间异常.");
            return;
        }
        JsonObject result = new JsonObject().put("type", MessageType.SC_COLOR_SELECT);
        if (StringUtils.isNotEmpty(room.getColorPlayerId(colorStr))) {
            result.put("success", false);
            player.sendMessage(result);
            player.sendErrorMessage("颜色已经被别人选择.");
            return;
        }
        room.setColorPlayerId(colorStr, playerId);
        result.put("success", true)
                .put("playerId", playerId)
                .put("roleIndex", player.getRoleIndex())
                .put("colorStr", colorStr);
        room.sendToAllPlayer(result);

        if (room.getMembers().size() > room.getColorMembers().size()) {
            return;
        }
        vertx.setTimer(1000, tid -> {
            room.setStatus(GameStatus.PRE_ROUND_1);
            room.syncRoomStatus();
        });
    }

    private void buildRoad(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        String roadKey = data.getString("roadKey");
        int roleIndex = data.getInteger("roleIndex");
        String playerId = data.getString("playerId");
        Player player = PlayerManager.getInstance().getPlayer(playerId);
        RoomModel room = rooms.get(roomId);
        if (room == null) {
            player.sendErrorMessage("房间异常.");
            return;
        }
        room.cacheRoadOwner(roadKey, roleIndex);

        JsonObject result = new JsonObject().put("type", MessageType.SC_BUILD_ROAD)
                .put("roadKey", roadKey)
                .put("roleIndex", roleIndex);
        room.sendToAllPlayer(result);
    }

    private void buildCity(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        String cityKey = data.getString("cityKey");
        Integer roleIndex = data.getInteger("roleIndex");
        Integer cityType = data.getInteger("cityType");
        String playerId = data.getString("playerId");
        Player player = PlayerManager.getInstance().getPlayer(playerId);
        RoomModel room = rooms.get(roomId);
        if (room == null) {
            player.sendErrorMessage("房间异常.");
            return;
        }
        room.cacheCityOwner(cityKey, roleIndex);

        JsonObject result = new JsonObject().put("type", MessageType.SC_BUILD_CITY)
                .put("cityKey", cityKey)
                .put("roleIndex", roleIndex)
                .put("cityType", cityType);
        room.sendToAllPlayer(result);
    }

    private void syncDice(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        int dice1 = data.getInteger("dice1");
        int dice2 = data.getInteger("dice2");
        RoomModel room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        JsonObject result = new JsonObject().put("type", MessageType.SC_THROW_DICE)
                .put("diceNum1", dice1)
                .put("diceNum2", dice2);
        room.sendToAllPlayer(result);
    }

    private void turnNext(int roomId, Message<String> msg) {
        RoomModel room = rooms.get(roomId);
        if (room == null) {
            logger.info("not exist room:{}", roomId);
            return;
        }
        JsonObject result = new JsonObject().put("type", MessageType.SC_TURN_NEXT_ONE);
        room.sendToAllPlayer(result);
    }

    private void syncRole(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        int sourceCardNum = data.getInteger("sourceCardNum");
        int skillCardNum = data.getInteger("skillCardNum");
        int robTimes = data.getInteger("robTimes");
        int roadLength = data.getInteger("roadLength");
        int totalScore = data.getInteger("totalScore");
        String playerId = data.getString("playerId");
        Player player = PlayerManager.getInstance().getPlayer(playerId);
        RoomModel room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        // 最长路通知
        int roadLimit = Constants.MAX_ROAD_LENGTH_LIMIT;
        if (StringUtils.isNotEmpty(room.getMaxRoadPlayerId())) {
            roadLimit = room.getMaxRoadLength();
        }
        if (roadLength > roadLimit) {
            room.setMaxRoadLength(roadLength);
            room.setMaxRoadPlayerId(playerId);

            JsonObject result = new JsonObject().put("type", MessageType.SC_MAX_ROAD_LENGTH_NOTICE);
            result.put("roleIndex", player.getRoleIndex())
                    .put("roadLength", roadLength);
            room.sendToAllPlayer(result);
        }
        // 最大士兵数通知
        int robLimit = Constants.MAX_ROB_TIMES_LIMIT;
        if (StringUtils.isNotEmpty(room.getMaxRobPlayerId())) {
            robLimit = room.getMaxRobTimes();
        }
        if (robTimes > robLimit) {
            room.setMaxRobTimes(robTimes);
            room.setMaxRobPlayerId(playerId);

            JsonObject result = new JsonObject().put("type", MessageType.SC_MAX_ROB_TIMES_NOTICE);
            result.put("roleIndex", player.getRoleIndex())
                    .put("robTimes", robTimes);
            room.sendToAllPlayer(result);
        }

        JsonObject result = new JsonObject().put("type", MessageType.SC_SYNC_ROLE_VIEW);
        result.put("roleIndex", player.getRoleIndex())
                .put("sourceCardNum", sourceCardNum)
                .put("skillCardNum", skillCardNum)
                .put("robTimes", robTimes)
                .put("roadLength", roadLength)
                .put("totalScore", totalScore);
        room.sendToAllPlayer(result);
    }

    private void startExchange(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        String playerId = data.getString("playerId");
        int roleIndex = data.getInteger("roleIndex");

        int outWoodNum = data.getInteger("outWoodNum");
        int outBrickNum = data.getInteger("outBrickNum");
        int outSheepNum = data.getInteger("outSheepNum");
        int outRiceNum = data.getInteger("outRiceNum");
        int outStoneNum = data.getInteger("outStoneNum");

        int inWoodNum = data.getInteger("inWoodNum");
        int inBrickNum = data.getInteger("inBrickNum");
        int inSheepNum = data.getInteger("inSheepNum");
        int inRiceNum = data.getInteger("inRiceNum");
        int inStoneNum = data.getInteger("inStoneNum");

        RoomModel room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        room.setStartExchangePlayerId(playerId);
        room.setExchangeInfo(data);
        room.setAcceptExchangePlayerId("");

        JsonObject result = new JsonObject().put("type", MessageType.SC_START_EXCHANGE);
        result.put("roleIndex", roleIndex)
                .put("outWoodNum", inWoodNum).put("outBrickNum", inBrickNum)
                .put("outSheepNum", inSheepNum).put("outRiceNum", inRiceNum)
                .put("outStoneNum", inStoneNum)
                .put("inWoodNum", outWoodNum).put("inBrickNum", outBrickNum)
                .put("inSheepNum", outSheepNum).put("inRiceNum", outRiceNum)
                .put("inStoneNum", outStoneNum);
        room.sendToAllPlayer(result);
    }

    private void closeExchange(int roomId, Message<String> msg) {
        String playerId = msg.body();
        RoomModel room = rooms.get(roomId);
        if (room == null) {
            logger.info("not exist room:{}", roomId);
            return;
        }
        room.setStartExchangePlayerId("");
        room.setExchangeInfo(null);
        room.setAcceptExchangePlayerId("");

        Player player = PlayerManager.getInstance().getPlayer(playerId);
        JsonObject result = new JsonObject().put("type", MessageType.SC_CLOSE_EXCHANGE)
                .put("roleIndex", player.getRoleIndex());
        room.sendToAllPlayer(result);
    }

    private void acceptExchange(int roomId, Message<String> msg) {
        String playerId = msg.body();
        sendExchangeResult(playerId, roomId, true);
    }

    private void resumeExchange(int roomId, Message<String> msg) {
        String playerId = msg.body();
        sendExchangeResult(playerId, roomId, false);
    }

    private void sendExchangeResult(String playerId, int roomId, boolean accept) {
        Player player = PlayerManager.getInstance().getPlayer(playerId);

        RoomModel room = rooms.get(roomId);
        if (room == null || "".equals(room.getStartExchangePlayerId())) {
            return;
        }
        String startPlayerId = room.getStartExchangePlayerId();
        Player startPlayer = PlayerManager.getInstance().getPlayer(startPlayerId);

        JsonObject result;
        if (accept) {
            result = new JsonObject().put("type", MessageType.SC_ACCEPT_EXCHANGE)
                    .put("acceptRoleIndex", player.getRoleIndex());
        } else {
            result = new JsonObject().put("type", MessageType.SC_RESUME_EXCHANGE)
                    .put("resumeRoleIndex", player.getRoleIndex());
        }
        startPlayer.sendMessage(result);
    }

    private void confirmExchange(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        String playerId = data.getString("playerId");
        String targetId = data.getString("targetId");
        Player player = PlayerManager.getInstance().getPlayer(playerId);
        Player target = PlayerManager.getInstance().getPlayer(targetId);

        RoomModel room = rooms.get(roomId);
        if (room == null || StringUtils.isEmpty(room.getStartExchangePlayerId())
                || StringUtils.isNotEmpty(room.getAcceptExchangePlayerId())) {
            return;
        }
        room.setAcceptExchangePlayerId(targetId);
        room.getMembers().keySet().forEach(eachPlayerId -> {
            if (eachPlayerId.equals(playerId) || eachPlayerId.equals(targetId)) {
                return;
            }
            Player eachPlayer = PlayerManager.getInstance().getPlayer(eachPlayerId);
            JsonObject result1 = new JsonObject().put("type", MessageType.SC_CLOSE_EXCHANGE)
                    .put("roleIndex", player.getRoleIndex())
                    .put("targetIndex", target.getRoleIndex())
                    .put("isExchanged", true);
            eachPlayer.sendMessage(result1);
        });

        JsonObject exchangeInfo = room.getExchangeInfo();

        JsonObject result2 = new JsonObject().put("type", MessageType.SC_CONFIRM_EXCHANGE)
                .put("roleIndex", player.getRoleIndex())
                .put("targetIndex", target.getRoleIndex())
                .put("inBrickNum", exchangeInfo.getInteger("inBrickNum")).put("inRiceNum", exchangeInfo.getInteger("inRiceNum"))
                .put("inSheepNum", exchangeInfo.getInteger("inSheepNum")).put("inStoneNum", exchangeInfo.getInteger("inStoneNum"))
                .put("inWoodNum", exchangeInfo.getInteger("inWoodNum"))
                .put("outBrickNum", exchangeInfo.getInteger("outBrickNum")).put("outRiceNum", exchangeInfo.getInteger("outRiceNum"))
                .put("outSheepNum", exchangeInfo.getInteger("outSheepNum")).put("outStoneNum", exchangeInfo.getInteger("outStoneNum"))
                .put("outWoodNum", exchangeInfo.getInteger("outWoodNum"));
        player.sendMessage(result2);

        JsonObject result3 = new JsonObject().put("type", MessageType.SC_CONFIRM_EXCHANGE)
                .put("roleIndex", player.getRoleIndex())
                .put("targetIndex", target.getRoleIndex())
                .put("inBrickNum", exchangeInfo.getInteger("outBrickNum")).put("inRiceNum", exchangeInfo.getInteger("outRiceNum"))
                .put("inSheepNum", exchangeInfo.getInteger("outSheepNum")).put("inStoneNum", exchangeInfo.getInteger("outStoneNum"))
                .put("inWoodNum", exchangeInfo.getInteger("outWoodNum"))
                .put("outBrickNum", exchangeInfo.getInteger("inBrickNum")).put("outRiceNum", exchangeInfo.getInteger("inRiceNum"))
                .put("outSheepNum", exchangeInfo.getInteger("inSheepNum")).put("outStoneNum", exchangeInfo.getInteger("inStoneNum"))
                .put("outWoodNum", exchangeInfo.getInteger("inWoodNum"));
        target.sendMessage(result3);
    }

    private void sendChat(int roomId, Message<JsonObject> msg) {
        JsonObject data = msg.body();
        String nickName = data.getString("nickName");
        String chatMsg = data.getString("chatMsg");

        RoomModel room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        JsonObject result = new JsonObject().put("type", MessageType.SC_SEND_CHAT)
                .put("nickName", nickName).put("chatContent", chatMsg);
        room.sendToAllPlayer(result);
    }

}
