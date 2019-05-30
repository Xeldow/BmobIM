package com.example.xhadow.bmobim.im.model;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xhadow.bmobim.chat.ChatActivity;
import com.example.xhadow.bmobim.R;
import com.example.xhadow.bmobim.databinding.ItemFriendsBinding;

import java.util.List;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/5/21
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    private List<FriendModel> modelList;
    private Context mContext;

    public FriendsAdapter(List<FriendModel> modelList, Context context) {
        this.modelList = modelList;
        mContext = context;
    }


    /**
     * 在RecycleView中使用MVVM
     * 关键是获取DataBinding
     */
    public class FriendsViewHolder extends RecyclerView.ViewHolder {
        ItemFriendsBinding binding;

        public FriendsViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (ItemFriendsBinding) binding;
        }

        public ItemFriendsBinding getBinding() {
            return binding;
        }
    }
    /**
     * 初始化View的时候也是通过DataBindingUtil充气item布局
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemFriendsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_friends, viewGroup, false);
        return new FriendsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int pos) {
        final FriendModel friends = modelList.get(pos);
        //这里也是用了双向绑定,每个item都与friends的name对应了
        holder.getBinding().setFriends(friends);
        holder.getBinding().setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("id", friends.id.get());
                intent.putExtra("name", friends.name.get());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return modelList.size() ;
    }
}
