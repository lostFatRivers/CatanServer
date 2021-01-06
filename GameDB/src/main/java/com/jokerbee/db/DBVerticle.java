package com.jokerbee.db;

import com.jokerbee.db.manager.DBManager;
import com.jokerbee.support.GameConstant;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;

/**
 * 数据处理verticle
 *
 * @author: Joker
 * @date: Created in 2020/11/12 15:42
 * @version: 1.0
 */
public class DBVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().consumer(GameConstant.DB_QUERY, this::doQuery);
    }

    private void doQuery(Message<JsonObject> msg) {
        JsonObject query = msg.body();
        String entityName = query.getString("entity");
        if (StringUtils.isEmpty(entityName)) {
            msg.fail(-1, "invalid params");
            return;
        }

    }
}
