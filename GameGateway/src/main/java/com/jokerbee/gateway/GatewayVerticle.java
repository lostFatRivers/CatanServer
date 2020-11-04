package com.jokerbee.gateway;

import com.jokerbee.support.GameConstant;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网关;
 *
 * @author: Joker
 * @date: Created in 2020/10/29 10:45
 * @version: 1.0
 */
public class GatewayVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("Gateway");

    private HttpServer httpServer;

    @Override
    public void start(Promise<Void> startPromise) {
        createWebSocketService()
                .onSuccess(hs -> {
                    this.httpServer = hs;
                    registerConsumer();
                    addShutdownHook();
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    private Future<HttpServer> createWebSocketService() {
        return Future.future(pro -> {
            HttpServer httpServer = vertx.createHttpServer();
            httpServer.requestHandler(this::httpRequestHandler)
                    .webSocketHandler(this::webSocketHandler)
                    .connectionHandler(this::httpConnection)
                    .listen(config().getInteger("port"), pro);

            logger.info("Gateway service started, listening port:{}", config().getInteger("port"));
        });
    }

    private void httpRequestHandler(HttpServerRequest request) {
        logger.info("Web Socket Server Get HttpRequest: {}", request.path());
        request.response().setStatusCode(404).end("<h1>Resource Not Found.</h1>");
    }

    private void webSocketHandler(final ServerWebSocket webSocket) {
        if (!webSocket.path().equals("/websocket")) {
            webSocket.reject();
            logger.info("websocket connect reject");
            return;
        }
        logger.info("websocket connect: {}, remote:{}", webSocket.path(), webSocket.remoteAddress());
        GatewayConnector connector = new GatewayConnector(vertx, webSocket);
        ConnectorManager.getInstance().addConnector(connector);
    }

    private void registerConsumer() {
        vertx.eventBus().consumer(GameConstant.API_SOCKET_CLOSE, this::disconnectWebSocket);
    }

    private void disconnectWebSocket(Message<String> msg) {
        String handlerId = msg.body();
        GatewayConnector connector = ConnectorManager.getInstance().getConnector(handlerId);
        if (connector == null) return;
        connector.close();
    }

    private void dispatchMessage(AsyncResult<Message<Buffer>> res) {
        if (res.succeeded()) {
            logger.info("message dispatch success.");
        } else {
            logger.error("message dispatch failed.", res.cause());
        }
    }

    private void httpConnection(HttpConnection connection) {
        logger.info("get http connection: {}", connection.remoteAddress());
    }

    private void addShutdownHook() {
        context.addCloseHook(h -> {
            httpServer.close(h);
            logger.info("close gateway service");
        });
    }
}
