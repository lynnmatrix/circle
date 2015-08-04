package com.jadenine.circle.ui.menu;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.utils.RecyclerItemClickListener;
import com.jadenine.circle.ui.widgets.DividerItemDecoration;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/22/15.
 */
public class ApMenuView extends NavigationView {

    @InjectView(R.id.menu_view)
    RecyclerView menuView;

    @Inject
    ApMenuPresenter presenter;

    @Inject
    DrawerLayout drawerLayout;

    @Inject
    ApMenuAdapter adapter;

    public ApMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        DaggerService.<HomeComponent>getDaggerComponent(getContext()).inject(this);

        menuView.setHasFixedSize(false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        menuView.setLayoutManager(linearLayoutManager);
        menuView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new
                RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                return presenter.onApSelected(position);
            }
        }));
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        menuView.addItemDecoration(decoration);
        menuView.setAdapter(adapter);
        presenter.takeView(this);
    }

    @Override
    public void onDetachedFromWindow() {
        presenter.dropView(this);
        drawerLayout = null;
        CircleApplication.getRefWatcher(getContext()).watch(this);
        super.onDetachedFromWindow();
    }

    ApMenuAdapter getAdapter() {
        return adapter;
    }
}
