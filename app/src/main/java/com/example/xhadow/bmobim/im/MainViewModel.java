package com.example.xhadow.bmobim.im;

import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.xhadow.bmobim.databinding.ActivityMainBinding;
import com.example.xhadow.bmobim.im.model.FriendModel;
import com.example.xhadow.bmobim.im.model.FriendsAdapter;
import com.example.xhadow.bmobim.login.model.UserBean;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.xhadow.bmobim.utils.Constants.user;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/21
 */
public class MainViewModel implements MessageListHandler {
    private ActivityMainBinding binding;
    private MainActivity mContext;

//    public final ObservableField<String> showMsg = new ObservableField<>();

    public static StringBuilder stringBuilder = new StringBuilder();

    private List<FriendModel> mList = new ArrayList<>();
    private FriendsAdapter adapter;


    private String id;


    public MainViewModel(ActivityMainBinding binding, MainActivity mContext) {
        this.binding = binding;
        this.mContext = mContext;
        initData();
        BmobIM.getInstance().addMessageListHandler(this);
    }

    private void initData() {
        BmobQuery<UserBean> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", user.getObjectId());
        query.findObjects(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e == null) {
                    if (list.get(0).getFriendModelList() != null && list.get(0).getFriendModelList().size() != 0) {
                        mList = list.get(0).getFriendModelList();
                    }
                    initView();
                } else {

                }
            }
        });
    }

    private void initView() {
        binding.friendRv.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new FriendsAdapter(mList, mContext);
        binding.friendRv.setAdapter(adapter);
    }


    public void addFriend() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        final EditText editText = new EditText(mContext);
        dialog.setTitle("添加好友")
                .setView(editText)
                .setCancelable(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uId = editText.getText().toString();
                        //查询数据库
                        queryId(uId);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }


    private void queryId(String uId) {
        BmobQuery<UserBean> query = new BmobQuery<>();
        query.addWhereEqualTo("account", uId);
        query.findObjects(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e == null) {
                    //保存好友数据到本地
                    FriendModel newFriend = new FriendModel(list.get(0).getAccount(), list.get(0).getObjectId());
                    mList.add(newFriend);
                    adapter.notifyDataSetChanged();
                    saveFriend();

                    id = list.get(0).getObjectId();
                    mContext.toastShort(list.get(0).getObjectId());
                } else {
                    mContext.toastShort(e.getMessage());
                }
            }
        });
    }

    private void saveFriend() {
        user.setFriendModelList(mList);
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mContext.toastShort("更新成功:");
                } else {
                    mContext.toastShort("更新失败：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        Logger.d("size=" + list.size());
        for (int i = 0; i < list.size(); i++) {
            addMsg(list.get(i));
            Logger.d("i=" + i);
            Logger.d("msg=" + i);
        }
    }

    private void addMsg(MessageEvent messageEvent) {
        String msg = messageEvent.getMessage().getContent();
        stringBuilder.append(msg);
        stringBuilder.append("\n");
//        showMsg.set(stringBuilder.toString());
    }

    public void toChat() {

    }
}
