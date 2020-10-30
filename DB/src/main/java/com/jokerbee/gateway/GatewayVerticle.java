package com.jokerbee.gateway;

import com.jokerbee.gateway.player.Player;
import com.jokerbee.gateway.player.PlayerManager;
import com.jokerbee.gateway.player.PlayerState;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

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

    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public void start(Promise<Void> startPromise) {
        createWebSocketService()
                .onSuccess(hs -> {
                    this.httpServer = hs;
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

    private void webSocketHandler(ServerWebSocket webSocket) {
        logger.info("websocket connect: {}", webSocket.path());
        if (!webSocket.path().equals("/websocket")) {
            webSocket.reject();
            logger.info("websocket connect reject");
            return;
        }
        final String binaryHandlerId = webSocket.binaryHandlerID();
        final String textHandlerId = webSocket.textHandlerID();
        logger.info("websocket connect opened t:{}, b:{}", textHandlerId, binaryHandlerId);

        final long id = idCounter.getAndIncrement();
        PlayerManager.getInstance().addPlayer(idCounter.getAndIncrement(), new Player(id, webSocket, context));
        webSocket.closeHandler(v -> {
            logger.info("websocket connect closed t:{}, b:{}", textHandlerId, binaryHandlerId);
            Player player = PlayerManager.getInstance().getPlayer(id);
            if (player != null)
                player.setState(PlayerState.OFFLINE);
        });
        webSocket.exceptionHandler(e -> logger.error("websocket cache exception, t:{}, b:{}", textHandlerId, binaryHandlerId, e));
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
