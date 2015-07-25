package com.jadenine.circle.ui.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.utils.TimeFormatUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/24/15.
 */
public class TopicHeader extends LinearLayout {

    @InjectView(R.id.avatar)
    ImageView avatarView;

    @InjectView(R.id.date)
    TextView dateView;

    @InjectView(R.id.content)
    TextView contentView;

    @InjectView(R.id.content_image)
    ImageView imageView;

    @InjectView(R.id.message_count)
    TextView messageCountView;

    public TopicHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bind(Bomb rootBomb, int commentCount, Drawable errorDrawable) {
        setTag(rootBomb.getId());

        dateView.setText(getFormattedTime(rootBomb.getTimestamp()));
        contentView.setText(rootBomb.getContent());
        messageCountView.setText("" + commentCount);
        if(null != rootBomb.getImages() && rootBomb.getImages().length() > 0) {
            imageView.setVisibility(View.VISIBLE);
            Uri imageUri = Uri.parse(rootBomb.getImages());
            Picasso.with(imageView.getContext()).load(imageUri).centerCrop().error(errorDrawable)
                    .resize(440, 220).onlyScaleDown().into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private String getFormattedTime(long timestamp) {
        return TimeFormatUtils.getFormattedTime(timestamp);
    }
}
