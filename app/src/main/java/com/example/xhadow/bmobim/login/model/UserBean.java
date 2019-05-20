package com.example.xhadow.bmobim.login.model;

import cn.bmob.v3.BmobObject;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/14
 */
public class UserBean extends BmobObject {
    private String account;
    private String password;

    public UserBean(String account, String password) {
        this.account = account;
        this.password = password;
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
