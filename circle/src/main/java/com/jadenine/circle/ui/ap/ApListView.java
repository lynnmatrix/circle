package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.UserAp;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/8/15.
 */
@DaggerScope(ApListPresenter.class)
public class ApListView extends RelativeLayout {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.ap_list_view)
    ListView apListView;

    @Inject
    ApListPresenter presenter;

    public ApListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ApListPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        toolbar.setTitle(R.string.title_activity_ap);

        apListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onApSelected(position);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.CYAN, Color.MAGENTA);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadAPList();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
}
