package com.jokerbee.player;

import com.jokerbee.cache.CacheManager;
import com.jokerbee.cache.RedisClient;
import com.jokerbee.support.GameConstant;
import com.jokerbee.support.MessageCode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 玩家管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/31 1:07
 * @version: 1.0
 */
public class PlayerVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("PlayerVerticle");

    private final Map<String, Player> playerMap = new HashMap<>();

    private String serverId;

    private MessageConsumer<String> createPlayerConsumer;
    private MessageConsumer<JsonObject> serverMessageConsumer;

    @Override
    public void start(Promise<Void> startPromise) {
        RedisClient redis = CacheManager.getInstance().redis();
        serverId = redis.incr(GameConstant.REDIS_SERVER_ID) + "";
        createPlayerConsumer = vertx.eventBus().consumer(GameConstant.API_CREATE_PLAYER, this::createPlayer);
        serverMessageConsumer = vertx.eventBus().consumer(GameConstant.API_SERVER_TITLE + serverId, this::onServerMessage);
        logger.info("start player service:{}", serverId);
        startPromise.complete();
    }

    private void onServerMessage(Message<JsonObject> msg) {
        JsonObject body = msg.body();
        Integer type = body.getInteger("type");
        if (type == null) {
            msg.fail(1, "invalidMessage");
            return;
        }
        int messageCode = type;
        switch (messageCode) {
            case MessageCode.AP_ACCOUNT_DISCONNECT -> playerDisconnect(body, msg);
            case MessageCode.AP_ACCOUNT_DESTROY -> playerDestroy(body, msg);
            default -> logger.warn("cannot handle message:{}", messageCode);
        }
    }

    private void playerDisconnect(JsonObject body, Message<JsonObject> msg) {
        String account = body.getString("account");
        Player player = playerMap.get(account);
        logger.info("player disconnect. account:{}.", account);
        if (player == null) {
            msg.fail(1, "account player not found.");
        } else {
            player.disconnect();
            msg.reply("");
        }
    }

    private void playerDestroy(JsonObject body, Message<JsonObject> msg) {
        String account = body.getString("account");
        Player player = playerMap.get(account);
        if (player != null) {
            player.destroy();
        }
        playerMap.remove(account);
        msg.reply("");
    }

    private void createPlayer(Message<String> msg) {
        String account = msg.body();
        Player player = new Player(account, vertx.getOrCreateContext());
        player.registerConsumer();
        playerMap.put(account, player);
        msg.reply(serverId);
    }

    private void destroyAll() {
        RedisClient redis = CacheManager.getInstance().redis();

        for (Map.Entry<String, Player> next : playerMap.entrySet()) {
            String account = next.getKey();
            redis.hdel(GameConstant.REDIS_ACCOUNT_SERVER, account);

            Player player = next.getValue();
            player.destroy();
        }
    }

    @Override
    public void stop() {
        createPlayerConsumer.unregister();
        serverMessageConsumer.unregister();
        destroyAll();
        logger.info("close player service");
    }

}
