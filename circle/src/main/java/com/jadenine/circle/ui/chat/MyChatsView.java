package com.jadenine.circle.ui.chat;

import android.content.Context;
import android.util.AttributeSet;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import javax.inject.Inject;

/**
 * Created by linym on 7/27/15.
 */
public class MyChatsView extends RefreshableHomeView {

    @Inject
    MyChatsPresenter presenter;

    @Inject
    MyChatsAdapter chatAdapter;

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
    }

    @Override
    public void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    public MyChatsAdapter getAdapter() {
        return chatAdapter;
    }
}
