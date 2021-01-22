package com.joker.tools.match;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 玩家匹配线程; <br/>
 * 玩家进入匹配, 随着匹配时间增加, 放宽匹配限制, 当匹配条件变化时, 玩家将会退出原来的房间, 重新选择匹配条件相同的房间.<br/>
 * 匹配条件: 同等级 + 不同武器类型  ==> 仅同等级  ==> 无限制
 *
 * @author: Joker
 * @date: Created in 2021/1/20 14:35
 * @version: 1.0
 */
public class MatchVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("Match");

    public static final String API_ADD_MATCH_PLAYER = "add_match_player";
    public static final String API_REMOVE_MATCH_PLAYER = "remove_match_player";

    @Override
    public void start() throws Exception {
        MatchManager.getInstance().init();
        vertx.setPeriodic(100, pid -> MatchManager.getInstance().tick());

        vertx.eventBus().consumer(API_ADD_MATCH_PLAYER, this::onAddMatchPlayer);
        vertx.eventBus().consumer(API_REMOVE_MATCH_PLAYER, this::onRemoveMatchPlayer);

        logger.info("-------- deploy MatchVerticle success. --------");
    }

    private void onAddMatchPlayer(Message<JsonObject> message) {
        JsonObject json = message.body();
        PlayerInfo playerInfo = new PlayerInfo(json);
        MatchManager.getInstance().addMatch(playerInfo);
        logger.info("add player match:{}", json.toString());
    }

    private void onRemoveMatchPlayer(Message<Integer> message) {
        int playerId = message.body();
        MatchManager.getInstance().removeMatch(playerId);
        logger.info("remove player match:{}", playerId);
    }
}
