package com.jadenine.circle.ui.topic;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.topic.detail.TopicDetailPath;
import com.jadenine.circle.ui.widgets.TopicHeader;

import flow.Flow;

/**
 * Created by linym on 7/22/15.
 */
class TopicItemViewHolder extends RecyclerView.ViewHolder {

    private final TopicHeader topicHeader;
    public TopicItemViewHolder(TopicHeader topicHeader) {
        super(topicHeader);
        this.topicHeader = topicHeader;
    }

    public void bind(final Bomb rootBomb, int commentCount, Drawable errorDrawable, AvatarBinder avatarBinder) {
        topicHeader.bind(rootBomb, commentCount, errorDrawable, avatarBinder);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flow.get(v.getContext()).set(new TopicDetailPath(rootBomb.getAp(), rootBomb
                        .getGroupId()));
            }
        });
    }
}
