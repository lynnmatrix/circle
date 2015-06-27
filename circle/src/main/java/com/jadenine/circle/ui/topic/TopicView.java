package com.jadenine.circle.ui.topic;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

/**
 * Created by linym on 6/10/15.
 */
@DaggerScope(TopicPresenter.class)
public class TopicView extends CoordinatorLayout{

    @InjectView(R.id.scrollableview)
    RecyclerView recyclerView;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @InjectView(R.id.anim_toolbar)
    Toolbar toolbar;

    @Inject
    TopicPresenter presenter;

    public TopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<TopicPath.Component>getDaggerComponent(context).inject(this);
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
                presenter.onOpenTopic(position);
            }
        }));

        recyclerView.addOnScrollListener(new AutoLoadMoreListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                presenter.loadMore();
            }
        });
        configToolbar();

        getTopicAdapter();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    protected void configToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_actionbar_back_light);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Flow.get(getContext()).goBack();
            }
        });
    }

    @OnClick(R.id.fab_add_message)
    public void onAddMessage(){
        presenter.addTopic();
    }

    TopicRecyclerAdapter getTopicAdapter() {
        TopicRecyclerAdapter topicRecyclerAdapter = (TopicRecyclerAdapter) recyclerView.getAdapter();
        if(null == topicRecyclerAdapter) {
            topicRecyclerAdapter = new TopicRecyclerAdapter();
            recyclerView.setAdapter(topicRecyclerAdapter);
        }
        return topicRecyclerAdapter;
    }
}
