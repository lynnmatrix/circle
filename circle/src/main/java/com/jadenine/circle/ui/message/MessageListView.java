package com.jadenine.circle.ui.message;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.topic.RecyclerItemClickListener;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

/**
 * Created by linym on 6/9/15.
 */
@DaggerScope(MessagePresenter.class)
public class MessageListView extends CoordinatorLayout{
    @InjectView(R.id.scrollableview)
    RecyclerView messageRecyclerView;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @InjectView(R.id.anim_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.reply_toolbar)
    Toolbar replyToolbar;

    @InjectView(R.id.private_checkbox)
    CheckBox privateCheckBox;

    @InjectView(R.id.message_edit)
    EditText replyEditor;

    @Inject
    MessagePresenter presenter;

    @Inject
    Activity activity;

    public MessageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<MessagePath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        messageRecyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        messageRecyclerView.setLayoutManager(linearLayoutManager);

        messageRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                presenter.setReplyTo(position);
            }
        }));

        configToolbar();
        getMessageAdapter();
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
                Flow.get(getContext()).goBack();
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.starry_night);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                int mutedLightColor = palette.getVibrantColor(Color.WHITE);
                collapsingToolbarLayout.setContentScrimColor(mutedColor);
//                ToolbarColorizer.colorizeToolbar(toolbar, mutedLightColor, activity);
            }
        });
        privateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.onPrivateCheckedChanged(isChecked);
            }
        });
    }

    @OnClick(R.id.fab_add_message)
    public void onSend(){
        presenter.send();
    }

    MessageRecyclerAdapter getMessageAdapter() {
        MessageRecyclerAdapter messageAdapter = (MessageRecyclerAdapter) messageRecyclerView.getAdapter();
        if(null == messageAdapter) {
            messageAdapter = new MessageRecyclerAdapter(presenter.getTopic());
            messageRecyclerView.setAdapter(messageAdapter);
        }

        return messageAdapter;
    }
}
