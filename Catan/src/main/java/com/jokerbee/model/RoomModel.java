package com.jokerbee.model;

import com.jokerbee.consts.GameStatus;
import com.jokerbee.consts.MessageType;
import com.jokerbee.player.Player;
import com.jokerbee.player.PlayerManager;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomModel {
    private int roomId;
    private String masterId;
    // 玩家id: 玩家名
    private Map<String, String> members = new HashMap<>();

    private List<MessageConsumer<?>> consumers = new ArrayList<>();

    private GameStatus status;

    // 颜色 - 玩家id
    private final Map<String, String> colorMembers = new HashMap<>();

    // roadKey - roleIndex
    private final Map<String, Integer> roadCache = new HashMap<>();

    // roadKey - roleIndex
    private final Map<String, Integer> cityCache = new HashMap<>();

    private String startExchangePlayerId = "";
    private JsonObject exchangeInfo;
    private String acceptExchangePlayerId = "";

    // 最长路长度和最长路玩家id
    private int maxRoadLength = -1;
    private String maxRoadPlayerId = "";

    // 最大士兵使用数量和玩家id
    private int maxRobTimes = -1;
    private String maxRobPlayerId = "";

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

    public List<MessageConsumer<?>> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<MessageConsumer<?>> consumers) {
        this.consumers = consumers;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void syncRoomStatus() {
        JsonObject msg = new JsonObject().put("type", MessageType.SC_SYNC_ROOM_STATUS)
                .put("status", status.getName())
                .put("trans", status.getTrans());
        sendToAllPlayer(msg);
    }

    public boolean checkStatus(GameStatus status) {
        return this.status == status;
    }

    public String getColorPlayerId(String colorStr) {
        return colorMembers.get(colorStr);
    }

    public void setColorPlayerId(String colorStr, String playerId) {
        colorMembers.put(colorStr, playerId);
    }

    public void sendToAllPlayer(JsonObject msg) {
        members.keySet().forEach(eachPlayerId -> {
            Player player = PlayerManager.getInstance().getPlayer(eachPlayerId);
            player.sendMessage(msg);
        });
    }

    public Map<String, String> getColorMembers() {
        return colorMembers;
    }

    public boolean cacheRoadOwner(String roadKey, int roleIndex) {
        if (roadCache.containsKey(roadKey)) {
            return false;
        }
        roadCache.put(roadKey, roleIndex);
        return true;
    }

    public boolean cacheCityOwner(String cityKey, int roleIndex) {
        if (cityCache.containsKey(cityKey)) {
            return false;
        }
        cityCache.put(cityKey, roleIndex);
        return true;
    }

    public JsonObject getExchangeInfo() {
        return exchangeInfo;
    }

    public void setExchangeInfo(JsonObject exchangeInfo) {
        this.exchangeInfo = exchangeInfo;
    }

    public String getStartExchangePlayerId() {
        return startExchangePlayerId;
    }

    public void setStartExchangePlayerId(String startExchangePlayerId) {
        this.startExchangePlayerId = startExchangePlayerId;
    }

    public String getAcceptExchangePlayerId() {
        return acceptExchangePlayerId;
    }

    public void setAcceptExchangePlayerId(String acceptExchangePlayerId) {
        this.acceptExchangePlayerId = acceptExchangePlayerId;
    }

    public int getMaxRoadLength() {
        return maxRoadLength;
    }

    public void setMaxRoadLength(int maxRoadLength) {
        this.maxRoadLength = maxRoadLength;
    }

    public String getMaxRoadPlayerId() {
        return maxRoadPlayerId;
    }

    public void setMaxRoadPlayerId(String maxRoadPlayerId) {
        this.maxRoadPlayerId = maxRoadPlayerId;
    }

    public int getMaxRobTimes() {
        return maxRobTimes;
    }

    public void setMaxRobTimes(int maxRobTimes) {
        this.maxRobTimes = maxRobTimes;
    }

    public String getMaxRobPlayerId() {
        return maxRobPlayerId;
    }

    public void setMaxRobPlayerId(String maxRobPlayerId) {
        this.maxRobPlayerId = maxRobPlayerId;
    }
}
