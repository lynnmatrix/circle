package com.jadenine.circle.ui.widgets;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.utils.SectionedRecyclerViewAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/28/15.
 */
public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.progress)
    LoadingView loadingView;

    @InjectView(R.id.message)
    TextView messageView;

    public LoadMoreViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(final SectionedRecyclerViewAdapter.Section section, final
    SectionedLoadMoreRecyclerAdapter.OnFooterClickListener listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onFooterClicked(section.getData(), LoadMoreViewHolder.this);
                }
            }
        });
    }

    public void setError() {
        messageView.setText(R.string.error_load_more);
        loadingView.toggleAnimation(false);
        messageView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    public void startLoading(){
        messageView.setText(R.string.loading_more);
        loadingView.toggleAnimation(true);
        messageView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    public void endLoading() {
        messageView.setText(R.string.loading_more);
        loadingView.toggleAnimation(false);
        messageView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }
}
