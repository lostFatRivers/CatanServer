package com.jokerbee.gateway.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家对象管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/29 16:28
 * @version: 1.0
 */
public enum PlayerManager {
    INSTANCE;

    private final Map<Long, Player> players = new ConcurrentHashMap<>();

    public static PlayerManager getInstance() {
        return INSTANCE;
    }

    public void addPlayer(long id, Player player) {
        players.put(id, player);
    }

    public Player getPlayer(long id) {
        return players.get(id);
    }

    public void remove(long id) {
        players.remove(id);
    }
}
