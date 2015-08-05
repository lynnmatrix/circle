package com.jadenine.circle.ui.topic.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

/**
 * Created by linym on 7/24/15.
 */
public class TopicDetailView extends LinearLayout{
    @InjectView(R.id.recycler_view)
    RecyclerView bombList;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.reply_toolbar)
    Toolbar replyToolbar;

    @InjectView(R.id.message_edit)
    EditText replyEditor;

    @Inject
    TopicDetailPresenter presenter;

    @Inject
    Drawable errorDrawable;

    @Inject
    BombListAdapter bombListAdapter;

    public TopicDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<TopicDetailPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        bombList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bombList.setLayoutManager(linearLayoutManager);
        bombList.setAdapter(bombListAdapter);

        configToolbar();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    protected void configToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_actionbar_back_light);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftKeyboardToggler.toggleInputMethod(replyEditor, false);
                Flow.get(getContext()).goBack();
            }
        });
    }

    @OnClick(R.id.send)
    public void onSend(){
        presenter.send();
    }

    BombListAdapter getBombAdapter() {
        return bombListAdapter;
    }

}
