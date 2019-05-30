package com.example.xhadow.bmobim.login.model;

import com.example.xhadow.bmobim.im.model.FriendModel;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/14
 */

public class UserBean extends BmobObject {
    private String name;
    private String account;
    private String password;
    private List<FriendModel> friendModelList;

    public UserBean() {

    }

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


    public List<FriendModel> getFriendModelList() {
        return friendModelList;
    }

    public void setFriendModelList(List<FriendModel> friendModelList) {
        this.friendModelList = friendModelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
