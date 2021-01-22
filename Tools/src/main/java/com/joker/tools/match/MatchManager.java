package com.joker.tools.match;

import com.jokerbee.cache.CacheManager;
import com.jokerbee.cache.RedisClient;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 匹配管理器
 *
 * @author: Joker
 * @date: Created in 2021/1/20 11:20
 * @version: 1.0
 */
public enum MatchManager {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger("MatchManager");

    private static final String REDIS_MATCH_PLAYER = "match_player";

    private final Map<Integer, PlayerInfo> infoMap = new HashMap<>();

    private final Map<Integer, MatchTeam> teamMap = new HashMap<>();

    public static MatchManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        RedisClient redis = CacheManager.getInstance().redis();
        redis.del(REDIS_MATCH_PLAYER);
        Map<String, String> map = redis.hgetAll(REDIS_MATCH_PLAYER);
        map.forEach((idStr, infoStr) -> {
            logger.info("init match manager, player:{}", infoStr);
            int playerId = Integer.parseInt(idStr);
            JsonObject json = new JsonObject(infoStr);
            PlayerInfo playerInfo = new PlayerInfo(json);
            playerInfo.setTeamId(0);
            infoMap.put(playerId, playerInfo);
        });
    }

    /**
     * 100ms tick
     */
    public void tick() {
        for (PlayerInfo eachPlayer : infoMap.values()) {
            eachPlayer.checkMatchTime();
        }
        playerTeamMatch();
    }

    public void addMatch(PlayerInfo player) {
        if (infoMap.containsKey(player.getId())) {
            PlayerInfo info = infoMap.get(player.getId());
            if (info.getTeamId() > 0 && info.getTeamId() != player.getTeamId()) {
                removeFromTeam(player.getId(), info.getTeamId());
            }
        }
        infoMap.put(player.getId(), player);
        RedisClient redis = CacheManager.getInstance().redis();
        JsonObject json = player.toJson();
        redis.hset(REDIS_MATCH_PLAYER, player.getId() + "", json.toString());

        logger.info("======== match player number:{}, team number:{}", infoMap.size(), teamMap.size());
    }

    public void removeMatch(int playerId) {
        PlayerInfo info = infoMap.remove(playerId);
        if (info != null) {
            this.removeFromTeam(playerId, info.getTeamId());
        }
        RedisClient redis = CacheManager.getInstance().redis();
        redis.hdel(REDIS_MATCH_PLAYER, playerId + "");
    }

    public void removeFromTeam(int playerId, int teamId) {
        if (teamId <= 0) {
            return;
        }
        MatchTeam team = teamMap.get(teamId);
        if (team == null) {
            return;
        }
        logger.info("remove team player, teamId:{}, playerId:{}", teamId, playerId);
        team.removePlayer(playerId);
        if (team.isEmpty()) {
            teamMap.remove(teamId);
            logger.info("team is empty, remove teamId:{}", teamId);
        }
    }

    private void playerTeamMatch() {
        out:
        for (PlayerInfo eachPlayer : infoMap.values()) {
            if (eachPlayer.getTeamId() > 0) {
                continue;
            }
            for (MatchTeam eachTeam : teamMap.values()) {
                if (eachTeam.checkAndAddPlayer(eachPlayer)) {
                    logger.info("player join team, playerId:{}, teamId:{}, current team member:{}", eachPlayer.getId(), eachTeam.getTeamId(), eachTeam.getMemberIds());
                    break out;
                }
            }
            MatchTeam newTeam = new MatchTeam(eachPlayer.getLimitType());
            newTeam.checkAndAddPlayer(eachPlayer);
            logger.info("new match team, playerId:{}, teamId:{}", eachPlayer.getId(), newTeam.getTeamId());
            teamMap.put(newTeam.getTeamId(), newTeam);
        }

        Iterator<Map.Entry<Integer, MatchTeam>> iterator = teamMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, MatchTeam> next = iterator.next();
            MatchTeam team = next.getValue();
            if (team.isNotFull()) {
                continue;
            }
            logger.info("team matched success, teamId:{}", team.getTeamId());
            List<PlayerInfo> members = team.getMembers();
            for (PlayerInfo eachInfo : members) {
                infoMap.remove(eachInfo.getId());
            }
            iterator.remove();
        }
    }

}
