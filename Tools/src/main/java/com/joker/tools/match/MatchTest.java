package com.joker.tools.match;

import com.jokerbee.cache.CacheManager;
import com.jokerbee.util.RandomUtil;
import com.jokerbee.util.TimeUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.TimeUnit;

/**
 * 测试匹配;
 */
public class MatchTest {

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    public static void main(String[] args) throws Exception {
        JsonObject cacheCfg = new JsonObject();
        cacheCfg.put("cluster", false).put("host", "10.0.0.159").put("port", 6379)
                .put("masterName", "mymaster").put("password", "123456").put("maxActive", 50)
                .put("maxWait", 1000).put("timeout", 1000);
        CacheManager.getInstance().init(cacheCfg);

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MatchVerticle.class.getName(), new DeploymentOptions().setInstances(1));

        TimeUnit.SECONDS.sleep(1);
        for (int i = 0; i < 100; i++) {
            int randomLevel = RandomUtil.getRandom(1, 4);
//            int randomLevel = 1;
            WeaponType[] values = WeaponType.values();
            WeaponType type = RandomUtil.randomValue(values);
            PlayerInfo info = new PlayerInfo(1, randomLevel, type);
            JsonObject json = info.toJson();
            vertx.eventBus().send(MatchVerticle.API_ADD_MATCH_PLAYER, json);
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }
}
