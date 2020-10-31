package com.jokerbee.player;

import com.jokerbee.handler.HandlerManager;
import com.jokerbee.handler.IMessageConsumer;
import com.jokerbee.support.GameConstant;
import io.vertx.core.Context;
import io.vertx.core.buffer.Buffer;
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
public class Player implements IMessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger("Player");

    private final String textHandlerId;
    private final Context context;

    private String account;

    public Player(String socketTextHandlerId, Context context) {
        this.context = context;
        this.textHandlerId = socketTextHandlerId;
        context.owner().eventBus().<Buffer>consumer(socketTextHandlerId + GameConstant.API_TAIL_MESSAGE_DISPATCH, msg -> onMessage(msg.body()));
        logger.info("create new player:{}", socketTextHandlerId);
    }

    private void onMessage(Buffer buffer) {
        String msg = buffer.toString();
        try {
            JsonObject obj = new JsonObject(msg);
            HandlerManager.getInstance().onProtocol(this, obj);
        } catch (Exception e) {
            logger.info("handle message failed. {}", e.getMessage());
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTextHandlerId() {
        return textHandlerId;
    }

}
