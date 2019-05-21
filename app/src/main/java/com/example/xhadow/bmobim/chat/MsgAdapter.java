package com.example.xhadow.bmobim.chat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.xhadow.bmobim.R;

import java.util.List;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/4/25
 */
public class MsgAdapter extends BaseQuickAdapter<Msg, BaseViewHolder> {

    public MsgAdapter(int layoutId, List<Msg> list) {
        super(layoutId, list);
    }

    @Override
    protected void convert(BaseViewHolder holder, Msg msg) {
        switch (msg.getType()) {
            case RECEIVED://接收的消息
                holder.setGone(R.id.left_layout, false);
                holder.setGone(R.id.right_layout, false);

                holder.setGone(R.id.left_layout, true);
                holder.setText(R.id.left_msg, msg.getContent());
                break;
            case SENT://发出的消息
                holder.setGone(R.id.left_layout, false);

                holder.setGone(R.id.right_layout, true);
                holder.setText(R.id.right_msg, msg.getContent());
                break;
        }
    }

}