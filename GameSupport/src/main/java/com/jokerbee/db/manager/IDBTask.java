package com.jokerbee.db.manager;


import javax.persistence.EntityManager;

/**
 * DB 任务接口;
 *
 * @author: Joker
 * @date: Created in 2021/1/7 15:46
 * @version: 1.0
 */
public interface IDBTask<T> {
    T execute(EntityManager manager) throws Exception;
}
