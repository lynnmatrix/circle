package com.jadenine.circle.ui.topic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.graphics.Palette;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new
                RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                presenter.onOpenTopic(position);
            }
        }));
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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                collapsingToolbarLayout.setContentScrimColor(mutedColor);
            }
        });
    }

    @OnClick(R.id.fab_add_message)
    public void onAddMessage(){
        presenter.addMessage();
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
