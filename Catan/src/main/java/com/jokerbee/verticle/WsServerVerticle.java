package com.jokerbee.verticle;

import com.jokerbee.consts.Constants;
import com.jokerbee.player.Player;
import com.jokerbee.player.PlayerManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WsServerVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger("WsServer");

    private Map<String, Player> connectPlayers = new HashMap<>();

    @Override
    public void start() {
        createWebSocketService();
    }

    private void createWebSocketService() {
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(this::httpRequestHandler)
                .webSocketHandler(this::webSocketHandler)
                .listen(config().getInteger("port"));

        logger.info("WsServerVerticle start, port:{}", config().getInteger("port"));
    }

    private void httpRequestHandler(HttpServerRequest request) {
        logger.info("Web Socket Server Get HttpRequest: {}", request.path());
        request.response().setStatusCode(404).end("<h1>Resource Not Found.</h1>");
    }

    private void webSocketHandler(ServerWebSocket webSocket) {
        if (!webSocket.path().equals(Constants.WEB_SOCKET_PATH)) {
            webSocket.reject();
            return;
        }
        String textHandlerId = webSocket.textHandlerID();
        logger.info("websocket connect open: {}", textHandlerId);

        Player newPlayer = new Player(webSocket);
        connectPlayers.put(textHandlerId, newPlayer);

        webSocket.closeHandler(v -> {
            logger.info("websocket connect closed: {}", textHandlerId);
            connectPlayers.remove(textHandlerId);
            int createRoomId = newPlayer.getCreateRoom();
            if (createRoomId > 0) {
                vertx.eventBus().send(Constants.API_DELETE_ROOM_PRE + createRoomId, "");
            }
            int roomId = newPlayer.getRoomId();
            if (roomId > 0) {
                vertx.eventBus().send(Constants.API_EXIT_ROOM_PRE + roomId, "");
            }
            PlayerManager.getInstance().removePlayer(newPlayer);
        });
        webSocket.exceptionHandler(e -> logger.error("websocket cache exception, binary id:{}", textHandlerId, e));
    }

}
