package com.jadenine.circle.ui.home;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.jadenine.circle.R;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * Created by linym on 7/22/15.
 */
@DaggerScope(BombListPresenter.class)
public class BombListView extends RefreshableHomeView {
    @Inject
    BombListPresenter presenter;

    @Inject
    Activity activity;

    @Inject
    SectionedLoadMoreRecyclerAdapter<Bomb> bombAdapter;

    public BombListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<BombListPath.Component>getDaggerComponent(context).inject(this);

        setRefreshableListener(presenter);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);

        setAdapter(bombAdapter);

    }

    @Override
    public void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }


    @OnClick(R.id.fab_add_bomb)
    public void onAddBomb(){
        presenter.addBomb();
    }

    SectionedLoadMoreRecyclerAdapter getAdapter() {
        return bombAdapter;
    }

}
