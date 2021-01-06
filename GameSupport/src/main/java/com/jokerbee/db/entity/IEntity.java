package com.jokerbee.db.entity;

import java.io.Serializable;

/**
 * 数据实体类;
 *
 * @author: Joker
 * @date: Created in 2020/10/15 12:26
 * @version: 1.0
 */
public interface IEntity extends Serializable {
    /**
     * 表id;
     *
     * @return
     *      long类型id
     */
    long getId();
}
