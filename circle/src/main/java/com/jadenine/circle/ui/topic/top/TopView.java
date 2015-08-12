package com.jadenine.circle.ui.topic.top;

import android.content.Context;
import android.util.AttributeSet;

import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.topic.TopicListAdapter;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import javax.inject.Inject;

/**
 * Created by linym on 8/11/15.
 */
public class TopView extends RefreshableHomeView{
    @Inject
    TopPresenter presenter;

    @Inject
    TopicListAdapter topAdapter;

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<TopPath.Component>getDaggerComponent(context).inject(this);
        setRefreshableListener(presenter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);

        setAdapter(topAdapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    TopicListAdapter getAdapter(){
        return topAdapter;
    }
}
