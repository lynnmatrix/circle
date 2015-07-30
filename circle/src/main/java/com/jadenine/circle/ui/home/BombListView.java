package com.jadenine.circle.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.utils.AutoLoadMoreListener;
import com.jadenine.circle.ui.widgets.DividerItemDecoration;
import com.jadenine.circle.utils.ToolbarColorizer;
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

    @Inject
    Drawable errorDrawable;

    @Inject
    Activity activity;

    @Inject
    AvatarBinder avatarBinder;

    @Inject
    SectionedBombGroupRecyclerAdapter sectionedBombGroupRecyclerAdapter;

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
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new AutoLoadMoreListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                presenter.loadMore();
            }
        });

        recyclerView.setAdapter(sectionedBombGroupRecyclerAdapter);

        sectionedBombGroupRecyclerAdapter.setOnFooterClickListener(new SectionedBombGroupRecyclerAdapter.OnFooterClickListener() {

            @Override
            public void onFooterClicked(TimelineRange range, LoadMoreViewHolder
                    loadMoreViewHolder) {
                presenter.loadMore(range, loadMoreViewHolder);
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
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
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

    @OnClick(R.id.fab_add_bomb)
    public void onAddBomb(){
        presenter.addBomb();
    }

    SectionedBombGroupRecyclerAdapter getAdapter() {
        return sectionedBombGroupRecyclerAdapter;
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
