package com.jadenine.circle.ui.topic.detail;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.widgets.TopicHeader;

/**
 * Created by linym on 7/25/15.
 */
class TopicHeaderViewHolder extends RecyclerView.ViewHolder {
    private final TopicHeader topicHeader;

    public TopicHeaderViewHolder(TopicHeader itemView) {
        super(itemView);
        this.topicHeader = itemView;

    }

    public void bind(Bomb rootBomb, int commentCount, Drawable errorDrawable, AvatarBinder avatarBinder) {
        topicHeader.bind(rootBomb, commentCount, errorDrawable, avatarBinder);
    }

    public void setOnAvatarClickListener(TopicHeader.OnAvatarClickListener listener) {
        topicHeader.setOnAvatarClickListener(listener);
    }
}
