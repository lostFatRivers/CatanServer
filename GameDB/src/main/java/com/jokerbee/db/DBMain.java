package com.jokerbee.db;

import com.jokerbee.db.manager.DBManager;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * DB服务启动器;
 *
 * @author: Joker
 * @date: Created in 2020/10/15 15:55
 * @version: 1.0
 */
public class DBMain {
    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    private static final Logger logger = LoggerFactory.getLogger("DB");

    public static void main(String[] args) {
        ClusterManager manager = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(manager);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                try {
                    DBManager.getInstance().init();
                } catch (Exception e) {
                    logger.error("db init failed.", e);
                }
                addShutdownOptional(vertx);
            } else {
                logger.error("cluster vertx start error", res.cause());
            }
        });


    }

    private static void addShutdownOptional(Vertx vertx) {
        new Thread(() -> {
            try {
                int read = System.in.read();
                logger.info("read console input:{}", read);
                if (read == 10) {
                    logger.info("******************************************");
                    logger.info("***                                    ***");
                    logger.info("*****            Good bye            *****");
                    logger.info("***                                    ***");
                    logger.info("******************************************");
                    vertx.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
