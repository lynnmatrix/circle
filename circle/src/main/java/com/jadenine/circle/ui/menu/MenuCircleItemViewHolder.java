package com.jadenine.circle.ui.menu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.ui.topic.TopicListPath;

import butterknife.ButterKnife;
import butterknife.InjectView;
import flow.Flow;
import flow.History;
import flow.path.Path;

/**
 * Created by linym on 7/22/15.
 */
class MenuCircleItemViewHolder extends RecyclerView.ViewHolder{
    @InjectView(R.id.ap_ssid)
    TextView ssidView;

    @InjectView(R.id.read)
    ImageView readView;

    public MenuCircleItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    void bind(final Circle circle) {
        ssidView.setText(circle.getName());
        readView.setVisibility(circle.hasUnread()?View.VISIBLE:View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                onCircleSelected(circle);
            }
        });
    }

    private void onCircleSelected(@NonNull Circle circle) {
        if(null == circle) {
            return;
        }
        replaceWithPath(new TopicListPath(circle.getCircleId()));
    }

    private void replaceWithPath(@NonNull Path path) {
        History.Builder historyBuilder = Flow.get(itemView.getContext()).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.push(path);

        Flow.get(itemView.getContext()).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
    }
}
