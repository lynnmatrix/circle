package com.jadenine.circle.ui.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/22/15.
 */
public class BombGroupItemViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.content_view)
    TextView contentView;

    public BombGroupItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    void bind(Group<Bomb> bombGroup) {
        contentView.setText(bombGroup.getEntities().get(0).getContent());
    }
}
