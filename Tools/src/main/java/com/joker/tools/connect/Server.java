package com.joker.tools.connect;

import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 连接测试: server
 *
 * @author: Joker
 * @date: Created in 2020/10/20 11:37
 * @version: 1.0
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger("Console");

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.createHttpServer()
                .webSocketHandler(Server::onConnected)
                .listen(9008, "0.0.0.0", res -> {
                    if (res.succeeded()) {
                        logger.info("Server started");
                    } else {
                        logger.info("Server start failed");
                    }
                });

        vertx.setPeriodic(2000, pid -> logger.info("{} client connected server", counter.get()));
    }

    private static void onConnected(ServerWebSocket webSocket) {
        counter.incrementAndGet();
        webSocket.closeHandler(v -> counter.decrementAndGet());
    }
}
