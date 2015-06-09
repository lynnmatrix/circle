package com.jadenine.circle.ui.message.composer;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/9/15.
 */
@DaggerScope(MessageComposerPresenter.class)
public class ComposerView extends RelativeLayout {
    @InjectView(R.id.message_edit)
    EditText editor;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    MessageComposerPresenter presenter;

    public ComposerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<MessageAddPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        toolbar.inflateMenu(R.menu.menu_message_add);
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
