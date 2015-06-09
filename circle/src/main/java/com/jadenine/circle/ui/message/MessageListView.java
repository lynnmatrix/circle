package com.jadenine.circle.ui.message;

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
import android.view.MenuItem;
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
 * Created by linym on 6/9/15.
 */
@DaggerScope(MessagePresenter.class)
public class MessageListView extends CoordinatorLayout {
    @InjectView(R.id.scrollableview)
    RecyclerView messageRecyclerView;

    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.anim_toolbar)
    Toolbar toolbar;

    @Inject
    MessagePresenter presenter;

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

        configToolbaor();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    private void configToolbaor() {
        collapsingToolbarLayout.setTitle(getContext().getString(R.string
                .title_activity_message));

        toolbar.inflateMenu(R.menu.menu_message);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh:
                        presenter.loadMessages();
                        return true;
                }
                return false;
            }
        });

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

    MessageRecyclerAdapter getMessageAdapter() {
        MessageRecyclerAdapter messageAdapter = (MessageRecyclerAdapter) messageRecyclerView.getAdapter();
        if(null == messageAdapter) {
            messageAdapter = new MessageRecyclerAdapter();
            messageRecyclerView.setAdapter(messageAdapter);
        }

        return messageAdapter;
    }
}
