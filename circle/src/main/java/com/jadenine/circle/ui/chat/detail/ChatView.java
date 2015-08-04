package com.jadenine.circle.ui.chat.detail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.jadenine.circle.utils.ToolbarColorizer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

/**
 * Created by linym on 7/25/15.
 */
public class ChatView extends LinearLayout {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.recycler_view)
    RecyclerView messageList;

    @InjectView(R.id.reply_toolbar)
    Toolbar replyToolbar;

    @InjectView(R.id.message_edit)
    EditText replyEditor;

    @Inject
    Activity activity;

    @Inject
    ChatAdapter chatAdapter;

    @Inject
    ChatPresenter presenter;

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ChatPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);


        messageList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        messageList.setLayoutManager(linearLayoutManager);

        messageList.setAdapter(chatAdapter);

        configToolbar();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        activity = null;
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    protected void configToolbar() {
        ToolbarColorizer.colorizeToolbar(toolbar, Color.WHITE, activity);
        toolbar.setTitle(R.string.title_private_chat);
        toolbar.setNavigationIcon(R.drawable.ic_actionbar_back_light);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftKeyboardToggler.toggleInputMethod(replyEditor, false);
                Flow.get(getContext()).goBack();
            }
        });
    }

    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

    @OnClick(R.id.send)
    public void onSend(){
        presenter.send();
    }
}
