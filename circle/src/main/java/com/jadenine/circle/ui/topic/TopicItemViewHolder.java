package com.jadenine.circle.ui.topic;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.Topic;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/10/15.
 */
public class TopicItemViewHolder extends RecyclerView.ViewHolder {
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    @InjectView(R.id.date)
    TextView dateView;

    @InjectView(R.id.content)
    TextView contentView;

    public TopicItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(Topic topic) {
        dateView.setText(getFormattedTime(topic.getTimestamp()));
        contentView.setText(topic.getTopic());
    }

    private String getFormattedTime(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
}
