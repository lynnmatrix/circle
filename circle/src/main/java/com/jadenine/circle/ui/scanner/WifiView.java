package com.jadenine.circle.ui.scanner;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;

import javax.inject.Inject;

/**
 * Created by linym on 6/13/15.
 */
@DaggerScope(WifiPresenter.class)
public class WifiView extends RecyclerView {
    @Inject
    WifiPresenter presenter;

    public WifiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<WifiPath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        setLayoutManager(linearLayoutManager);

        setAdapter(new WifiAdapter());

        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }
}
