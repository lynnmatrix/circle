package com.jadenine.circle.ui.topic.user;

import android.content.Context;
import android.util.AttributeSet;

import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import javax.inject.Inject;

/**
 * Created by linym on 8/6/15.
 */
public class MyTopicView  extends RefreshableHomeView {
    @Inject
    MyTopicsPresenter presenter;

    @Inject
    SectionedLoadMoreRecyclerAdapter<Bomb> bombAdapter;

    public MyTopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<MyTopicPath.Component>getDaggerComponent(context).inject(this);

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
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    SectionedLoadMoreRecyclerAdapter getAdapter() {
        return bombAdapter;
    }
}
