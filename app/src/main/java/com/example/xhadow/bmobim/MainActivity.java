package com.example.xhadow.bmobim;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.example.xhadow.bmobim.base.BaseActivity;
import com.example.xhadow.bmobim.databinding.ActivityMainBinding;
import com.example.xhadow.bmobim.login.model.UserBean;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import static com.example.xhadow.bmobim.Constants.user;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        connect();
    }

    private void connect() {
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //连接成功
                        toastShort("连接成功！");
                    } else {
                        //连接失败
                        toastShort(e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void initView() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                final EditText editText = new EditText(MainActivity.this);
                dialog.setTitle("添加好友")
                        .setView(editText)
                        .setCancelable(true)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String uId = editText.getText().toString();
                                dialog.dismiss();
                            }
                        })
                        .create().show();

//                Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
//                intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
//                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }
}
