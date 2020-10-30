package com.jokerbee.gateway.player;

import io.vertx.core.Context;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 玩家对象;
 *
 * @author: Joker
 * @date: Created in 2020/10/29 16:28
 * @version: 1.0
 */
public class Player {
    private static final Logger logger = LoggerFactory.getLogger("Player");

    private final long id;
    private volatile ServerWebSocket ws;
    private final Context context;
    private volatile PlayerState state;

    public Player(long id, ServerWebSocket ws, Context context) {
        this.id = id;
        this.ws = ws;
        this.context = context;
        this.state = PlayerState.ON_LOGIN;
        registerHandler();
        logger.info("create new player:{}", id);
    }

    private void registerHandler() {
        this.ws.handler(buf -> this.context.runOnContext(v -> this.onMessage(buf)));
        JsonObject json = new JsonObject().put("id", this.id);
        this.ws.writeTextMessage(json.toString());
    }

    private void onMessage(Buffer buffer) {
        String msg = buffer.toString();
        logger.info("get client message:{}", msg);
        try {
            JsonObject obj = new JsonObject(msg);
        } catch (Exception e) {
            logger.info("handle message failed. {}", e.getMessage());
        }
    }

    public void repeatWebSocket(ServerWebSocket webSocket) {
        this.ws = webSocket;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }
}
