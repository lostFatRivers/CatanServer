package com.jokerbee.db;

import com.jokerbee.db.manager.DBManager;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

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
        ClusterManager manager = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(manager)
                .setEventBusOptions(new EventBusOptions().setHost("10.0.0.159"));

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                try {
                    DBManager.getInstance().init(vertx);
                    testQueryRequest(vertx);
                } catch (Exception e) {
                    logger.error("db init failed.", e);
                }
            } else {
                logger.error("cluster vertx start error", res.cause());
            }
        });
    }

    private static void testQueryRequest(Vertx vertx) {
        AtomicInteger counter = new AtomicInteger(0);
        vertx.setPeriodic(500, tid -> {
            final int index = counter.incrementAndGet();
            logger.info("start query entity:{}", index);
            vertx.eventBus().request("queryEntity", "hello", res -> {
                if (res.failed()) {
                    logger.error("query entity failed. index:{}", index, res.cause());
                }
            });
        });
    }


}
