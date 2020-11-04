package com.jokerbee.player;

import com.jokerbee.handler.HandlerManager;
import com.jokerbee.handler.IMessageConsumer;
import com.jokerbee.support.GameConstant;
import io.vertx.core.Context;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家对象;
 *
 * @author: Joker
 * @date: Created in 2020/10/29 16:28
 * @version: 1.0
 */
public class Player implements IMessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger("Player");

    private String textHandlerId;

    private final Context context;

    private final String account;

    private final List<MessageConsumer<?>> consumers = new ArrayList<>();

    public Player(String account, Context context) {
        this.account = account;
        this.context = context;
        logger.info("create new player:{}", context);
    }

    public void registerConsumer() {
        MessageConsumer<Buffer> consumer = this.context.owner().eventBus().consumer(account + GameConstant.API_TAIL_MESSAGE_DISPATCH, this::onMessage);
        consumers.add(consumer);
        MessageConsumer<String> consumer1 = this.context.owner().eventBus().consumer(account + GameConstant.API_TAIL_SOCKET_SWAP, this::socketSwap);
        consumers.add(consumer1);
    }

    private void onMessage(Message<Buffer> message) {
        String msg = message.body().toString();
        try {
            logger.info("handle message {}",  msg);
            JsonObject obj = new JsonObject(msg);
            HandlerManager.getInstance().onProtocol(this, obj);
        } catch (Exception e) {
            logger.error("handle message failed. {}, msg:{}", e.getMessage(), msg);
        }
    }

    private void socketSwap(Message<String> message) {
        String handlerId = message.body();
        logger.info("player textHandlerId swap. account:{}, handlerId:{}", account, handlerId);
        if (StringUtils.isNotEmpty(this.textHandlerId)) {
            logger.warn("old textHandlerId:{} not disconnect", this.textHandlerId);
        }
        this.textHandlerId = handlerId;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public String getAccount() {
        return account;
    }

    public String getTextHandlerId() {
        return textHandlerId;
    }

    public void disconnect() {
        if (StringUtils.isEmpty(textHandlerId)) {
            return;
        }
        this.context.owner().eventBus().publish(GameConstant.API_SOCKET_CLOSE, textHandlerId);
        textHandlerId = null;
    }

    public void destroy() {
        disconnect();
        consumers.forEach(MessageConsumer::unregister);
    }
}
