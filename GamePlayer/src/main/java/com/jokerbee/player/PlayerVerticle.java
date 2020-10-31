package com.jokerbee.player;

import com.jokerbee.support.GameConstant;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/31 1:07
 * @version: 1.0
 */
public class PlayerVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("Player");

    private final Map<String, Player> playerMap = new HashMap<>();
    private final Map<String, MessageConsumer<?>> destroyConsumers = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) {
        logger.info("start player service");
        vertx.eventBus().consumer(GameConstant.API_CREATE_PLAYER, this::createPlayer);
        startPromise.complete();
    }

    private void createPlayer(Message<JsonObject> msg) {
        JsonObject json = msg.body();
        String socketTextHandlerId = json.getString("handlerId");
        String account = json.getString("account");
        Player player = new Player(socketTextHandlerId, vertx.getOrCreateContext());
        player.setAccount(account);
        playerMap.put(socketTextHandlerId, player);

        MessageConsumer<String> consumer = vertx.eventBus().consumer(GameConstant.API_DESTROY_PLAYER + socketTextHandlerId, dMsg -> {
            String dAccount = dMsg.body();
            if (!account.equals(dAccount)) {
                logger.info("destroy player not match account:{}, {}", account, dAccount);
            }
            playerMap.remove(socketTextHandlerId);
            MessageConsumer<?> dc = destroyConsumers.remove(socketTextHandlerId);
            dc.unregister();
            vertx.eventBus().send(socketTextHandlerId + GameConstant.API_TAIL_SOCKET_CLOSE, "");
            dMsg.reply(GameConstant.RESULT_SUCCESS);
        });
        destroyConsumers.put(socketTextHandlerId, consumer);
        msg.reply(GameConstant.RESULT_SUCCESS);
    }

    @Override
    public void stop() {
        logger.info("close player service");
    }
}
