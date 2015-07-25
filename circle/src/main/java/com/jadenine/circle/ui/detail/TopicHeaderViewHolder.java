package com.jadenine.circle.ui.detail;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;

/**
 * Created by linym on 7/25/15.
 */
public class TopicHeaderViewHolder extends RecyclerView.ViewHolder implements TopicHeader.OnAvatarClickListener{
    private final TopicHeader topicHeader;

    public TopicHeaderViewHolder(TopicHeader itemView) {
        super(itemView);
        this.topicHeader = itemView;
        topicHeader.setOnAvatarClickListener(this);
    }

    public void bind(Bomb rootBomb, int commentCount, Drawable errorDrawable, AvatarBinder avatarBinder) {
        topicHeader.bind(rootBomb, commentCount, errorDrawable, avatarBinder);
    }

    @Override
    public void onClick() {
        //TODO
    }
}
