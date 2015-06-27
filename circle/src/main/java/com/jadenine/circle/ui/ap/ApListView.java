package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.common.flow.HandlesBack;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/8/15.
 */
@DaggerScope(ApListPresenter.class)
public class ApListView extends RelativeLayout implements HandlesBack {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.ap_list_view)
    ListView apListView;

    @Inject
    ApListPresenter presenter;

    @Inject
    DrawerLayout drawerLayout;

    public ApListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ApListPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        setUpToolbar();

        apListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onApSelected(position);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R.color
                .primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadAPList();
            }
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void setUpToolbar() {
        toolbar.setTitle(R.string.title_activity_ap);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        presenter.dropView(this);
    }

    ArrayAdapter<UserAp> getApAdapter() {
        ArrayAdapter<UserAp> apListViewAdapter = (ArrayAdapter<UserAp>) apListView.getAdapter();
        if (null == apListViewAdapter) {
            apListViewAdapter = new ArrayAdapter<>(getContext(), android.R.layout
                    .simple_list_item_1, new ArrayList<UserAp>(0));
            apListView.setAdapter(apListViewAdapter);
        }
        return apListViewAdapter;
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
