package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerService;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/8/15.
 */
public class ApListView extends RelativeLayout {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.ap_list_view)
    ListView apListView;

    @Inject
    ApListPresenter presenter;

    public ApListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ApListPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        presenter.takeView(this);

        toolbar.setTitle(R.string.title_activity_ap);
        toolbar.inflateMenu(R.menu.menu_ap);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_list_ap:
                        presenter.loadAPList();
                        return true;
                }
                return false;
            }
        });

        apListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onApSelected(position);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }
}
