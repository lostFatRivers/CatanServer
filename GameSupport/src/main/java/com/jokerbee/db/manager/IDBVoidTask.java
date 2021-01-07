package com.jokerbee.db.manager;

import javax.persistence.EntityManager;

/**
 * 无返回值 DB 任务接口;
 *
 * @author: Joker
 * @date: Created in 2021/1/7 16:11
 * @version: 1.0
 */
public interface IDBVoidTask {
    void execute(EntityManager manager) throws Exception;
}
