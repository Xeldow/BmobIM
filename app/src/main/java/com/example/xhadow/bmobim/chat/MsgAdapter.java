package com.example.xhadow.bmobim.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.xhadow.bmobim.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static com.example.xhadow.bmobim.chat.ChatActivity.fName;
import static com.example.xhadow.bmobim.chat.ChatActivity.mName;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/4/25
 */
public class MsgAdapter extends BaseQuickAdapter<Msg, BaseViewHolder> {
    private Context mContext;

    public MsgAdapter(int layoutId, List<Msg> list, Context mContext) {
        super(layoutId, list);
        this.mContext=mContext;
    }

    @Override
    protected void convert(BaseViewHolder holder, Msg msg) {
        switch (msg.getType()) {
            case RECEIVED://接收的消息
                holder.setGone(R.id.left_layout, false);
                holder.setGone(R.id.right_layout, false);

                holder.setGone(R.id.left_layout, true);
                holder.setText(R.id.left_msg, msg.getContent());
                holder.setText(R.id.left_name, fName);
                break;
            case SENT://发出的消息
                holder.setGone(R.id.left_layout, false);

                holder.setGone(R.id.right_layout, true);
                holder.setText(R.id.right_msg, msg.getContent());
                holder.setText(R.id.right_name, mName);
                break;
            case SENT_IMG:
                holder.setGone(R.id.left_layout, false);

                holder.setGone(R.id.right_layout, true);
                holder.setGone(R.id.right_msg, false);
                holder.setVisible(R.id.right_img, true);
                holder.setText(R.id.right_name, mName);
                Bitmap bitmap = getLoacalBitmap(msg.getContent());
                holder.setImageBitmap(R.id.right_img, bitmap);
                break;
            case RECEIVED_IMG://接收的消息
                holder.setGone(R.id.left_layout, false);
                holder.setGone(R.id.right_layout, false);

                holder.setGone(R.id.left_layout, true);
                holder.setGone(R.id.left_msg, false);
                holder.setVisible(R.id.left_img, true);
                holder.setText(R.id.left_name, fName);
                Glide.with(mContext)
                        .load(msg.getContent())
                        .into((ImageView) holder.getView(R.id.left_head_img));
                break;
        }
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}