package com.jokerbee.player;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum PlayerManager {
    INSTANCE;
    private static Logger logger = LoggerFactory.getLogger("PlayerManager");

    private ConcurrentHashMap<String, Player> playerCache = new ConcurrentHashMap<>();

    private AtomicInteger roomCounter = new AtomicInteger(100);

    public static PlayerManager getInstance() {
        return INSTANCE;
    }

    public void catchPlayer(Player player) {
        String playerId = player.getPlayerId();
        if (StringUtils.isEmpty(playerId)) {
            return;
        }
        Player oldPlayer = playerCache.get(playerId);
        if (oldPlayer != null) {
            oldPlayer.destroy();
        }
        playerCache.put(playerId, player);
    }

    public void removePlayer(String playerId) {
        playerCache.remove(playerId);
    }

    public void removePlayer(Player player) {
        if (StringUtils.isEmpty(player.getPlayerId())) {
            return;
        }
        playerCache.remove(player.getPlayerId(), player);
        logger.info("remove player:{}", player.getPlayerId());
    }

    public Player getPlayer(String playerId) {
        return playerCache.get(playerId);
    }

    public int nextRoomId() {
        return roomCounter.incrementAndGet();
    }

    public void sendToAll(JsonObject msg) {
        playerCache.forEachValue(Long.MAX_VALUE, eachPlayer -> eachPlayer.sendMessage(msg));
    }
}
