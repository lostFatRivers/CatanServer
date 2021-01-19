package com.jokerbee.player;

import com.jokerbee.handler.HandlerManager;
import com.jokerbee.handler.IMessageConsumer;
import com.jokerbee.support.GameConstant;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
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

    private String handlerId;

    private final Context context;

    private final Vertx vertx;

    private final String account;

    private final List<MessageConsumer<?>> consumers = new ArrayList<>();

    public Player(String account, Context context, Vertx vertx) {
        this.account = account;
        this.context = context;
        this.vertx = vertx;
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
        logger.info("player handlerId swap. account:{}, handlerId:{}", account, handlerId);
        if (StringUtils.isNotEmpty(this.handlerId)) {
            logger.warn("old handlerId:{} not disconnect", this.handlerId);
        }
        this.handlerId = handlerId;
        message.reply(GameConstant.RESULT_SUCCESS);

        vertx.setTimer(1000, tid -> {
            JsonObject msg = new JsonObject();
            msg.put("result", "success");
            sendMessage(msg);
        });
    }

    @Override
    public Context getContext() {
        return context;
    }

    public String getAccount() {
        return account;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void sendMessage(JsonObject message) {
        this.vertx.eventBus().request(this.handlerId + GameConstant.API_TAIL_MESSAGE_CLIENT, message.toString())
            .onFailure(err -> logger.error("player:{} send message:{} failed.", account, message, err));
    }

    public void disconnect() {
        if (StringUtils.isEmpty(handlerId)) {
            return;
        }
        this.context.owner().eventBus().publish(GameConstant.API_SOCKET_CLOSE, handlerId);
        handlerId = null;
    }

    public void destroy() {
        logger.info("destroy player:{}, handlerId:{}", account, handlerId);
        disconnect();
        consumers.forEach(MessageConsumer::unregister);
    }
}
