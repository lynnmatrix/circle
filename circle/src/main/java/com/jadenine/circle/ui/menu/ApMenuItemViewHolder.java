package com.jadenine.circle.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Circle;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/22/15.
 */
class ApMenuItemViewHolder extends RecyclerView.ViewHolder{
    @InjectView(R.id.ap_ssid)
    TextView ssidView;

    @InjectView(R.id.read)
    ImageView readView;

    public ApMenuItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    void bind(Circle circle) {
        ssidView.setText(circle.getName());
        readView.setVisibility(circle.hasUnread()?View.VISIBLE:View.GONE);
    }
}
