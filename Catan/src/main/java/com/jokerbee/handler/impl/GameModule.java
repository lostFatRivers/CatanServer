package com.jokerbee.handler.impl;

import com.jokerbee.anno.MessageHandler;
import com.jokerbee.consts.Constants;
import com.jokerbee.consts.MessageType;
import com.jokerbee.handler.AbstractModule;
import com.jokerbee.player.Player;
import com.jokerbee.player.PlayerManager;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;

public class GameModule extends AbstractModule {

    @MessageHandler(code = MessageType.CS_PLAYER_ENTER)
    private void playerEnter(Player player, JsonObject message) throws Exception {
        String playerId = message.getString("playerId");
        if (StringUtils.isEmpty(playerId)) {
            player.sendErrorMessage("player id is empty.");
            return;
        }
        player.setPlayerId(playerId);
        player.setPlayerName(message.getString("name"));
        PlayerManager.getInstance().catchPlayer(player);

        logger.info("player enter server success, id:{}, name:{}", playerId, player.getPlayerName());
        JsonObject result = new JsonObject();
        result.put("type", MessageType.SC_PLAYER_ENTER)
                .put("enterSuccess", true);
        player.sendMessage(result);
    }

    @MessageHandler(code = MessageType.CS_SYNC_ROOM)
    private void syncRooms(Player player, JsonObject message) throws Exception {
        logger.info("player sync rooms");
        vertx.eventBus().publish(Constants.API_SYNC_ROOM, player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_CHANGE_NAME)
    private void rename(Player player, JsonObject message) throws Exception {
        logger.info("player rename");
        player.setPlayerName(message.getString("newName"));
    }

    @MessageHandler(code = MessageType.CS_CREATE_ROOM)
    private void createRooms(Player player, JsonObject message) throws Exception {
        logger.info("player create room");
        if (player.getRoomId() > 0) {
            logger.info("player already have room");
            return;
        }
        vertx.eventBus().<Integer>request(Constants.API_CREATE_ROOM, player.getPlayerId(), res -> {
            if (res.succeeded()) {
                int roomId = res.result().body();
                logger.info("player create room success, roomId:{}", roomId);
                player.setRoomId(roomId);
                player.setCreateRoom(roomId);
                return;
            }
            logger.error("player create room failed.", res.cause());
        });
    }

    @MessageHandler(code = MessageType.CS_DELETE_ROOM)
    private void deleteRoom(Player player, JsonObject message) throws Exception {
        logger.info("player delete room");
        Integer roomId = message.getInteger("roomId");
        if (player.getCreateRoom() <= 0 || roomId == null || player.getCreateRoom() != roomId) {
            logger.info("player delete room error, player.getCreateRoom():{}, roomId:{}", player.getCreateRoom(), roomId);
            return;
        }
        vertx.eventBus().send(Constants.API_DELETE_ROOM_PRE + roomId, player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_EXIT_ROOM)
    private void exitRoom(Player player, JsonObject message) throws Exception {
        logger.info("player exit room");
        Integer roomId = message.getInteger("roomId");
        if (player.getRoomId() <= 0 || roomId == null || player.getRoomId() != roomId) {
            logger.info("player delete room error, player.getRoomId():{}, roomId:{}", player.getRoomId(), roomId);
            return;
        }
        vertx.eventBus().send(Constants.API_EXIT_ROOM_PRE + roomId, player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_JOIN_ROOM)
    private void joinRoom(Player player, JsonObject message) throws Exception {
        logger.info("player join room");
        Integer roomId = message.getInteger("roomId");
        if (player.getRoomId() > 0 || roomId == null) {
            logger.info("player join room error, player.getRoomId():{}, roomId:{}", player.getRoomId(), roomId);
            return;
        }
        vertx.eventBus().send(Constants.API_JOIN_ROOM_PRE + roomId, player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_START_GAME)
    private void startGame(Player player, JsonObject message) throws Exception {
        logger.info("player start game");
        Integer roomId = message.getInteger("roomId");
        if (player.getCreateRoom() <= 0 || roomId == null || player.getCreateRoom() != roomId) {
            logger.info("player start game error, player.getRoomId():{}, roomId:{}", player.getRoomId(), roomId);
            return;
        }
        vertx.eventBus().send(Constants.API_START_GAME_PRE + roomId, player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_COLOR_SELECT)
    private void selectColor(Player player, JsonObject message) throws Exception {
        logger.info("player select color");
        String colorStr = message.getString("colorStr");
        if (player.getRoomId() <= 0) {
            logger.info("player select color error, not in room");
            return;
        }
        JsonObject msg = new JsonObject().put("colorStr", colorStr).put("playerId", player.getPlayerId());
        vertx.eventBus().send(Constants.API_SELECT_COLOR_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_BUILD_ROAD)
    private void buildRoad(Player player, JsonObject message) throws Exception {
        String roadKey = message.getString("roadKey");
        Integer roleIndex = message.getInteger("roleIndex");
        logger.info("player build road:{}, roleIndex:{}", roadKey, roleIndex);
        if (player.getRoomId() <= 0 || StringUtils.isEmpty(roadKey) || roleIndex == null) {
            logger.error("player build road error, not in room");
            return;
        }
        JsonObject msg = new JsonObject().put("roadKey", roadKey).put("playerId", player.getPlayerId()).put("roleIndex", roleIndex);
        vertx.eventBus().send(Constants.API_BUILD_ROAD_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_BUILD_CITY)
    private void buildCity(Player player, JsonObject message) throws Exception {
        String cityKey = message.getString("cityKey");
        Integer roleIndex = message.getInteger("roleIndex");
        Integer cityType = message.getInteger("cityType");
        logger.info("player build city:{}, roleIndex:{}, cityType:{}", cityKey, roleIndex, cityType);
        if (player.getRoomId() <= 0 || StringUtils.isEmpty(cityKey) || roleIndex == null || cityType == null) {
            logger.error("player build city error, not in room");
            return;
        }
        JsonObject msg = new JsonObject().put("cityKey", cityKey).put("playerId", player.getPlayerId()).put("roleIndex", roleIndex)
                .put("cityType", cityType);
        vertx.eventBus().send(Constants.API_BUILD_CITY_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_THROW_DICE)
    private void throwDice(Player player, JsonObject message) throws Exception {
        Integer dice1 = message.getInteger("diceNum1");
        Integer dice2 = message.getInteger("diceNum2");
        logger.info("player throw dice, dice1:{}, dice2:{}", dice1, dice2);
        if (player.getRoomId() <= 0 || dice1 == null || dice2 == null) {
            logger.error("player throw dice error.");
            return;
        }
        JsonObject msg = new JsonObject().put("playerId", player.getPlayerId()).put("dice1", dice1)
                .put("dice2", dice2);
        vertx.eventBus().send(Constants.API_THROW_DICE_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_TURN_NEXT_ONE)
    private void turnNext(Player player, JsonObject message) throws Exception {
        logger.info("player turn next.");
        if (player.getRoomId() <= 0) {
            logger.error("player turn next error.");
            return;
        }
        vertx.eventBus().send(Constants.API_TURN_NEXT_PRE + player.getRoomId(), player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_SYNC_ROLE_VIEW)
    private void syncRole(Player player, JsonObject message) throws Exception {
        logger.info("player sync role.");
        Integer sourceCardNum = message.getInteger("sourceCardNum");
        Integer skillCardNum = message.getInteger("skillCardNum");
        Integer robTimes = message.getInteger("robTimes");
        Integer roadLength = message.getInteger("roadLength");
        Integer totalScore = message.getInteger("totalScore");
        if (player.getRoomId() <= 0 || sourceCardNum == null || skillCardNum == null || robTimes == null
            || roadLength == null || totalScore == null) {
            logger.error("player sync role error.");
            return;
        }
        JsonObject msg = new JsonObject().put("playerId", player.getPlayerId()).put("sourceCardNum", sourceCardNum)
                .put("skillCardNum", skillCardNum).put("robTimes", robTimes).put("roadLength", roadLength)
                .put("totalScore", totalScore);
        vertx.eventBus().send(Constants.API_SYNC_ROLE_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_START_EXCHANGE)
    private void startExchange(Player player, JsonObject message) throws Exception {
        logger.info("player start exchange.");
        if (player.getRoomId() <= 0 || !message.containsKey("outWoodNum") || !message.containsKey("outBrickNum")
                || !message.containsKey("outSheepNum") || !message.containsKey("outRiceNum")|| !message.containsKey("outStoneNum")
                || !message.containsKey("inWoodNum") || !message.containsKey("inBrickNum")|| !message.containsKey("inSheepNum")
                || !message.containsKey("inRiceNum") || !message.containsKey("inStoneNum")) {
            logger.error("player start exchange error.");
            return;
        }
        message.put("playerId", player.getPlayerId());
        vertx.eventBus().send(Constants.API_START_EXCHANGE_PRE + player.getRoomId(), message);
    }

    @MessageHandler(code = MessageType.CS_CLOSE_EXCHANGE)
    private void closeExchange(Player player, JsonObject message) throws Exception {
        logger.info("player close exchange.");
        if (player.getRoomId() <= 0) {
            logger.error("player close exchange error.");
            return;
        }
        vertx.eventBus().send(Constants.API_CLOSE_EXCHANGE_PRE + player.getRoomId(), player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_ACCEPT_EXCHANGE)
    private void acceptExchange(Player player, JsonObject message) throws Exception {
        logger.info("player accept exchange.");
        if (player.getRoomId() <= 0 || !message.containsKey("roleIndex")) {
            logger.error("player accept exchange error.");
            return;
        }
        vertx.eventBus().send(Constants.API_ACCEPT_EXCHANGE_PRE + player.getRoomId(), player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_RESUME_EXCHANGE)
    private void resumeExchange(Player player, JsonObject message) throws Exception {
        logger.info("player resume exchange.");
        if (player.getRoomId() <= 0 || !message.containsKey("roleIndex")) {
            logger.error("player resume exchange error.");
            return;
        }
        vertx.eventBus().send(Constants.API_RESUME_EXCHANGE_PRE + player.getRoomId(), player.getPlayerId());
    }

    @MessageHandler(code = MessageType.CS_CONFIRM_EXCHANGE)
    private void confirmExchange(Player player, JsonObject message) throws Exception {
        logger.info("player confirm exchange.");
        if (player.getRoomId() <= 0 || !message.containsKey("targetId")) {
            logger.error("player resume exchange error.");
            return;
        }
        JsonObject msg = new JsonObject().put("playerId", player.getPlayerId()).put("targetId", message.getString("targetId"));
        vertx.eventBus().send(Constants.API_CONFIRM_EXCHANGE_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_SEND_CHAT)
    private void sendChat(Player player, JsonObject message) throws Exception {
        logger.info("player send chat.");
        if (player.getRoomId() <= 0 || !message.containsKey("nickName") || !message.containsKey("chatMsg")) {
            logger.error("player send chat error.");
            return;
        }
        JsonObject msg = new JsonObject().put("nickName", message.getString("nickName")).put("chatMsg", message.getString("chatMsg"));
        vertx.eventBus().send(Constants.API_SEND_CHAT_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_ROB_OUT_SOURCE)
    private void sysRobOut(Player player, JsonObject message) throws Exception {
        logger.info("player system rob out source.");
        if (player.getRoomId() <= 0 || player.getRoleIndex() != message.getInteger("roleIndex")) {
            logger.error("player system rob out error.");
            return;
        }
        JsonObject msg = new JsonObject()
                .put("playerId", player.getPlayerId())
                .put("brickNum", message.getInteger("brickNum", 0))
                .put("riceNum", message.getInteger("riceNum", 0))
                .put("sheepNum", message.getInteger("sheepNum", 0))
                .put("stoneNum", message.getInteger("stoneNum", 0))
                .put("woodNum", message.getInteger("woodNum", 0));
        vertx.eventBus().send(Constants.API_SYS_ROB_OUT_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_ROBBER_PUT_MAP)
    private void putRobber(Player player, JsonObject message) throws Exception {
        logger.info("player put rob on map.");
        if (player.getRoomId() <= 0 || message.getInteger("mapIndex") == null) {
            logger.error("player put rob on map error.");
            return;
        }
        JsonObject msg = new JsonObject()
                .put("playerId", player.getPlayerId())
                .put("mapIndex", message.getInteger("mapIndex"));
        vertx.eventBus().send(Constants.API_PUT_ROBBER_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_PLAYER_SELECTED_ROB_TARGET)
    private void playerSelectRobTarget(Player player, JsonObject message) throws Exception {
        logger.info("player select rob target.");
        if (player.getRoomId() <= 0 || message.getInteger("targetIndex") == null) {
            logger.error("player select rob target error.");
            return;
        }
        JsonObject msg = new JsonObject()
                .put("playerId", player.getPlayerId())
                .put("targetIndex", message.getInteger("targetIndex"));
        vertx.eventBus().send(Constants.API_PLAYER_SELECT_ROB_TARGET_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_PLAYER_ROB_TARGET_BACK)
    private void playerRobBack(Player player, JsonObject message) throws Exception {
        logger.info("player robbed back target.");
        if (player.getRoomId() <= 0 || !message.containsKey("sourceType")) {
            logger.error("player robbed back target.");
            return;
        }
        JsonObject msg = new JsonObject()
                .put("playerId", player.getPlayerId())
                .put("sourceType", message.getString("sourceType"));
        vertx.eventBus().send(Constants.API_PLAYER_ROB_BACK_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_USE_SKILL_CARD)
    private void useSkill(Player player, JsonObject message) throws Exception {
        logger.info("player use skill.");
        if (player.getRoomId() <= 0 || message.getInteger("cardType") == null) {
            logger.error("player use skill error.");
            return;
        }
        JsonObject msg = new JsonObject()
                .put("playerId", player.getPlayerId())
                .put("cardType", message.getInteger("cardType"))
                .put("cardParam", message.getString("cardParam"));
        vertx.eventBus().send(Constants.API_USE_SKILL_CARD_PRE + player.getRoomId(), msg);
    }

    @MessageHandler(code = MessageType.CS_GET_SKILL_CARD)
    private void getSkill(Player player, JsonObject message) throws Exception {
        logger.info("player get skill.");
        if (player.getRoomId() <= 0) {
            logger.error("player get skill error.");
            return;
        }
        JsonObject msg = new JsonObject()
                .put("playerId", player.getPlayerId());
        vertx.eventBus().send(Constants.API_GET_SKILL_CARD_PRE + player.getRoomId(), msg);
    }
}
