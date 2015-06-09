package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jadenine.circle.mortar.DaggerService;

import javax.inject.Inject;

/**
 * Created by linym on 6/8/15.
 */
public class ApListView extends ListView {
    @Inject
    ApListPresenter presenter;

    public ApListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ApListPath.Component>getDaggerComponent(context).inject(this);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onApSelected(position);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }
}
