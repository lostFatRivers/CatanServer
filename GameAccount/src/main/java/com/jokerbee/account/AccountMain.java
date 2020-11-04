package com.jokerbee.account;

import com.jokerbee.cache.CacheManager;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 账号中心启动器;
 *
 * @author: Joker
 * @date: Created in 2020/10/31 22:10
 * @version: 1.0
 */
public class AccountMain {

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    private static final Logger logger = LoggerFactory.getLogger("Account");

    private static ConfigRetriever configRetriever;
    private static Context bootContext;

    public static void main(String[] args) {
        createClusterVertx()
                .compose(AccountMain::loadConfig)
                .compose(AccountMain::deployVerticle)
                .onSuccess(v -> {
                    logger.info("boot Account Service success");
                    addShutdownOptional(bootContext.owner());
                })
                .onFailure(tr -> {
                    logger.info("boot Account service failed.", tr);
                    bootContext.owner().close();
                });
    }

    /**
     * 创建集群模式 Vertx;
     */
    private static Future<Vertx> createClusterVertx() {
        VertxOptions options = new VertxOptions().setClusterManager(new HazelcastClusterManager())
                .setBlockedThreadCheckInterval(10000000);
        return Future.future(pro -> Vertx.clusteredVertx(options, pro));
    }

    /**
     * 加载服务器配置;
     */
    private static Future<JsonObject> loadConfig(Vertx vertx) {
        bootContext = vertx.getOrCreateContext();
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("hocon")
                .setConfig(new JsonObject().put("path", "server.conf"));
        logger.info("start load server config");
        configRetriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(store));
        return Future.future(configRetriever::getConfig);
    }

    /**
     * Verticle 部署;
     */
    private static Future<String> deployVerticle(JsonObject config) {
        configRetriever.close();
        Vertx vertx = bootContext.owner();

        logger.info("start deploy verticle");
        return Future.<Void>future(pros -> {
            try {
                CacheManager.getInstance().init(config.getJsonObject("cache"));
                pros.complete();
            } catch (Exception e) {
                pros.fail(e);
            }
        }).compose(v -> Future.future(pros -> {
            // 玩家启动
            JsonObject accountConfig = config.getJsonObject("account");
            DeploymentOptions options = new DeploymentOptions()
                    .setWorkerPoolName("Account")
                    .setWorkerPoolSize(1)
                    .setInstances(accountConfig.getInteger("instance"))
                    .setConfig(accountConfig);
            vertx.deployVerticle(AccountVerticle.class.getName(), options, pros);
        }));
    }

    private static void addShutdownOptional(Vertx vertx) {
        new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    int read = System.in.read();
                    logger.info("read console input:{}", read);
                    if (read == 10) {
                        CacheManager.getInstance().shutdown();
                        logger.info("******************************************");
                        logger.info("***                                    ***");
                        logger.info("*****            Good bye            *****");
                        logger.info("***                                    ***");
                        logger.info("******************************************");
                        vertx.close();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
