package com.jadenine.circle.ui.menu;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.topic.RecyclerItemClickListener;

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
            public void onItemClick(View view, int position) {
                presenter.onApSelected(position);
            }
        }));

        getAdapter();
        presenter.takeView(this);
    }

     ApMenuAdapter getAdapter() {
         ApMenuAdapter adapter = (ApMenuAdapter) menuView.getAdapter();
        if(null == adapter) {
            adapter = new ApMenuAdapter();
            menuView.setAdapter(adapter);
        }
        return adapter;
    }
}
