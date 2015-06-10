package com.jadenine.circle.ui.message;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/9/15.
 */
public class MessageItemViewHolder extends RecyclerView.ViewHolder {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    @InjectView(R.id.from)
    TextView fromView;

    @InjectView(R.id.date)
    TextView dateView;

    @InjectView(R.id.content)
    TextView contentView;

    public MessageItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(Message message, int position) {
        fromView.setText(String.format("%d•User%d", position + 1, (message.getUser() + message
                .getTopicId())
                .hashCode()));
        dateView.setText(getFormattedTime(message.getTimestamp()));
        contentView.setText(message.getContent());
    }

    @NonNull
    private String getFormattedTime(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
}
