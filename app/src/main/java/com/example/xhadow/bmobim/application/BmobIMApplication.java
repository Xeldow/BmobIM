package com.example.xhadow.bmobim.application;

import android.app.Application;

import com.example.xhadow.bmobim.utils.MyMessageHandler;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/13
 */
public class BmobIMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO 集成：1.8、初始化IM SDK，并注册消息接收器
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler());
        }
        // TODO :初始化Logger
        Logger.addLogAdapter(new AndroidLogAdapter());
        //初始化Bmob后台
        Bmob.initialize(this, "1ac3e1ba09906471f87dbe9a82bd6c65");
    }

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
