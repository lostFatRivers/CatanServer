package com.jokerbee.gateway;

import com.jokerbee.support.GameConstant;
import com.jokerbee.util.TimeUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Map<ServerWebSocket, Long> connectTime = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) {
        createWebSocketService()
                .onSuccess(hs -> {
                    this.httpServer = hs;
                    vertx.setPeriodic(5000, this::tickConnect);
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
        connectTime.put(webSocket, TimeUtil.getTime());

        final String textHandlerId = webSocket.textHandlerID();
        vertx.eventBus().send(GameConstant.API_CONNECT_ACTIVE, textHandlerId);

        MessageConsumer<Object> consumer = vertx.eventBus().consumer(textHandlerId + GameConstant.API_TAIL_SOCKET_CLOSE, msg -> {
            webSocket.close();
            connectTime.remove(webSocket);
        });
        webSocket.handler(buf -> vertx.eventBus().send(textHandlerId + GameConstant.API_TAIL_MESSAGE_DISPATCH, buf));
        webSocket.closeHandler(v -> {
            logger.info("websocket closed:{}", textHandlerId);
            consumer.unregister();
        });
        webSocket.exceptionHandler(e -> logger.error("websocket cache exception:{}.", textHandlerId, e));
    }

    private void tickConnect(Long tid) {
        List<ServerWebSocket> timeoutList = new ArrayList<>();
        connectTime.forEach((key, value) -> {
            if (TimeUtil.getTime() - value > 60000) {
                timeoutList.add(key);
            }
        });
        timeoutList.forEach(each -> {
            each.close();
            connectTime.remove(each);
        });
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
