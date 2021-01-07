package com.jokerbee.db.manager;

import com.jokerbee.db.entity.IEntity;
import com.jokerbee.db.entity.impl.AccountEntity;
import com.jokerbee.util.TimeUtil;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * DB数据管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/15 12:25
 * @version: 1.0
 */
public enum DBManager {
    INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger("DBManager");

    private EntityManagerFactory factory;

    public static DBManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        factory = Persistence.createEntityManagerFactory("myUnit");
    }

    /**
     * 保存
     */
    public void insert(IEntity entity) {
        voidBaseDBTask(manager -> manager.persist(entity));
    }

    /**
     * 更新
     */
    public void update(IEntity entity) {
        voidBaseDBTask(manager -> manager.merge(entity));
    }

    /**
     * 条件查询
     */
    public <T extends IEntity> List<T> query(Class<T> clazz, JsonObject queryInfo) {
        return baseDBTask(manager -> {
            String sql = buildSql(clazz, queryInfo);
            TypedQuery<T> query = manager.createQuery(sql, clazz);
            return query.getResultList();
        });
    }

    /**
     * sql语句查询
     */
    public <T extends IEntity> List<T> query(Class<T> clazz, String sql) {
        return baseDBTask(manager -> {
            TypedQuery<T> query = manager.createQuery(sql, clazz);
            return query.getResultList();
        });
    }

    private String buildSql(Class<?> clazz, JsonObject queryInfo) {
        String sql = "from " + clazz.getSimpleName();
        StringBuilder sqlParam = new StringBuilder();
        for (Map.Entry<String, Object> next : queryInfo) {
            String param = " " + next.getKey() + "='" + next.getValue() + "'";
            if (sqlParam.isEmpty()) {
                sqlParam.append(" where");
            } else {
                sqlParam.append(" and");
            }
            sqlParam.append(param);
        }
        sql = sql + sqlParam.toString();
        logger.debug("origin sql:{}", sql);
        return sql;
    }


    private <T> T baseDBTask(IDBTask<T> task) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            T result = task.execute(manager);
            tx.commit();
            return result;
        } catch (Exception e) {
            logger.error("DB task execute error.", e);
            tx.rollback();
            return null;
        } finally {
            manager.close();
        }
    }

    private void voidBaseDBTask(IDBVoidTask task) {
        baseDBTask(manager -> {
            task.execute(manager);
            return null;
        });
    }

    public static void main(String[] args) {
        DBManager.getInstance().init();

//        AccountEntity account = new AccountEntity();
//        account.setAccount("zoll");
//        account.setPassword("123321");
//        DBManager.getInstance().save(account);

        for (int i = 0; i < 5; i++) {
            long time = TimeUtil.getNanoTime();
            JsonObject json = new JsonObject().put("account", "zoll");
            List<AccountEntity> list = DBManager.getInstance().query(AccountEntity.class, json);
            logger.info("query cost time:{}", (TimeUtil.getNanoTime() - time) / 1000000.0f);
        }


    }
}
