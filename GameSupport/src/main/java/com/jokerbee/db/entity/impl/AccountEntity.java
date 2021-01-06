package com.jokerbee.db.entity.impl;

import com.jokerbee.db.entity.IEntity;

import javax.persistence.*;
import java.util.StringJoiner;

/**
 * 账号实体
 *
 * @author: Joker
 * @date: Created in 2020/11/12 16:21
 * @version: 1.0
 */
@Entity
@Table(name = "account")
public class AccountEntity implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account")
    private String account;

    @Column(name = "password")
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

    @Override
    public String toString() {
        return new StringJoiner(", ", AccountEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("account='" + account + "'")
                .add("password='" + password + "'")
                .toString();
    }
}
