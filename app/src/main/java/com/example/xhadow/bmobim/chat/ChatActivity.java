package com.example.xhadow.bmobim.chat;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xhadow.bmobim.R;
import com.example.xhadow.bmobim.base.BaseActivity;
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
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

public class ChatActivity extends BaseActivity implements MessageListHandler {
    private RecyclerView recyclerView;
    private MsgAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Msg> mList = new ArrayList<>();

    private Button send_btn;
    private EditText editText;
    private BmobIMConversation mBmobIMConversation;


    private String id;

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
    }

    public void toEnd() {
        int newSize = mList.size() - 1;
        adapter.notifyItemInserted(newSize);
        recyclerView.scrollToPosition(newSize);
    }

    public void initData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        toastShort(id);
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

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        Logger.d("receive!" + list.size());
        for (int i = 0; i < list.size(); i++) {
            mList.add(new Msg(list.get(i).getMessage().getContent(), Msg.TYPE.RECEIVED));
            toEnd();
            Logger.d("receive!" + i);
        }
    }
}
