package com.jadenine.circle.ui.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.ui.chat.MyChatPath;

import butterknife.ButterKnife;
import butterknife.InjectView;
import flow.Flow;
import flow.History;
import flow.path.Path;

/**
 * Created by linym on 8/5/15.
 */
class MenuNormalItemViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.icon)
    ImageView iconView;
    @InjectView(R.id.title)
    TextView titleView;

    @InjectView(R.id.read)
    ImageView readView;

    public MenuNormalItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(int iconResId, int titleResId, boolean hasUnread, final Path path) {
        if(iconResId > 0) {
            iconView.setImageResource(iconResId);
        }
        iconView.setVisibility(iconResId >0?View.VISIBLE:View.GONE);

        titleView.setText(titleResId);
        readView.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                Context context = itemView.getContext();
                History.Builder historyBuilder = Flow.get(context).getHistory().buildUpon();
                historyBuilder.pop();
                historyBuilder.push(path);

                Flow.get(context).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
            }
        });
    }
}
