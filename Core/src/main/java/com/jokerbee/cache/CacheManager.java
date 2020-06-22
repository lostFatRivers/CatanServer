package com.jokerbee.cache;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum CacheManager {
    INSTANCE;
    private static Logger logger = LoggerFactory.getLogger("Handler");

    private RedisClient redisClient;

    public static CacheManager getInstance() {
        return INSTANCE;
    }

    public void init(JsonObject config) throws Exception {
        redisClient = new RedisClient(config.getBoolean("cluster"), config.getString("host"),
                config.getInteger("port"), config.getString("masterName"), config.getString("password"),
                config.getInteger("maxActive"), config.getLong("maxWait"), config.getInteger("timeout"));
        redisClient.startup();
    }

    public RedisClient redis() {
        return redisClient;
    }
}
