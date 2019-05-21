package com.example.xhadow.bmobim.im;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.xhadow.bmobim.R;
import com.example.xhadow.bmobim.base.BaseActivity;
import com.example.xhadow.bmobim.databinding.ActivityMainBinding;
import com.orhanobut.logger.Logger;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.exception.BmobException;

import static com.example.xhadow.bmobim.utils.Constants.user;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MainViewModel(binding, this);
        binding.setViewModel(viewModel);
        connect();
    }

    private void connect() {
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        Logger.d(user.getAccount());
        Logger.d(user.getObjectId());
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //连接成功
                        toastLong("连接成功！");
                    } else {
                        //连接失败
                        toastLong(e.getMessage());
                    }
                }
            });
        }
    }

    public void initView() {


//                Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
//                intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
//                startActivity(intent);
    }

}
