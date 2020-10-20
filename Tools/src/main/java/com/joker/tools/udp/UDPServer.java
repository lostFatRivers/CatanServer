package com.joker.tools.udp;

import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * udp 连接测试
 *
 * @author: Joker
 * @date: Created in 2020/10/20 14:34
 * @version: 1.0
 */
public class UDPServer {
    private static final Logger logger = LoggerFactory.getLogger("UDP");

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DatagramSocket socket = vertx.createDatagramSocket();
        socket.listen(1234, "0.0.0.0", res -> {
            if (res.succeeded()) {
                socket.handler(pack -> {
                    String msg = pack.data().toString();
                    logger.info("UDP Server receive msg:{}", msg);
                    logger.info("UDP sender:{}", pack.sender());
                });
            } else {
                logger.error("listen failed.", res.cause());
            }
        });
    }
}
