package com.jokerbee.account;

import com.jokerbee.support.GameConstant;
import com.jokerbee.support.MessageCode;
import com.jokerbee.util.TimeUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/31 1:07
 * @version: 1.0
 */
public class AccountVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("Player");

    private final Map<String, Long> connectTime = new HashMap<>();
    private final Map<String, MessageConsumer<?>> socketConsumers = new HashMap<>();
    private final Map<String, String> accountToSocket = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) {
        logger.info("start account service");
        vertx.eventBus().consumer(GameConstant.API_CONNECT_ACTIVE, this::connectActive);
        vertx.setPeriodic(5000, this::tickConnect);
        startPromise.complete();
    }

    private void tickConnect(Long tid) {
        List<String> timeoutList = new ArrayList<>();
        connectTime.forEach((key, value) -> {
            if (TimeUtil.getTime() - value > 60000) {
                timeoutList.add(key);
            }
        });
        timeoutList.forEach(eachId -> {
            vertx.eventBus().send(eachId + GameConstant.API_TAIL_SOCKET_CLOSE, "");
            logger.info("invalid connect: {}, close it.", eachId);
            connectTime.remove(eachId);
            MessageConsumer<?> consumer = socketConsumers.get(eachId);
            if (consumer != null) consumer.unregister();
        });
    }

    private void connectActive(Message<String> msg) {
        String socketTextHandlerId = msg.body();
        logger.info("new connect received: {}", socketTextHandlerId);
        connectTime.put(socketTextHandlerId, TimeUtil.getTime());
        MessageConsumer<Buffer> consumer = vertx.eventBus().consumer(socketTextHandlerId + GameConstant.API_TAIL_MESSAGE_DISPATCH, cMsg -> onMessage(socketTextHandlerId, cMsg));
        socketConsumers.put(socketTextHandlerId, consumer);
    }

    private void onMessage(String handlerId, Message<Buffer> msg) {
        Buffer body = msg.body();
        try {
            JsonObject message = body.toJsonObject();
            int messageType = message.getInteger("type");
            if (messageType == MessageCode.CS_ACCOUNT_LOGIN) {
                String account = message.getString("account");
                String password = message.getString("password");
                if (invalidAccount(account, password)) {
                    return;
                }
                String oldId = accountToSocket.get(account);
                if (oldId.equals(handlerId)) {
                    msg.reply(GameConstant.RESULT_SUCCESS);
                    connectTime.remove(handlerId);
                    MessageConsumer<?> consumer = socketConsumers.get(handlerId);
                    if (consumer != null) consumer.unregister();
                    return;
                }
                vertx.eventBus().request(GameConstant.API_DESTROY_PLAYER + oldId, account, res -> {
                    if (res.succeeded()) {
                        msg.reply(GameConstant.RESULT_SUCCESS);
                        connectTime.remove(handlerId);
                        MessageConsumer<?> consumer = socketConsumers.get(handlerId);
                        if (consumer != null) consumer.unregister();
                        JsonObject json = new JsonObject().put("account", account).put("handlerId", handlerId);
                        vertx.eventBus().request(GameConstant.API_CREATE_PLAYER, json, res2 -> {
                            if (res2.succeeded()) {
                                logger.info("create player success");
                            } else {
                                logger.info("create player error", res2.cause());
                                vertx.eventBus().send(handlerId + GameConstant.API_TAIL_SOCKET_CLOSE, "");
                            }
                        });
                    } else {
                        logger.error("destroy player error", res.cause());
                        vertx.eventBus().send(handlerId + GameConstant.API_TAIL_SOCKET_CLOSE, "");
                    }
                });
            }
            msg.fail(1, "not login message.");
        } catch (Exception e) {
            logger.error("connect {} message error.", handlerId);
            msg.fail(1, e.getMessage());
        }
    }

    public boolean invalidAccount(String account, String password) {
        return StringUtils.isEmpty(account) || StringUtils.isEmpty(password);
    }

    @Override
    public void stop() {
        logger.info("close player service");
    }
}
