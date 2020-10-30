package com.jokerbee.gateway;

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
 * 网络入口启动器;
 *
 * @author: Joker
 * @date: Created in 2020/10/29 10:21
 * @version: 1.0
 */
public class GatewayMain {
    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    private static final Logger logger = LoggerFactory.getLogger("Gateway");

    private static ConfigRetriever configRetriever;
    private static Context bootContext;

    public static void main(String[] args) {
        createClusterVertx()
                .compose(GatewayMain::loadConfig)
                .compose(GatewayMain::deployVerticle)
                .onSuccess(v -> {
                    logger.info("boot Gateway Service success");
                    addShutdownOptional(bootContext.owner());
                })
                .onFailure(tr -> {
                    logger.info("boot gateway service failed.", tr);
                    bootContext.owner().close();
                });
    }

    /**
     * 创建集群模式 Vertx;
     */
    private static Future<Vertx> createClusterVertx() {
        VertxOptions options = new VertxOptions().setClusterManager(new HazelcastClusterManager());
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
        return Future.future(pros -> {
            // 网关启动
            JsonObject gatewayConfig = config.getJsonObject("gateway");
            DeploymentOptions options = new DeploymentOptions()
                    .setWorkerPoolName("Gateway")
                    .setWorkerPoolSize(gatewayConfig.getInteger("workPoolSize"))
                    .setInstances(gatewayConfig.getInteger("instance"))
                    .setConfig(gatewayConfig);
            vertx.deployVerticle(GatewayVerticle.class.getName(), options, pros);
        });
    }

    private static void addShutdownOptional(Vertx vertx) {
        new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    int read = System.in.read();
                    logger.info("read console input:{}", read);
                    if (read == 10) {
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
