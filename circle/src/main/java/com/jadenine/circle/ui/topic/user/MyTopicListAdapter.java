package com.jadenine.circle.ui.topic.user;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.topic.TopicListAdapter;
import com.jadenine.circle.ui.widgets.TopicHeader;

/**
 * Created by linym on 8/6/15.
 */
class MyTopicListAdapter extends TopicListAdapter {

    public MyTopicListAdapter(Drawable errorDrawable, AvatarBinder avatarBinder) {
        super(errorDrawable, avatarBinder);
    }

    @Override
    public MyTopicItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TopicHeader topicHeader = (TopicHeader) LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_header,
                parent, false);
        MyTopicItemViewHolder viewHolder = new MyTopicItemViewHolder(topicHeader);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Group<Bomb> bombGroup = getBombGroup(position);
        ((MyTopicItemViewHolder)holder).bind(bombGroup, errorDrawable, avatarBinder);
    }
}
