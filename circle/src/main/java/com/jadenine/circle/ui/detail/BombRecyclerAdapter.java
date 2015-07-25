package com.jadenine.circle.ui.detail;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.home.BombGroupItemViewHolder;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by linym on 7/24/15.
 */
public class BombRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_REPLY = 1;
    private List<Bomb> bombs = Collections.emptyList();
    private Bomb rootBomb;
    private Drawable errorDrawable;
    private final AvatarBinder avatarBinder;

    @Inject
    @Singleton
    public BombRecyclerAdapter(Drawable errorDrawable, AvatarBinder avatarBinder) {
        this.errorDrawable = errorDrawable;
        this.avatarBinder = avatarBinder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_REPLY;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(TYPE_HEADER == viewType) {
            TopicHeader topicHeader = (TopicHeader) LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_header,
                    parent, false);
            viewHolder = new BombGroupItemViewHolder(topicHeader);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,
                    parent, false);
            viewHolder = new BombItemViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case TYPE_HEADER:
                ((BombGroupItemViewHolder) holder).bind(rootBomb, getItemCount() - 1,
                        errorDrawable, avatarBinder);
                break;
            case TYPE_REPLY:
                ((BombItemViewHolder)holder).bind(bombs.get(position), position, avatarBinder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bombs.size();
    }

    public void setBombs(Bomb rootBomb, List<Bomb> entities) {
        this.rootBomb = rootBomb;
        bombs = entities;
        notifyDataSetChanged();
    }
}
