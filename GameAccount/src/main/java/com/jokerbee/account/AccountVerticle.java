package com.jokerbee.account;

import com.jokerbee.support.GameConstant;
import com.jokerbee.support.MessageCode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 玩家管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/31 1:07
 * @version: 1.0
 */
public class AccountVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("Account");

    private final Map<String, String> accountServerIds = new HashMap<>();

    private final Set<String> lockedAccount = new HashSet<>();

    @Override
    public void start(Promise<Void> startPromise) {
        logger.info("start account service");
        vertx.eventBus().consumer(GameConstant.API_ACCOUNT_BIND, this::accountBind);
        vertx.eventBus().consumer(GameConstant.API_ACCOUNT_UNBIND, this::accountUnbind);
        startPromise.complete();
    }

    /**
     * 绑定账号和Player;
     */
    private void accountBind(Message<JsonObject> msg) {
        JsonObject body = msg.body();
        String account = body.getString("account");
        String password = body.getString("password");
        if (invalidAccount(account, password)) {
            msg.fail(1, "invalidAccount");
        }
        logger.info("account bind start:{}", account);
        if (lockedAccount.contains(account)) {
            msg.fail(1, "already in bind");
            return;
        }
        lockedAccount.add(account);
        String serverId = accountServerIds.get(account);
        if (serverId == null) {
            logger.info("create account player:{}", account);
            vertx.eventBus().<String>request(GameConstant.API_CREATE_PLAYER, account, res -> onCreatePlayer(account, res, msg));
        } else {
            tellAccountDisconnect(account, serverId, msg);
        }
        vertx.setTimer(5000, tid -> {
            if (lockedAccount.contains(account)) {
                logger.warn("account lock timeout:{}", account);
                lockedAccount.remove(account);
            }
        });
    }

    /**
     * 解绑账号和Player, 会销毁Player对象;
     */
    private void accountUnbind(Message<String> msg) {
        String account = msg.body();
        String serverId = accountServerIds.get(account);
        if (serverId == null) {
            msg.reply("");
        } else {
            JsonObject serverMsg = new JsonObject().put("type", MessageCode.AP_ACCOUNT_DESTROY).put("account", account);
            vertx.eventBus().<String>request(GameConstant.API_SERVER_TITLE + serverId, serverMsg, res -> {
                if (res.succeeded()) {
                    msg.reply("");
                    accountServerIds.remove(account);
                } else {
                    logger.error("account unbind failed.", res.cause());
                    msg.fail(1, res.cause().getMessage());
                }
            });
        }
    }

    private void onCreatePlayer(String account, AsyncResult<Message<String>> res, Message<JsonObject> msg) {
        if (res.succeeded()) {
            String serverId = res.result().body();
            accountServerIds.put(account, serverId);
            msg.reply(serverId);
            logger.info("create account player success:{}, server:{}", account, serverId);
        } else {
            msg.fail(1, res.cause().getMessage());
            logger.info("create account player failed:{}.", account, res.cause());
        }
        lockedAccount.remove(account);
    }

    private void tellAccountDisconnect(String account, String serverId, Message<JsonObject> msg) {
        logger.info("account player disconnect:{}", account);
        JsonObject serverMsg = new JsonObject().put("type", MessageCode.AP_ACCOUNT_DISCONNECT).put("account", account);
        vertx.eventBus().<String>request(GameConstant.API_SERVER_TITLE + serverId, serverMsg, res -> {
            if (res.succeeded()) {
                msg.reply(serverId);
                logger.info("account player disconnect success:{}", account);
            } else {
                logger.error("account player disconnect failed:{}", account, res.cause());
                msg.fail(1, res.cause().getMessage());
            }
            lockedAccount.remove(account);
        });
    }

    private boolean invalidAccount(String account, String password) {
        return StringUtils.isEmpty(account) || StringUtils.isEmpty(password);
    }

    @Override
    public void stop() {
        logger.info("close player service");
    }
}
