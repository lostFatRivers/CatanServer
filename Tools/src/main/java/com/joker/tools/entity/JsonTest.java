package com.joker.tools.entity;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对象和json测试;
 *
 * @author: Joker
 * @date: Created in 2020/11/12 17:16
 * @version: 1.0
 */
public class JsonTest {
    private static final Logger logger = LoggerFactory.getLogger("Json");

    public static void main(String[] args) {
        AccountEntity entity = new AccountEntity();
        entity.setId(1);
        entity.setAccount("account");
        entity.setPassword("123321");


        JsonObject object = JsonObject.mapFrom(entity);
        logger.info("json str:{}", object.encodePrettily());
    }
}
