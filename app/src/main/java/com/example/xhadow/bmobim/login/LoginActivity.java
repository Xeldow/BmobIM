package com.example.xhadow.bmobim.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.example.xhadow.bmobim.R;
import com.example.xhadow.bmobim.base.BaseActivity;
import com.example.xhadow.bmobim.databinding.ActivityLoginBinding;
import com.example.xhadow.bmobim.login.viewmodel.LoginViewModel;

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







}
