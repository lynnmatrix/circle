package com.jadenine.circle.ui.chat;

import android.content.Context;
import android.util.AttributeSet;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import javax.inject.Inject;

/**
 * Created by linym on 7/27/15.
 */
public class MyChatsView extends RefreshableHomeView {

    @Inject
    MyChatsPresenter presenter;

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

        setAdapter(chatAdapter);

        getToolbar().setTitle(R.string.title_private_chat);
        presenter.takeView(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    SectionedLoadMoreRecyclerAdapter<DirectMessageEntity> getAdapter() {
        return chatAdapter;
    }
}
