package com.jadenine.circle.ui.home;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.topic.AutoLoadMoreListener;
import com.jadenine.circle.ui.topic.RecyclerItemClickListener;
import com.jadenine.common.flow.HandlesBack;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by linym on 7/22/15.
 */
@DaggerScope(BombListPresenter.class)
public class BombListView extends RelativeLayout implements HandlesBack {
    @InjectView(R.id.scrollable_view)
    RecyclerView recyclerView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Inject
    DrawerLayout drawerLayout;

    @Inject
    BombListPresenter presenter;

    public BombListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<BombListPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        recyclerView.setHasFixedSize(false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new
                RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                presenter.onDetail(position);
            }
        }));

        recyclerView.addOnScrollListener(new AutoLoadMoreListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                presenter.loadMore();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R
                .color.primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refresh();
            }
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        configToolbar();

        getAdapter();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    protected void configToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @OnClick(R.id.fab_add_bomb)
    public void onAddBomb(){
        presenter.addBomb();
    }

    BombRecyclerAdapter getAdapter() {
        BombRecyclerAdapter adapter = (BombRecyclerAdapter) recyclerView.getAdapter();
        if(null == adapter) {
            adapter = new BombRecyclerAdapter();
            recyclerView.setAdapter(adapter);
        }
        return adapter;
    }

    @Override
    public boolean onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        }
        return false;
    }

}
