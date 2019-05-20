package com.example.xhadow.bmobim.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.example.xhadow.bmobim.MainActivity;
import com.example.xhadow.bmobim.R;
import com.example.xhadow.bmobim.base.BaseActivity;
import com.example.xhadow.bmobim.databinding.ActivityLoginBinding;
import com.example.xhadow.bmobim.login.model.UserBean;
import com.example.xhadow.bmobim.login.viewmodel.LoginViewModel;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        viewModel=new LoginViewModel(binding,LoginActivity.this);
        binding.setViewmodel(viewModel);
    }


    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }




}
