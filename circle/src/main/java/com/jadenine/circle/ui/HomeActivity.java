package com.jadenine.circle.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.mortar.MortarPathContainerView;
import com.jadenine.circle.ui.ap.ApListPath;
import com.jadenine.circle.ui.scanner.WifiPath;
import com.umeng.update.UmengUpdateAgent;

import butterknife.InjectView;
import flow.Flow;

@Container(R.layout.activity_home)
public class HomeActivity extends MortarActivity {

    @InjectView(R.id.nav_drawer)
    DrawerLayout drawerLayout;

    @InjectView(R.id.nav_view)
    NavigationView navigationView;

    @InjectView(R.id.container)
    MortarPathContainerView pathContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.update(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_share:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        share();
                        return true;
                    case R.id.item_wifi_scan:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        scanWifi();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected Object buildDaggerService() {
        return DaggerHomeComponent.builder().appComponent((CircleApplication.AppComponent)
                DaggerService.getDaggerComponent(getApplicationContext())).homeActivityModule(new
                HomeActivityModule(this)).build();
    }

    @Override
    protected Object getFirstScreen() {
        return new ApListPath();
    }

    @Override
    protected Flow.Dispatcher getFlowDispatcher() {
        return new Flow.Dispatcher() {
            @Override
            public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {

                pathContainerView.dispatch(traversal, new Flow.TraversalCallback() {
                    @Override
                    public void onTraversalCompleted() {
                        callback.onTraversalCompleted();
                    }
                });
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (pathContainerView.onBackPressed()) {
            return;
        }
        if (flowDelegate.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }


    private void scanWifi() {
        Flow.get(this).set(new WifiPath());
    }

    private void share() {
        Context context = this;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        Bitmap bt= BitmapFactory.decodeResource(context.getResources(), R.drawable.starry_night);
        final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bt,
                null, null));

        String shareDescription = context.getString(R.string.share_message_description, context
                .getString(R.string.app_name), context.getString(R.string.share_link));

        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareDescription);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(shareIntent);
    }

}