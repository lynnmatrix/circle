package com.jadenine.circle.ui.topic;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Topic;
import com.squareup.picasso.Picasso;

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

    @InjectView(R.id.imageview)
    ImageView imageView;

    @InjectView(R.id.message_count)
    TextView messageCountView;

    public TopicItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(Topic topic) {
        dateView.setText(getFormattedTime(topic.getTimestamp()));
        contentView.setText(topic.getTopic());
        messageCountView.setText(""+topic.getMessageCount());
        if(null != topic.getImages() && topic.getImages().size() > 0) {
            imageView.setVisibility(View.VISIBLE);
            Uri imageUri = Uri.parse(topic.getImages().get(0));
            Picasso.with(imageView.getContext()).load(imageUri).into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private String getFormattedTime(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
}
