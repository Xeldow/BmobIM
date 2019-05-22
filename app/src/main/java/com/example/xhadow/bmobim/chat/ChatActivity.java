package com.example.xhadow.bmobim.chat;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.xhadow.bmobim.R;
import com.example.xhadow.bmobim.base.BaseActivity;
import com.example.xhadow.bmobim.im.MainActivity;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMFileMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.exception.BmobException;

import static com.example.xhadow.bmobim.utils.Constants.user;

public class ChatActivity extends BaseActivity implements MessageListHandler {
    private RecyclerView recyclerView;
    private MsgAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Msg> mList = new ArrayList<>();

    private Button send_btn;
    private ImageButton more_btn;
    private EditText editText;
    private BmobIMConversation mBmobIMConversation;


    private String id;
    public static String fName;
    public static String mName = user.getAccount();

    BmobIMConversation mConversationManager;

    private BmobIMMessage firstMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);
        BmobIM.getInstance().addMessageListHandler(this);

        initData();
        initView();
    }

    public void initView() {
        refreshLayout = findViewById(R.id.load_more_load_fail_view);
        refreshLayout.setProgressViewOffset(true, -30, -40);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MsgAdapter(R.layout.item_msg, mList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(50);

        editText = findViewById(R.id.et_talk);
        send_btn = findViewById(R.id.btn_send);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();

                mList.add(new Msg(msg, Msg.TYPE.SENT));
                toEnd();

                sendMsg(msg);
                editText.setText("");
            }
        });
        more_btn = findViewById(R.id.more_func_btn);
        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int REQUESTCODE_FROM_ACTIVITY = 1000;
                LFilePicker lFilePicker = new LFilePicker()
                        .withActivity(ChatActivity.this)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY);
                lFilePicker.start();
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryMsg(firstMsg, 4);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
//                //TODO 发送消息：6.8、发送本地文件消息
//                BmobIMFileMessage file = new BmobIMFileMessage("此处替换为你本地的文件地址");
//                mConversationManager.sendMessage(file, new MessageSendListener() {
//                    @Override
//                    public void done(BmobIMMessage bmobIMMessage, BmobException e) {
//
//                    }
//                });
            }
        }
    }

    public void toEnd() {
        int newSize = mList.size() - 1;
        adapter.notifyItemInserted(newSize);
        recyclerView.scrollToPosition(newSize);
    }

    public void initData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        fName = intent.getStringExtra("name");
        toastShort(id);
        BmobIMConversation conversationEntrance = new BmobIMConversation();
        conversationEntrance.setConversationId(id);
        //TODO 消息：5.1、根据会话入口获取消息管理，聊天页面
        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        //先获取5条历史消息
        queryMsg(null, 6);
    }

    public void sendMsg(final String message) {
        BmobIMUserInfo info = new BmobIMUserInfo();
        info.setAvatar("填写接收者的头像");
        info.setUserId(id);
        info.setName("填写接收者的名字");
        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if (e == null) {
//                    isOpenConversation = true;
                    //在此跳转到聊天页面或者直接转化
                    mBmobIMConversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
//                    tv_message.append("发送者：" + user.getAccount() + "\n");
                    BmobIMTextMessage msg = new BmobIMTextMessage();
                    msg.setContent(message);
                    mBmobIMConversation.sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {
                            if (e == null) {
                                toastShort("send!");
                            } else {
                                toastShort("error!");
                            }
                        }
                    });
                } else {
                    toastShort("开启会话出错");
                }
            }
        });
    }

    public void queryMsg(BmobIMMessage bmobIMMessage, int count) {

        mConversationManager.queryMessages(bmobIMMessage, count, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        //填充消息到list
                        addMsg(list);
//                        adapter.addMessages(list);
//                        adapter.notifyDataSetChanged();
//                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
//                        toastLong(list.get(4).getContent());
                    }
                } else {
                    toastShort(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    private void addMsg(List<BmobIMMessage> list) {
        firstMsg = list.get(0);

        if (adapter == null) {

            for (BmobIMMessage bmobIMMessage : list) {
                if (bmobIMMessage.getFromId().equals(user.getObjectId())) {
                    mList.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.SENT));
                } else {
                    mList.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.RECEIVED));
                }
            }
        } else {

            List<Msg> newMsg = new ArrayList<>();
            for (BmobIMMessage bmobIMMessage : list) {
                if (bmobIMMessage.getFromId().equals(user.getObjectId())) {
                    newMsg.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.SENT));
                } else {
                    newMsg.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.RECEIVED));
                }
            }
            adapter.addData(0, newMsg);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        Logger.d("size=" + list.size());
        for (int i = 0; i < list.size(); i++) {
            mList.add(new Msg(list.get(i).getMessage().getContent(), Msg.TYPE.RECEIVED));
            toEnd();
            Logger.d("i=" + i);
            Logger.d("msg=" + list.get(i).getMessage().getContent());
        }
    }
}
