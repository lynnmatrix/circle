package com.jadenine.circle.ui.detail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import com.jadenine.circle.ui.topic.RecyclerItemClickListener;
import com.jadenine.circle.utils.ToolbarColorizer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

/**
 * Created by linym on 7/24/15.
 */
public class BombGroupDetailView extends LinearLayout{
    @InjectView(R.id.bomb_list)
    RecyclerView bombList;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.reply_toolbar)
    Toolbar replyToolbar;

    @InjectView(R.id.message_edit)
    EditText replyEditor;

    @Inject
    Activity activity;

    @Inject
    BombGroupPresenter presenter;

    @Inject
    Drawable errorDrawable;

    public BombGroupDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<BombGroupPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        bombList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bombList.setLayoutManager(linearLayoutManager);

        bombList.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Long bombId = (Long) view.getTag();
                        presenter.setReplyTo(bombId);
                    }
                }));

        configToolbar();
        getBombAdapter();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    protected void configToolbar() {
        ToolbarColorizer.colorizeToolbar(toolbar, Color.WHITE, activity);
        toolbar.setNavigationIcon(R.drawable.ic_actionbar_back_light);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Flow.get(getContext()).goBack();
            }
        });
    }

    @OnClick(R.id.send)
    public void onSend(){
        presenter.send();
    }

    BombRecyclerAdapter getBombAdapter() {
        BombRecyclerAdapter messageAdapter = (BombRecyclerAdapter) bombList.getAdapter();
        if(null == messageAdapter) {
            messageAdapter = new BombRecyclerAdapter(errorDrawable);
            bombList.setAdapter(messageAdapter);
        }

        return messageAdapter;
    }

}
