package com.jadenine.circle.ui.menu;

import android.app.Activity;
import android.view.View;

import com.jadenine.circle.ui.share.ShareDialogFragment;

/**
 * Created by Guaidaodl on 2015/8/27.
 */
public class MenuShareItemViewHolder extends MenuNormalItemViewHolder {

    public MenuShareItemViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(int iconResId, int titleResId) {
        if (iconResId > 0) {
            iconView.setImageResource(iconResId);
        }
        if(iconResId > 0) {
            iconView.setImageResource(iconResId);
        }
        iconView.setVisibility(iconResId >0? View.VISIBLE:View.GONE);

        titleView.setText(titleResId);
        readView.setVisibility(View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialogFragment dialog = new ShareDialogFragment();
                dialog.show(((Activity)v.getContext()).getFragmentManager(), "share");
            }
        });
    }
}
