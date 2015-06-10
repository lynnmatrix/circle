package com.jadenine.circle.ui.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.Message;
import com.jadenine.circle.entity.Topic;

import java.util.Collections;
import java.util.List;

/**
 * Created by linym on 6/9/15.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageItemViewHolder> {

    private final Topic topic;
    private List<Message> messages = Collections.emptyList();

    public MessageRecyclerAdapter(Topic topic) {
        this.topic = topic;
    }

    @Override
    public MessageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,
                parent, false);
        MessageItemViewHolder viewHolder = new MessageItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageItemViewHolder holder, int position) {
        holder.bind(topic, messages.get(position), position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
}
