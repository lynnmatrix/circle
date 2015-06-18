package com.jadenine.circle.ui.topic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Topic;

import java.util.Collections;
import java.util.List;

/**
 * Created by linym on 6/10/15.
 */
public class TopicRecyclerAdapter extends RecyclerView.Adapter<TopicItemViewHolder>{
    private List<Topic> topics = Collections.emptyList();

    @Override
    public TopicItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic,
                parent, false);
        TopicItemViewHolder viewHolder = new TopicItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TopicItemViewHolder holder, int position) {
        holder.bind(topics.get(position));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    public Topic getTopic(int position) {
        return topics.get(position);
    }
}
