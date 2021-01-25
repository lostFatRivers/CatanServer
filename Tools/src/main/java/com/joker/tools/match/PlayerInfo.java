package com.joker.tools.match;

import com.jokerbee.util.TimeUtil;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 玩家基础信息
 *
 * @author: Joker
 * @date: Created in 2021/1/20 11:21
 * @version: 1.0
 */
public class PlayerInfo implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger("PlayerInfo");

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(10000);

    private int id;
    private int serverId;
    private int level;
    private WeaponType type;
    private long startTime;

    private int teamId;

    private LimitType limitType = LimitType.WEAPON_AND_LEVEL;

    public PlayerInfo(JsonObject json) {
        this.id = json.getInteger("id");
        this.serverId = json.getInteger("serverId");
        this.level = json.getInteger("level");
        this.type = WeaponType.valueOf(json.getString("type"));
        this.startTime = json.getLong("startTime");
        this.teamId = json.getInteger("teamId");
        this.limitType = LimitType.valueOf(json.getString("limitType"));
    }

    public PlayerInfo(int serverId, int level, WeaponType type) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.serverId = serverId;
        this.level = level;
        this.type = type;
        this.startTime = TimeUtil.getTime();
    }

    public void checkMatchTime() {
        if (limitType == LimitType.ANY) {
            return;
        }
        // 超过20秒切换到 ANY
        long currentTime = TimeUtil.getTime();
        if (currentTime - this.startTime > 20 * TimeUtil.SECOND_MILLIS) {
            setLimitType(LimitType.ANY);
            return;
        }

        if (limitType == LimitType.LEVEL_ONLY) {
            return;
        }
        // 超过10秒切换到 LEVEL_ONLY
        if (currentTime - this.startTime > 10 * TimeUtil.SECOND_MILLIS) {
            setLimitType(LimitType.LEVEL_ONLY);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public WeaponType getType() {
        return type;
    }

    public void setType(WeaponType type) {
        this.type = type;
    }

    public LimitType getLimitType() {
        return limitType;
    }

    public void setLimitType(LimitType limitType) {
        this.limitType = limitType;
        MatchManager.getInstance().removeFromTeam(id, teamId);
        logger.info("player:{} match limit change to [{}]", id, this.limitType.name());
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("id", id).put("serverId", serverId).put("level", level).put("type", type.name())
            .put("startTime", startTime).put("teamId", teamId).put("limitType", limitType.name());
        return json;
    }

    public String simpleInfo() {
        JsonObject json = new JsonObject();
        json.put("id", id).put("level", level).put("type", type.ordinal());
        return json.toString();
    }
}
