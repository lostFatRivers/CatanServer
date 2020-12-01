package com.joker.tools.entity;

/**
 * 账号实体
 *
 * @author: Joker
 * @date: Created in 2020/11/12 17:15
 * @version: 1.0
 */
public class AccountEntity {
    
    private long id;
    private String account;
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
