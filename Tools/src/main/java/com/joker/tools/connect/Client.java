package com.joker.tools.connect;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.NetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 连接测试: client
 *
 * @author: Joker
 * @date: Created in 2020/10/20 11:37
 * @version: 1.0
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger("Console");

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        for (int i = 0; i < 2; i++) {
            startConnect(vertx, 5000);
            TimeUnit.SECONDS.sleep(10);
        }
    }

    private static void startConnect(Vertx vertx, int num) {
        for (int i = 0; i < num; i++) {
            HttpClient httpClient = vertx.createHttpClient();
            httpClient
                    .webSocket(9008, "192.168.203.128", "/", res -> {
                        if (res.succeeded()) {
                            logger.info("connect server ok.");
                        } else {
                            logger.error("connect failed.", res.cause());
                        }
                    });
            Future<HttpClientRequest> request = httpClient.request(HttpMethod.GET, 8080, "10.0.0.159", "/item");

            NetClient netClient = vertx.createNetClient();

        }
    }
}
