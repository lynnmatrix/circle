package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.UserAp;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/8/15.
 */
@DaggerScope(ApListPresenter.class)
public class ApListView extends RelativeLayout {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

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

        setUpToolbar();

        apListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onApSelected(position);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.orange, R.color.green);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadAPList();
            }
        });
    }

    private void setUpToolbar() {
        toolbar.inflateMenu(R.menu.drawer);
        toolbar.setTitle(R.string.title_activity_ap);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_share:
                        share();
                        return true;
                    case R.id.item_wifi_scan:
                        scanWifi();
                        return true;
                }
                return false;
            }
        });
    }

    private void scanWifi() {
        //TODO
    }

    private void share() {
        Context context = getContext();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        Bitmap bt= BitmapFactory.decodeResource(context.getResources(), R
                .drawable.starry_night);
        final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContext()
                        .getContentResolver(),
                bt, null,null));

        String shareDescription = context.getString(R.string.share_message_description, context
                .getString(R.string.app_name), context.getString(R.string.share_link));

        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareDescription);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(shareIntent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    ArrayAdapter<UserAp> getApAdapter() {
        ArrayAdapter<UserAp> apListViewAdapter = (ArrayAdapter<UserAp>) apListView.getAdapter();
        if (null == apListViewAdapter) {
            apListViewAdapter = new ArrayAdapter<>(getContext(), android.R.layout
                    .simple_list_item_1, new ArrayList<UserAp>(0));
            apListView.setAdapter(apListViewAdapter);
        }
        return apListViewAdapter;
    }
}
