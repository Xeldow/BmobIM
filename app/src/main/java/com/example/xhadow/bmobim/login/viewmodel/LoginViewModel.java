package com.example.xhadow.bmobim.login.viewmodel;

import android.content.SharedPreferences;
import android.databinding.ObservableField;

import com.example.xhadow.bmobim.utils.Constants;
import com.example.xhadow.bmobim.im.MainActivity;
import com.example.xhadow.bmobim.databinding.ActivityLoginBinding;
import com.example.xhadow.bmobim.login.LoginActivity;
import com.example.xhadow.bmobim.login.model.UserBean;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static android.content.Context.MODE_PRIVATE;
import static com.example.xhadow.bmobim.utils.Constants.user;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/14
 */
public class LoginViewModel {
    private ActivityLoginBinding binding;
    private LoginActivity mContext;

    /**
     * 双向绑定
     */
    public final ObservableField<String> opName = new ObservableField<>();


    public LoginViewModel(ActivityLoginBinding binding, LoginActivity mContext) {
        this.binding = binding;
        this.mContext = mContext;
        opName.set("登录/注册");
        /**
         * 打开app的时候判之前是否有保留账号信息，有的话直接到主界面
         */
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", 0);
        if (sharedPreferences.getBoolean("isLogin", false)) {
            UserBean userBean = new UserBean();
            userBean.setAccount(sharedPreferences.getString("account", "null"));
            userBean.setObjectId(sharedPreferences.getString("id", "null"));
            user = userBean;
            //跳转
            mContext.startActivity_x(MainActivity.class);
            mContext.finish();
        }
    }

    public void login() {
        final String account = binding.etAccount.getText().toString();
        final String password = binding.etPassword.getText().toString();
        final String password_md5 = md5Decode(password);
                /*
                查询账号是否存在
                 */
        BmobQuery<UserBean> query = new BmobQuery<>();
        query.addWhereEqualTo("account", account);
        query.findObjects(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                //账号已注册
                if (e == null) {
                    opName.set("登录中...");
                    if (password_md5.equals(list.get(0).getPassword())) {
                        mContext.toastShort("登录成功!");
                        /*
                        实现自动登录
                         */
                        saveUser(list.get(0));
                    }
                    //密码错误
                    else {
                        mContext.toastShort("密码错误!");
                    }
                }
                //账号未注册
                else {
                    opName.set("注册中...");
                    mContext.toastShort("账号不存在,开始自动注册...");

                    final UserBean newUser = new UserBean(account, password_md5);
                    newUser.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                mContext.toastShort("自动注册成功！");
                                saveUser(newUser);
                            } else {
                                mContext.toastShort("自动注册失败：" + e.getMessage());
                            }
                        }
                    });
                }
                binding.etAccount.setText("");
                binding.etPassword.setText("");
            }
        });

    }

    /**
     * 登录成功后记住密码并跳转到主界面
     */
    private void saveUser(UserBean userBean) {
        user = userBean;
        user.setName(userBean.getAccount());
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", userBean.getAccount());
        editor.putString("id", userBean.getObjectId());
        editor.putBoolean("isLogin", true);
        editor.apply();
        //跳转
        mContext.startActivity_x(MainActivity.class);
        mContext.finish();
    }

    /**
     * 32位MD5加密
     *
     * @param content -- 待加密内容
     * @return
     */
    public String md5Decode(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
        //对生成的16字节数组进行补零操作
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
