package com.jadenine.circle.ui.topic.detail;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.widgets.TopicHeader;
import com.jadenine.circle.ui.widgets.TopicHeaderViewHolder;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by linym on 7/24/15.
 */
class BombListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_REPLY = 1;
    private List<Bomb> bombs = Collections.emptyList();
    private Bomb rootBomb;
    private Drawable errorDrawable;
    private final AvatarBinder avatarBinder;

    private TopicHeader.OnAvatarClickListener onAvatarClickListener;
    private OnBombItemClickListener onBombItemClick;

    @Inject
    @Singleton
    public BombListAdapter(Drawable errorDrawable, AvatarBinder avatarBinder) {
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
            viewHolder = new TopicHeaderViewHolder(topicHeader);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bomb,
                    parent, false);
            viewHolder = new BombItemViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()){
            case TYPE_HEADER:
                TopicHeaderViewHolder headerViewHolder = (TopicHeaderViewHolder) holder;
                headerViewHolder.bind(rootBomb, getItemCount() - 1,
                        errorDrawable, avatarBinder);
                headerViewHolder.setOnAvatarClickListener(this.onAvatarClickListener);

                break;
            case TYPE_REPLY:
                ((BombItemViewHolder)holder).bind(bombs.get(position), position, avatarBinder);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != onBombItemClick) {
                    onBombItemClick.onBombItemClicked(bombs.get(position));
                }
            }
        });
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

    public void setOnAvatarClickListener(TopicHeader.OnAvatarClickListener onAvatarClickListener) {
        this.onAvatarClickListener = onAvatarClickListener;
    }

    public void setOnBombItemClick(OnBombItemClickListener onBombItemClick) {
        this.onBombItemClick = onBombItemClick;
    }

    public interface OnBombItemClickListener{
        boolean onBombItemClicked(Bomb bomb);
    }
}

