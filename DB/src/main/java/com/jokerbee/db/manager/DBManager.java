package com.jokerbee.db.manager;

/**
 * DB数据管理器;
 *
 * @author: Joker
 * @date: Created in 2020/10/15 12:25
 * @version: 1.0
 */
public enum DBManager {
    INSTANCE;


    public static DBManager getInstance() {
        return INSTANCE;
    }


}
