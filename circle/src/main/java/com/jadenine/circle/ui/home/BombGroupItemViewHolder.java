package com.jadenine.circle.ui.home;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.detail.TopicHeader;

/**
 * Created by linym on 7/22/15.
 */
public class BombGroupItemViewHolder extends RecyclerView.ViewHolder {

    private final TopicHeader topicHeader;
    public BombGroupItemViewHolder(TopicHeader topicHeader) {
        super(topicHeader);
        this.topicHeader = topicHeader;
    }

    public void bind(Bomb rootBomb, int groupSize, Drawable errorDrawable) {
        topicHeader.bind(rootBomb, groupSize, errorDrawable);
    }
}
