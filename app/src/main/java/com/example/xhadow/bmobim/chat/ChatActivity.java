package com.example.xhadow.bmobim.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.xhadow.bmobim.R;
import com.example.xhadow.bmobim.base.BaseActivity;
import com.example.xhadow.bmobim.im.MainActivity;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMFileMessage;
import cn.bmob.newim.bean.BmobIMImageMessage;
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
    private ImageView img_right;
    private EditText editText;
    private BmobIMConversation mBmobIMConversation;


    private String id;
    public static String fName;
    public static String mName = user.getAccount();

    public static BmobIMConversation mConversationManager;

    private static final int FILE_SELECT_CODE = 0;


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
        img_right = findViewById(R.id.right_img);

        refreshLayout = findViewById(R.id.load_more_load_fail_view);
        refreshLayout.setProgressViewOffset(true, -30, -40);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MsgAdapter(R.layout.item_msg, mList, ChatActivity.this);
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
                chooseFile();
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

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = getPath(ChatActivity.this, uri);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists()) {
                            String upLoadFilePath = file.toString();
                            String upLoadFileName = file.getName();
                            Logger.d(upLoadFileName);
                            Logger.d(upLoadFilePath);
                            toastLong("正在发送...");
                            sendLocalFileMessage(upLoadFilePath);
                        }
                    }
                }
            }
        }
    }


    public void toEnd() {
        int newSize = mList.size() - 1;
        adapter.notifyItemInserted(newSize);
        recyclerView.scrollToPosition(newSize);
    }

    /**
     * 创建会话
     */
    public void initData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        fName = intent.getStringExtra("name");
        toastShort(id);
        BmobIMConversation conversationEntrance = new BmobIMConversation();
        conversationEntrance.setConversationId(id);
        //TODO 消息：5.1、根据会话入口获取消息管理，聊天页面
//        BmobIMUserInfo bmobIMUserInfo = BmobIM.getInstance().getUserInfo(user.getObjectId());
        BmobIMUserInfo bmobIMUserInfo = new BmobIMUserInfo(id, fName, "s");

        BmobIMConversation bmobIMConversation = BmobIM.getInstance().startPrivateConversation(bmobIMUserInfo, null);


        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), bmobIMConversation);

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

    /**
     * 加载历史数据
     *
     * @param bmobIMMessage
     * @param count
     */
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
                String m = bmobIMMessage.getContent();
                if (bmobIMMessage.getFromId().equals(user.getObjectId())) {
                    if (m.contains("http")) {
                        String[] strings = m.split("&");
                        mList.add(new Msg(strings[1], Msg.TYPE.SENT_IMG));
                    } else {
                        mList.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.SENT));
                    }
                } else {
                    if (m.contains("http")) {
                        mList.add(new Msg(m, Msg.TYPE.RECEIVED_IMG));
                    } else {
                        mList.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.RECEIVED));
                    }
                }
            }
        } else {

            List<Msg> newMsg = new ArrayList<>();
            for (BmobIMMessage bmobIMMessage : list) {
                String m = bmobIMMessage.getContent();
                if (bmobIMMessage.getFromId().equals(user.getObjectId())) {
                    if (m.contains("http")) {
                        String[] strings = m.split("&");
                        mList.add(new Msg(strings[1], Msg.TYPE.SENT_IMG));
                    } else {
                        mList.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.SENT));
                    }
                } else {
                    if (m.contains("http")) {
                        mList.add(new Msg(m, Msg.TYPE.RECEIVED_IMG));
                    } else {
                        mList.add(new Msg(bmobIMMessage.getContent(), Msg.TYPE.RECEIVED));
                    }
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
            String m = list.get(i).getMessage().getContent();
            if (m.contains("http")) {
                mList.add(new Msg(m, Msg.TYPE.RECEIVED_IMG));
            } else {
                mList.add(new Msg(list.get(i).getMessage().getContent(), Msg.TYPE.RECEIVED));
            }
            toEnd();
            Logger.d("i=" + i);
            Logger.d("msg=" + list.get(i).getMessage().getContent());
        }
    }


    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            Logger.d("onProgress：" + value);
            Logger.d(value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);

            Logger.d(msg);
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference
            if (e != null) {
                toastShort(e.getMessage());
            } else {
                toastShort("send!");
//                mList.add(new Msg(),Msg.TYPE.SENT);

                toEnd();
            }
        }
    };


    /**
     * 发送本地文件
     */
    public void sendLocalFileMessage(String path) {
        mList.add(new Msg(path, Msg.TYPE.SENT_IMG));

        //TODO 发送消息：6.8、发送本地文件消息
        BmobIMImageMessage image = new BmobIMImageMessage(path);
        mConversationManager.sendMessage(image, listener);

    }


    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
