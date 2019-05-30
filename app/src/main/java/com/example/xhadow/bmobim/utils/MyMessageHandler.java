package com.example.xhadow.bmobim.utils;

import android.databinding.ObservableField;

import com.example.xhadow.bmobim.login.viewmodel.LoginViewModel;
import com.orhanobut.logger.Logger;

import java.nio.channels.NonWritableChannelException;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;


/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/13
 */
public class MyMessageHandler extends BmobIMMessageHandler {

    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        super.onMessageReceive(messageEvent);
    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        super.onOfflineReceive(offlineMessageEvent);
    }
}
