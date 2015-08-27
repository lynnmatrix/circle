package com.jadenine.circle.ui.topic;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.widgets.TopicHeader;

/**
 * Created by linym on 7/22/15.
 */
class TopicItemViewHolder extends RecyclerView.ViewHolder {

    private final TopicHeader topicHeader;

    public TopicItemViewHolder(TopicHeader topicHeader) {
        super(topicHeader);
        this.topicHeader = topicHeader;
    }

    public void bind(final Group<Bomb> topic, Drawable
            errorDrawable,
                     AvatarBinder
                             avatarBinder, final TopicListAdapter.OnTopicClickListener onTopicClickListener) {
        final Bomb rootBomb = topic.getRoot();
        topicHeader.bind(rootBomb, topic.getCount() - 1, errorDrawable, avatarBinder);

        topicHeader.bindTopicWithComments(topic, avatarBinder);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopicClickListener.onTopicClicked(v.getContext(), topic);
            }
        });
    }
}
