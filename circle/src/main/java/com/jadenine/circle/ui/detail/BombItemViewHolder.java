package com.jadenine.circle.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.utils.TimeFormatUtils;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/24/15.
 */
public class BombItemViewHolder extends RecyclerView.ViewHolder{
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    @InjectView(R.id.avatar)
    ImageView avatarView;

    @InjectView(R.id.private_view)
    ImageView privateView;

    @InjectView(R.id.from)
    TextView fromView;

    @InjectView(R.id.date)
    TextView dateView;

    @InjectView(R.id.content)
    TextView contentView;


    public BombItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(Bomb bomb, int position, AvatarBinder avatarBinder) {
        privateView.setVisibility(View.GONE);

        avatarView.setImageResource(avatarBinder.getAvatar(bomb.getFrom(), bomb.getRootMessageId
                ()));

        boolean isOwner = bomb.getRootUser().equals(bomb.getFrom());

        fromView.setText(String.format("%s", isOwner ? fromView.getContext().getString(R.string
                .topic_owner) : "User" + (bomb.getFrom() + bomb.getRootMessageId()).hashCode()));
        dateView.setText(TimeFormatUtils.getFormattedTime(bomb.getTimestamp()));
        contentView.setText(bomb.getContent());

        itemView.setTag(bomb.getId());
    }

}
