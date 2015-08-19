package com.jadenine.circle.ui.topic;

import android.content.Context;
import android.util.AttributeSet;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * Created by linym on 7/22/15.
 */
public class TopicListView extends RefreshableHomeView {
    @Inject
    TopicListPresenter presenter;

    @Inject
    SectionedLoadMoreRecyclerAdapter<Bomb> bombAdapter;

    public TopicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<TopicListPath.Component>getDaggerComponent(context).inject(this);

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
        super.onDetachedFromWindow();
        presenter.dropView(this);
        setAdapter(null);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }


    @OnClick(R.id.fab_add_bomb)
    public void onAddBomb(){
        presenter.addBomb();
    }

    SectionedLoadMoreRecyclerAdapter getAdapter() {
        return bombAdapter;
    }

}
