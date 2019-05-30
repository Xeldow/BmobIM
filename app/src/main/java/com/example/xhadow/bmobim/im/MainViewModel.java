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
import com.example.xhadow.bmobim.download.DownLoadActivity;
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


    private List<FriendModel> mList = new ArrayList<>();
    private FriendsAdapter adapter;

    public enum TYPE {SEND, RECEIVE}


    private String fId;
    /**
     * 用于发送消息开启对话
     */
    private BmobIMConversation mBmobIMConversation;


    public MainViewModel(ActivityMainBinding binding, MainActivity mContext) {
        this.binding = binding;
        this.mContext = mContext;
        initData();
        BmobIM.getInstance().addMessageListHandler(this);
    }

    /**
     * 初始化好友列表
     */
    private void initData() {
        BmobQuery<UserBean> query = new BmobQuery<>();
        //通过id找好友列表
        query.addWhereEqualTo("objectId", user.getObjectId());
        query.findObjects(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e == null) {
                    if (list.get(0).getFriendModelList() != null
                            && list.get(0).getFriendModelList().size() != 0) {
                        mList = list.get(0).getFriendModelList();
                    }
                    initView();
                } else {
                    mContext.toastShort("请先添加好友");
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
                        String fAccount = editText.getText().toString();
                        //查询数据库
                        queryId(fAccount, TYPE.SEND);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    public void toDownLoad() {
        mContext.startActivity_x(DownLoadActivity.class);
    }


    private void queryId(final String fAccount, final Enum type) {
        BmobQuery<UserBean> query = new BmobQuery<>();
        //通过账号找到对应的id
        query.addWhereEqualTo("account", fAccount);
        query.findObjects(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e == null) {
                    //保存好友数据库
                    fId = list.get(0).getObjectId();
                    if (type == TYPE.SEND) {
                        //把自己的账号发过去,此时接收者是被添加的好友
                        sendMsg(user.getAccount() + "Add", fId, fAccount);
                    }
                    mContext.toastShort(fId);
                } else {
                    mContext.toastShort(e.getMessage());
                }
            }
        });
    }

    public void sendMsg(final String message, String fId, String fAccount) {
        BmobIMUserInfo info = new BmobIMUserInfo();
        info.setAvatar("填写接收者的头像");
        info.setUserId(fId);
        info.setName(fAccount);
        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if (e == null) {
                    //在此跳转到聊天页面或者直接转化
                    mBmobIMConversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
                    BmobIMTextMessage msg = new BmobIMTextMessage();
                    msg.setContent(message);
                    mBmobIMConversation.sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {
                            if (e == null) {
                                mContext.toastShort("请求成功!");
                            } else {
                                mContext.toastShort("error!");
                            }
                        }
                    });
                } else {
                    mContext.toastShort("开启会话出错");
                }
            }
        });
    }

    @Override
    public void onMessageReceive(final List<MessageEvent> list) {
        Logger.d("size=" + list.size());
        for (int i = 0; i < list.size(); i++) {
            final String m = list.get(i).getMessage().getContent();
            //这个是被添加方收到的
            if (m.contains("Add")) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("好友请求")
                        .setMessage(m.replace("Add", "") + "希望成为你的好友")
                        .setNegativeButton("拒绝", null)
                        .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //同意之后把自己的账号再发回去,同时把新好友加入列表
                                saveFriend(list, m.replace("Add", ""));
                                sendMsg("AOK" + user.getAccount()
                                        , list.get(0).getFromUserInfo().getUserId()
                                        , m.replace("Add", ""));
                            }
                        });
                dialog.show();
            } else if (m.contains("AOK")) {
                saveFriend(list, m.replace("AOK", ""));
            }
            Logger.d("i=" + i);
            Logger.d("msg=" + i);
        }
    }


    /**
     * 保存在本地同时上传到服务器
     * @param list
     * @param account
     */
    private void saveFriend(List<MessageEvent> list, String account) {
        FriendModel newFriend =
                new FriendModel(account
                        , list.get(0).getFromUserInfo().getUserId());
        mList.add(newFriend);
        adapter.notifyDataSetChanged();
        user.setFriendModelList(mList);
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mContext.toastShort("添加成功");
                } else {
                    mContext.toastShort("更新失败：" + e.getMessage());
                }
            }
        });
    }
}
