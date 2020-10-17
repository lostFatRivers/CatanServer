package com.jokerbee.db.manager;

import com.jokerbee.db.DBMain;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * DB数据管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/15 12:25
 * @version: 1.0
 */
public enum DBManager {
    INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger("DB");

    private Vertx vertx;
    private DataSource dataSource;

    public static DBManager getInstance() {
        return INSTANCE;
    }

    public void init(Vertx vertx) throws Exception {
        this.vertx = vertx;
        createDataConnectPool();
        deployConsumers();
    }

    private void createDataConnectPool() throws Exception {
        InputStream stream = DBMain.class.getResourceAsStream("/jdbc.properties");
        Properties properties = new Properties();
        properties.load(stream);
        this.dataSource = new HikariDataSource(new HikariConfig(properties));
    }

    private void deployConsumers() {
        this.vertx.eventBus().consumer("queryEntity", this::queryEntity);
    }

    private void queryEntity(Message<String> msg) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select name from account where id=1");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                logger.info("result:{}", resultSet.getString(1));
            }
            connection.close();
            msg.reply("{}");
        } catch (Exception e) {
            msg.fail(1, e.getMessage());
        }
    }

}
