package com.jadenine.circle.ui.topic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.utils.SectionedRecyclerViewAdapter;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.widgets.TopicHeader;

import java.util.Collections;
import java.util.List;

/**
 * Created by linym on 7/22/15.
 */
public class TopicListAdapter extends SectionedRecyclerViewAdapter
        .ItemAdapter<Bomb> {
    private List<Group<Bomb>> bombGroups = Collections.emptyList();
    protected final Drawable errorDrawable;
    protected final AvatarBinder avatarBinder;

    private final OnTopicClickListener onTopicClickListener;

    public TopicListAdapter(Drawable errorDrawable, AvatarBinder avatarBinder, OnTopicClickListener listener) {
        this.errorDrawable = errorDrawable;
        this.avatarBinder = avatarBinder;
        this.onTopicClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TopicHeader topicHeader = (TopicHeader) LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_header,
                parent, false);
        TopicItemViewHolder viewHolder = new TopicItemViewHolder(topicHeader);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Group<Bomb> bombGroup = bombGroups.get(position);
        ((TopicItemViewHolder)holder).bind(bombGroup, errorDrawable, avatarBinder, onTopicClickListener);
    }

    @Override
    public int getItemCount() {
        return bombGroups.size();
    }

    @Override
    public long getItemId(int position) {
        return bombGroups.get(position).getId();
    }

    @Override
    public void setItems(List<Group<Bomb>> items) {
        setBombGroups(items);
    }

    @Override
    public Group<Bomb> getItem(int position) {
        return getBombGroup(position);
    }

    public void setBombGroups(List<Group<Bomb>> bombGroups) {
        this.bombGroups = bombGroups;
        notifyDataSetChanged();
    }

    public Group<Bomb> getBombGroup(int position) {
        return bombGroups.get(position);
    }

    public interface OnTopicClickListener{
        void onTopicClicked(Context context, Group<Bomb> topic);
    }
}
