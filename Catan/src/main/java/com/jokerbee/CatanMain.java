package com.jokerbee;

import com.jokerbee.handler.HandlerManager;
import com.jokerbee.template.TemplateManager;
import com.jokerbee.verticle.RoomVerticle;
import com.jokerbee.verticle.GatewayVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatanMain {
    static {
        System.setProperty("vertx.logger-delegate-factory-class-path", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    private static final Logger logger = LoggerFactory.getLogger("Console");

    private static Vertx vertx;

    public static void main(String[] args) {
        logger.info("Catan server start");
        vertx = Vertx.vertx();

        initHandlerManager(vertx);
        initTemplateManager(vertx);

        loadConfig()
            .compose(CatanMain::deployVerticle)
            .onSuccess(v -> logger.info("Catan server start success."))
            .onFailure(err -> {
                logger.error("Catan server start failed.", err);
                vertx.close();
            });
    }

    private static void initHandlerManager(Vertx vertx) {
        try {
            HandlerManager.getInstance().init(vertx);
        } catch (Exception e) {
            logger.error("HandlerManager init error.", e);
            vertx.close();
        }
    }

    private static void initTemplateManager(Vertx vertx) {
        try {
            TemplateManager.getInstance().init();
        } catch (Exception e) {
            logger.error("HandlerManager init error.", e);
            vertx.close();
        }
    }

    private static Future<JsonObject> loadConfig() {
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("hocon")
                .setConfig(new JsonObject().put("path", "catan.conf"));

        return Future.future(pros -> ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
                    .addStore(store))
                    .getConfig(pros));
    }

    /**
     * Verticle 部署;
     */
    private static Future<String> deployVerticle(JsonObject config) {
        logger.info("get server config:{}", config.encode());
        return Future.<String>future(pros -> {
            // 网关启动
            JsonObject websocketConfig = config.getJsonObject("websocket");
            DeploymentOptions options = new DeploymentOptions()
                    .setWorkerPoolName("WebSocket")
                    .setInstances(websocketConfig.getInteger("instance"))
                    .setConfig(websocketConfig);
            vertx.deployVerticle(GatewayVerticle.class.getName(), options, pros);
        }).compose(s -> Future.future(pros -> {
            // 房间启动
            JsonObject roomConfig = config.getJsonObject("room");
            DeploymentOptions options = new DeploymentOptions()
                    .setWorkerPoolName("Room")
                    .setInstances(roomConfig.getInteger("instance"))
                    .setConfig(roomConfig);
            vertx.deployVerticle(RoomVerticle.class.getName(), options, pros);
        }));
    }
}
