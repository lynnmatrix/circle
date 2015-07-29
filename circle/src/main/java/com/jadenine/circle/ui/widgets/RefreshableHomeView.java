package com.jadenine.circle.ui.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.ui.utils.AutoLoadMoreListener;
import com.jadenine.circle.ui.utils.RecyclerItemClickListener;
import com.jadenine.circle.utils.ToolbarColorizer;
import com.jadenine.common.flow.HandlesBack;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/27/15.
 */
public abstract class RefreshableHomeView extends LinearLayout implements HandlesBack {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.scrollable_view)
    RecyclerView recyclerView;

    @Inject
    DrawerLayout drawerLayout;

    @Inject
    Activity activity;

    private RefreshableHomeListener refreshableHomeListener;

    public RefreshableHomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);

        recyclerView.setHasFixedSize(false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new
                RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position) {
                        if(null != refreshableHomeListener) {
                            return refreshableHomeListener.onRowClick(position);
                        }
                        return false;
                    }
                }));

        recyclerView.addOnScrollListener(new AutoLoadMoreListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                if(null != refreshableHomeListener) {
                    refreshableHomeListener.onLoadMore();
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R
                .color.primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null != refreshableHomeListener) {
                    refreshableHomeListener.onRefresh();
                }
            }
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        configToolbar();
    }

    @Override
    public void onDetachedFromWindow() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        CircleApplication.getRefWatcher(getContext()).watch(this);
        super.onDetachedFromWindow();
    }

    protected void configToolbar() {
        ToolbarColorizer.colorizeToolbar(toolbar, Color.WHITE, activity);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        }
        return false;
    }

    protected void setRefreshableListener(RefreshableHomeListener listener) {
        this.refreshableHomeListener = listener;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public interface RefreshableHomeListener{
        void onRefresh();
        void onLoadMore();
        boolean onRowClick(int position);
    }
}
