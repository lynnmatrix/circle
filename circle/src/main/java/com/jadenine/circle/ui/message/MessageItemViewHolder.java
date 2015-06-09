package com.jadenine.circle.ui.message;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.Message;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/9/15.
 */
public class MessageItemViewHolder extends RecyclerView.ViewHolder {
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

    public void bind(Message message) {
        fromView.setText(message.getUser());
        dateView.setText(message.getFormattedTime());
        contentView.setText(message.getContent());
    }
}
