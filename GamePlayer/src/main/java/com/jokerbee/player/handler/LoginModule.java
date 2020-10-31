package com.jokerbee.player.handler;

import com.jokerbee.anno.MessageHandler;
import com.jokerbee.handler.AbstractModule;
import com.jokerbee.support.MessageCode;
import com.jokerbee.player.Player;
import com.jokerbee.player.PlayerState;
import io.vertx.core.json.JsonObject;

/**
 * 登录模块;
 *
 * @author: Joker
 * @date: Created in 2020/10/31 13:49
 * @version: 1.0
 */
public class LoginModule extends AbstractModule {

    @MessageHandler(code = MessageCode.CS_SYNC_DATA)
    private void syncData(Player player, JsonObject message) {

        logger.info("player data {}.", message);
    }

}
