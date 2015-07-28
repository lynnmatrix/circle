package com.jadenine.circle.ui.home;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.detail.BombGroupPath;
import com.jadenine.circle.ui.detail.TopicHeader;

import flow.Flow;

/**
 * Created by linym on 7/22/15.
 */
public class BombGroupItemViewHolder extends RecyclerView.ViewHolder {

    private final TopicHeader topicHeader;
    public BombGroupItemViewHolder(TopicHeader topicHeader) {
        super(topicHeader);
        this.topicHeader = topicHeader;
    }

    public void bind(final Bomb rootBomb, int commentCount, Drawable errorDrawable, AvatarBinder avatarBinder) {
        topicHeader.bind(rootBomb, commentCount, errorDrawable, avatarBinder);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flow.get(v.getContext()).set(new BombGroupPath(rootBomb.getAp(), rootBomb
                        .getGroupId()));
            }
        });
    }
}
