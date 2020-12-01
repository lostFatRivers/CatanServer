package com.jokerbee.db.entity.impl;

import com.jokerbee.db.entity.IEntity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 账号实体
 *
 * @author: Joker
 * @date: Created in 2020/11/12 16:21
 * @version: 1.0
 */
@Entity(name = "account")
public class AccountEntity implements IEntity {

    @Id
    private Long id;

    private String account;

    private String password;

    @Override
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
