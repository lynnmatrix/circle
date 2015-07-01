package com.jadenine.circle.ui.composer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.utils.ToolbarColorizer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import flow.Flow;

/**
 * Created by linym on 6/9/15.
 */
@DaggerScope(ComposerPresenter.class)
public class ComposerView extends RelativeLayout implements PreferenceManager.OnActivityResultListener {
    @InjectView(R.id.message_edit)
    EditText editor;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.imageview)
    ImageView imageView;

    @Inject
    ComposerPresenter presenter;

    @Inject
    Activity activity;

    public ComposerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ComposerPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        toolbar.inflateMenu(R.menu.menu_message_add);
        toolbar.setNavigationIcon(R.drawable.ic_actionbar_back_dark);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Flow.get(getContext()).goBack();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(R.id.action_send == item.getItemId()) {
                    onClickSend();
                    return true;
                } else if(R.id.action_add_image == item.getItemId()) {
                    presenter.pickImage();
                    return true;
                }
                return false;
            }
        });

        ToolbarColorizer.colorizeToolbar(toolbar, Color.WHITE, activity);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return presenter.onActivityResult(requestCode, resultCode, data);
    }

    void onClickSend(){
        presenter.send(editor.getText().toString());
    }
}
