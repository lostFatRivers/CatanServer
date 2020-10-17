package com.jokerbee.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

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

    private static final Logger LOG = LoggerFactory.getLogger("DB");

    public static void main(String[] args) {
        ClusterManager manager = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(manager)
                .setEventBusOptions(new EventBusOptions().setHost("10.0.0.159"));

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                databaseInit(vertx);
                LOG.info("vertx start ok.");
            } else {
                LOG.error("cluster vertx start error", res.cause());
            }
        });
    }

    private static void databaseInit(Vertx vertx) {
        vertx.setTimer(1000, tid -> {
            try {
                InputStream stream = DBMain.class.getResourceAsStream("/jdbc.properties");
                Properties properties = new Properties();
                properties.load(stream);
                DataSource dataSource = new HikariDataSource(new HikariConfig(properties));
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("select name from account where id=1");
                ResultSet resultSet = statement.executeQuery();
                LOG.info("mysql and hikariCP start ok:{}", resultSet);
                vertx.close();
            } catch (Exception e) {
                LOG.error("mysql start failed", e);
            }
        });
    }


}
