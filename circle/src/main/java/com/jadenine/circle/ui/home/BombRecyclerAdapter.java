package com.jadenine.circle.ui.home;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.detail.TopicHeader;

import java.util.Collections;
import java.util.List;

/**
 * Created by linym on 7/22/15.
 */
public class BombRecyclerAdapter extends RecyclerView.Adapter<BombGroupItemViewHolder>{
    private List<Group<Bomb>> bombGroups = Collections.emptyList();
    private final Drawable errorDrawable;
    public BombRecyclerAdapter(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
    }

    @Override
    public BombGroupItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TopicHeader topicHeader = (TopicHeader) LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_header,
                parent, false);
        BombGroupItemViewHolder viewHolder = new BombGroupItemViewHolder(topicHeader);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BombGroupItemViewHolder holder, int position) {
        holder.bind(bombGroups.get(position), errorDrawable);
    }

    @Override
    public int getItemCount() {
        return bombGroups.size();
    }

    public void setBombGroups(List<Group<Bomb>> bombGroups) {
        this.bombGroups = bombGroups;
        notifyDataSetChanged();
    }

    public Group<Bomb> getBombGroup(int position) {
        return bombGroups.get(position);
    }
}
