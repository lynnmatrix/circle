package com.jadenine.circle.ui.topic.user;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.topic.detail.MyTopicDetailPath;
import com.jadenine.circle.ui.widgets.TopicHeader;

import flow.Flow;

/**
 * Created by linym on 8/6/15.
 */
class MyTopicItemViewHolder extends RecyclerView.ViewHolder {

    private final TopicHeader topicHeader;

    public MyTopicItemViewHolder(TopicHeader topicHeader) {
        super(topicHeader);
        this.topicHeader = topicHeader;
    }

    public void bind(Group<Bomb> topic, Drawable errorDrawable, AvatarBinder avatarBinder) {
        final Bomb rootBomb = topic.getRoot();
        topicHeader.bind(rootBomb, topic.getCount() - 1, errorDrawable, avatarBinder);

        topicHeader.bindTopicWithComments(topic, avatarBinder);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flow.get(v.getContext()).set(new MyTopicDetailPath(rootBomb.getGroupId()));
            }
        });
    }
}