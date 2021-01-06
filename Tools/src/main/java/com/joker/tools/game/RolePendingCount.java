package com.joker.tools.game;

import cn.hutool.core.io.file.FileReader;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 解析日志, 统计pending role断开的数量.
 *
 * @author: Joker
 * @date: Created in 2020/12/27 23:28
 * @version: 1.0
 */
public class RolePendingCount {
    private static final Logger logger = LoggerFactory.getLogger("Role");

    private static final Map<Integer, Map<String, List<JsonObject>>> tellClientMap = new HashMap<>();
    private static final List<RoleInfo> infoList = new ArrayList<>();

    private static final String path = "E:/ProgramData/onlineLogs";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateFormat RES_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        logger.info("start load log files.");

        loadTellClientLog("/lg_1.log");
        loadTellClientLog("/lg_2.log");
        loadTellClientLog("/lg_3.log");

        logger.info("tell client map size:{}", tellClientMap.size());

        parsePendingRole("/bt_1.log");
        parsePendingRole("/bt_2.log");

        logger.info("role info map size:{}", infoList.size());

        sortTimeNotEnterRole();
    }

    private static void loadTellClientLog(String fileName) {
        String matchStr = "进入场景中通知客户端IP端口已经场景ID";

        FileReader reader = new FileReader(path + fileName);
        List<String> strings = reader.readLines();
        strings.stream().filter(es -> es.contains(matchStr)).map(JsonObject::new).forEach(ej -> {
            String areaIdStr = ej.getString("area_id");
            if (StringUtils.isEmpty(areaIdStr)) {
                return;
            }
            int areaId = Integer.parseInt(areaIdStr);
            if (!tellClientMap.containsKey(areaId)) {
                tellClientMap.put(areaId, new HashMap<>());
            }
            Map<String, List<JsonObject>> listMap = tellClientMap.get(areaId);
            String timeStr = ej.getString("time");
            String[] split = timeStr.split("\\.");
            String secondTime = split[0];
            if (!listMap.containsKey(secondTime)) {
                listMap.put(secondTime, new ArrayList<>());
            }
            listMap.get(secondTime).add(ej);
        });
    }

    private static void parsePendingRole(String fileName) {
        String pendingMatchStr = "[PendingEnterScene e]";
        String clientEnterSceneMatchStr = "enter scene";
        String enterOverMatchStr = "[EnterScene]over";
        FileReader reader = new FileReader(path + fileName);
        List<String> strings = reader.readLines();
        List<String> validLines = new ArrayList<>();
        strings.stream().filter(es -> es.contains(pendingMatchStr) || es.contains(clientEnterSceneMatchStr) || es.contains(enterOverMatchStr)).forEach(validLines::add);
        logger.info("valid line size:{}", validLines.size());

        for (int i = 0; i < validLines.size(); i++) {
            String eachLine = validLines.get(i);
            JsonObject eachJ = new JsonObject(eachLine);
            try {
                String log = eachJ.getString("log");
                String timeStr = eachJ.getString("time");
                String secondStr = timeStr.split("\\.")[0];

                if (log.contains(pendingMatchStr)) {
                    String[] split = log.split(",");
                    String pidSplit = split[0];
                    String playerIdStr = pidSplit.split(":")[1];
                    long playerId = Long.parseLong(playerIdStr);

                    Date date = DATE_FORMAT.parse(timeStr);

                    String sceneIdStr = split[4].split(" ")[0].split(":")[1];
                    long sceneId = Long.parseLong(sceneIdStr);

                    String serverIdStr = split[4].split(" ")[1].split(":")[1];
                    int serverId = Integer.parseInt(serverIdStr);

                    RoleInfo roleInfo = new RoleInfo();
                    roleInfo.setPlayerId(playerId);
                    roleInfo.setPendingTime(date.getTime());
                    roleInfo.setSceneId(sceneId);

                    infoList.add(roleInfo);

                    if (!hasTellClient(serverId, secondStr, playerIdStr, roleInfo)) {
                        continue;
                    }
                    for (int j = 1; j < 100; j++) {
                        if (i + j >= validLines.size()) {
                            break;
                        }
                        String nextLine = validLines.get(i + j);
                        if (!nextLine.contains(clientEnterSceneMatchStr) || !nextLine.contains(playerIdStr) || !nextLine.contains(sceneIdStr)) {
                            continue;
                        }
                        JsonObject nextJ = new JsonObject(nextLine);
                        String nextTime = nextJ.getString("time");
                        Date nextDate = DATE_FORMAT.parse(nextTime);
                        roleInfo.setClientEnterSceneTime(nextDate.getTime());
                        for (int k = 1; k < 10; k++) {
                            if (i + j + k >= validLines.size()) {
                                break;
                            }
                            String lastLine = validLines.get(i + j + k);
                            if (!lastLine.contains(enterOverMatchStr) || !lastLine.contains(playerIdStr) || !lastLine.contains(sceneIdStr)) {
                                continue;
                            }
                            JsonObject lastJ = new JsonObject(lastLine);
                            String lastTime = lastJ.getString("time");
                            Date lastDate = DATE_FORMAT.parse(lastTime);
                            roleInfo.setEnterSceneOverTime(lastDate.getTime());
                            break;
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("parse pending role log error.", e);
            }
        }
    }

    private static boolean hasTellClient(int sceneId, String secondStr, String playerIdStr, RoleInfo roleInfo) throws Exception {
        if (!tellClientMap.containsKey(sceneId)) {
            return false;
        }
        Map<String, List<JsonObject>> listMap = tellClientMap.get(sceneId);
        if (!listMap.containsKey(secondStr)) {
            return false;
        }
        List<JsonObject> list = listMap.get(secondStr);
        for (JsonObject json : list) {
            String logicLog = json.getString("log");
            if (logicLog.contains(playerIdStr)) {
                String timeStr = json.getString("time");
                Date date = DATE_FORMAT.parse(timeStr);
                roleInfo.setSendClientSceneTime(date.getTime());
                return true;
            }
        }
        return false;
    }

    private static void sortTimeNotEnterRole() {
        Map<Long, List<RoleInfo>> timedRoles = new TreeMap<>();
        for (RoleInfo eachRole : infoList) {
            if (eachRole.clientEnterSceneTime > 0) {
                continue;
            }
            long timeKey = eachRole.pendingTime / (100 * 1000);
            if (!timedRoles.containsKey(timeKey)) {
                timedRoles.put(timeKey, new ArrayList<>());
            }
            timedRoles.get(timeKey).add(eachRole);
        }
        for (Map.Entry<Long, List<RoleInfo>> eachEntry : timedRoles.entrySet()) {
            Long timeKey = eachEntry.getKey();
            long time = timeKey * 100 * 1000;
            logger.info("time:{}, timeFormat:{}, roleSize:{}", time, RES_DATE_FORMAT.format(time), eachEntry.getValue().size());
        }
    }

    /**
     * 玩家信息
     */
    static class RoleInfo {
        private long playerId;
        private long pendingTime;
        private long sceneId;
        private long sendClientSceneTime;
        private long clientEnterSceneTime;
        private long enterSceneOverTime;

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }

        public long getPendingTime() {
            return pendingTime;
        }

        public void setPendingTime(long pendingTime) {
            this.pendingTime = pendingTime;
        }

        public long getSceneId() {
            return sceneId;
        }

        public void setSceneId(long sceneId) {
            this.sceneId = sceneId;
        }

        public long getSendClientSceneTime() {
            return sendClientSceneTime;
        }

        public void setSendClientSceneTime(long sendClientSceneTime) {
            this.sendClientSceneTime = sendClientSceneTime;
        }

        public long getClientEnterSceneTime() {
            return clientEnterSceneTime;
        }

        public void setClientEnterSceneTime(long clientEnterSceneTime) {
            this.clientEnterSceneTime = clientEnterSceneTime;
        }

        public long getEnterSceneOverTime() {
            return enterSceneOverTime;
        }

        public void setEnterSceneOverTime(long enterSceneOverTime) {
            this.enterSceneOverTime = enterSceneOverTime;
        }
    }
}
