package com.example.xhadow.bmobim.im.model;

import android.databinding.ObservableField;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/21
 */
public class FriendModel {
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> id = new ObservableField<>();

    public FriendModel(String name, String id) {
        this.name.set(name);
        this.id.set(id);
    }

}
