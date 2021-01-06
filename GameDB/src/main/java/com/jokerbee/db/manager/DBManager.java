package com.jokerbee.db.manager;

import com.jokerbee.db.entity.IEntity;
import com.jokerbee.db.entity.impl.AccountEntity;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
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
     * 新增
     */
    public void createAccountTest() {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccount("joker");
        accountEntity.setPassword("4555");
        manager.persist(accountEntity);
        tx.commit();
        logger.info("create account entity finished.");
    }

    /**
     * 更新
     */
    public void updateAccount() {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        AccountEntity entity = manager.find(AccountEntity.class, 7L);
        entity.setPassword("123456");
        tx.commit();
        logger.info("update account entity finished.");
    }

    /**
     * 查询
     */
    public void queryAccount() {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        TypedQuery<AccountEntity> query = manager.createQuery("from AccountEntity where id=7", AccountEntity.class);
        List<AccountEntity> resultList = query.getResultList();
        tx.commit();
        logger.info("query account: {}.", resultList);
    }

    /**
     * 查询
     */
    @SuppressWarnings("unchecked")
    public <T extends IEntity> List<T> query(JsonObject queryInfo) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        List<T> list = new ArrayList<>();
        tx.begin();
        try {
            String entityName = queryInfo.getString("entity");
            Class<T> aClass = (Class<T>) Class.forName(entityName);
            String sql = buildSql(aClass, queryInfo);
            TypedQuery<T> query = manager.createQuery(sql, aClass);
            list = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            logger.error("query entity error,", e);
            tx.rollback();
        }
        return list;
    }

    private String buildSql(Class<?> clazz, JsonObject queryInfo) {
        String sql = "from " + clazz.getSimpleName();
        StringBuilder sqlParam = new StringBuilder();
        for (Map.Entry<String, Object> next : queryInfo) {
            String key = next.getKey();
            if (key.equals("entity")) {
                continue;
            }
            String param = " " + key + "='" + next.getValue() + "'";
            if (sqlParam.isEmpty()) {
                sqlParam.append(" where");
            } else {
                sqlParam.append(" and");
            }
            sqlParam.append(param);
        }
        sql = sql + sqlParam.toString();
        logger.info("origin sql:{}", sql);
        return sql;
    }

    public static void main(String[] args) {
        DBManager.getInstance().init();

//        DBManager.getInstance().createAccountTest();
//        DBManager.getInstance().updateAccount();
        //DBManager.getInstance().queryAccount();
        JsonObject json = new JsonObject();
        json.put("entity", AccountEntity.class.getName());
//        json.put("account", "joker");
//        json.put("password", "123456");
        List<AccountEntity> list = DBManager.getInstance().query(json);

        JsonArray jsonArray = new JsonArray();
        list.forEach(en -> jsonArray.add(JsonObject.mapFrom(en)));
        logger.info("query account:{}", jsonArray);
    }
}
