package com.jadenine.circle.ui.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by linym on 6/25/15.
 */
public abstract class AutoLoadMoreListener extends RecyclerView.OnScrollListener {

    public static final int DEFAULT_VISIBLE_THRESHOLD = 2;

    private final LinearLayoutManager layoutManager;

    private final int visibleThreshold;

    public AutoLoadMoreListener(@NonNull LinearLayoutManager layoutManager) {
        this(layoutManager, null);
    }

    public AutoLoadMoreListener(@NonNull LinearLayoutManager layoutManager, Integer visibleThreshold) {
        this.layoutManager = layoutManager;
        if(null != visibleThreshold && visibleThreshold >= 0) {
            this.visibleThreshold = visibleThreshold;
        } else {
            this.visibleThreshold = DEFAULT_VISIBLE_THRESHOLD;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        boolean reachEnd = (firstVisibleItem + visibleItemCount) >= (totalItemCount -
                visibleThreshold);
        if (reachEnd) {
            onLoadMore();
        }
    }

    public abstract void onLoadMore();
}
