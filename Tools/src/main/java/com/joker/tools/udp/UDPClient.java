package com.joker.tools.udp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * udp 连接测试
 *
 * @author: Joker
 * @date: Created in 2020/10/20 14:29
 * @version: 1.0
 */
public class UDPClient {
    private static final Logger logger = LoggerFactory.getLogger("UDP");

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DatagramSocket datagramSocket = vertx.createDatagramSocket();
        Buffer bu = Buffer.buffer("content");
        datagramSocket.send(bu, 1234, "10.0.0.159", res -> {
            logger.info("send success: {}", res.succeeded());
        });
        datagramSocket.handler(pack -> {
            logger.info("client receive msg:{}", pack.data().toString());
        });
    }
}
