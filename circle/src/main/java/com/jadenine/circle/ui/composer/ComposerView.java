package com.jadenine.circle.ui.composer;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import flow.Flow;

/**
 * Created by linym on 6/9/15.
 */
@DaggerScope(ComposerPresenter.class)
public class ComposerView extends RelativeLayout {
    @InjectView(R.id.message_edit)
    EditText editor;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    ComposerPresenter presenter;

    public ComposerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ComposerPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        toolbar.setTitle(R.string.title_activity_message_add);

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
                }
                return false;
            }
        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }


    void onClickSend(){
        presenter.send(editor.getText().toString());
    }
}
