package com.jadenine.circle.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.jadenine.circle.R;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;
import com.jadenine.circle.utils.ToolbarColorizer;

import javax.inject.Inject;

/**
 * Created by linym on 7/27/15.
 */
public class MyChatsView extends RefreshableHomeView {

    @Inject
    MyChatsPresenter presenter;

    @Inject
    Activity activity;

    @Inject
    SectionedLoadMoreRecyclerAdapter<DirectMessageEntity> chatAdapter;

    @Inject
    public MyChatsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<MyChatPath.Component>getDaggerComponent(context).inject(this);

        setRefreshableListener(presenter);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);

        setAdapter(chatAdapter);

        getToolbar().setTitle(R.string.title_private_chat);
        ToolbarColorizer.colorizeToolbar(getToolbar(), Color.WHITE, activity);
    }

    @Override
    public void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    SectionedLoadMoreRecyclerAdapter<DirectMessageEntity> getAdapter() {
        return chatAdapter;
    }
}
