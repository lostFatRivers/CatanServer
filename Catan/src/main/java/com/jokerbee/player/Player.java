package com.jokerbee.player;

import com.jokerbee.consts.MessageType;
import com.jokerbee.handler.HandlerManager;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private static Logger logger = LoggerFactory.getLogger("Player");

    private final ServerWebSocket connector;
    private String playerId;
    private String playerName = "玩家007";

    private volatile int roomId = 0;

    private int createRoom = 0;

    private String colorStr = "#000000";

    private int roleIndex = -1;

    private Map<String, Integer> resourceMap = new HashMap<>();

    private Map<String, Integer> skillMap = new HashMap<>();

    private int robTimes = 0;

    public Player(ServerWebSocket webSocket) {
        connector = webSocket;
        webSocket.textMessageHandler(this::messageHandler);
    }

    private void messageHandler(String textMessage) {
        JsonObject json = new JsonObject(textMessage);
        Integer msgType = json.getInteger("type");
        if (msgType == null) {
            return;
        }
        if (playerId == null && msgType != MessageType.CS_PLAYER_ENTER) {
            logger.info("receive text message: {}, but player not init.", msgType);
            return;
        }
        HandlerManager.getInstance().onProtocol(this, json);
    }

    public void sendMessage(JsonObject message) {
        connector.writeTextMessage(message.encode());
    }

    public void sendErrorMessage(String errorMsg) {
        JsonObject json = new JsonObject();
        json.put("type", MessageType.SC_ERROR_CODE)
            .put("errorMsg", errorMsg);
        sendMessage(json);
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public ServerWebSocket getConnector() {
        return connector;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getCreateRoom() {
        return createRoom;
    }

    public void setCreateRoom(int createRoom) {
        this.createRoom = createRoom;
    }

    public String getColorStr() {
        return colorStr;
    }

    public void setColorStr(String colorStr) {
        this.colorStr = colorStr;
    }

    public int getRoleIndex() {
        return roleIndex;
    }

    public void setRoleIndex(int roleIndex) {
        this.roleIndex = roleIndex;
    }

    public Map<String, Integer> getResourceMap() {
        return resourceMap;
    }

    public void setResourceMap(Map<String, Integer> resourceMap) {
        this.resourceMap = resourceMap;
    }

    public Map<String, Integer> getSkillMap() {
        return skillMap;
    }

    public void setSkillMap(Map<String, Integer> skillMap) {
        this.skillMap = skillMap;
    }

    public int getRobTimes() {
        return robTimes;
    }

    public void setRobTimes(int robTimes) {
        this.robTimes = robTimes;
    }

    public void destroy() {
        if (connector.isClosed()) {
            connector.close();
        }
        if (StringUtils.isNotEmpty(this.playerId)) {
            PlayerManager.getInstance().removePlayer(this.playerId);
        }
    }

    public JsonObject buildRoleData() {
        JsonObject roleData = new JsonObject();
        roleData.put("roleIndex", roleIndex)
                .put("colorStr", colorStr)
                .put("roleName", playerName)
                .put("roleId", playerId);
        return roleData;
    }

}
