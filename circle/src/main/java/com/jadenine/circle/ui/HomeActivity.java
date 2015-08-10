package com.jadenine.circle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.jadenine.circle.BuildConfig;
import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.mortar.MortarPathContainerView;
import com.jadenine.circle.ui.menu.ApMenuView;
import com.jadenine.circle.ui.welcome.WelcomePath;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

import javax.inject.Inject;

import butterknife.InjectView;
import flow.Flow;

@Container(R.layout.activity_home)
public class HomeActivity extends MortarActivity {

    @InjectView(R.id.nav_drawer)
    DrawerLayout drawerLayout;

    @InjectView(R.id.nav_view)
    ApMenuView navigationView;

    @InjectView(R.id.container)
    MortarPathContainerView pathContainerView;

    @Inject
    HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UmengUpdateAgent.update(this);
        PushAgent.getInstance(this).setDebugMode(BuildConfig.DEBUG);
        PushAgent.getInstance(this).onAppStart();
        PushAgent.getInstance(this).enable();

        DaggerService.<HomeComponent>getDaggerComponent(this).inject(this);

        presenter.takeView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dropView(this);
    }

    @Override
    protected Object buildDaggerService() {
        return DaggerHomeComponent.builder().appComponent((CircleApplication.AppComponent)
                DaggerService.getDaggerComponent(getApplicationContext())).homeActivityModule(new
                HomeActivityModule()).build();
    }

    @Override
    protected Object getFirstScreen() {
        return new WelcomePath();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pathContainerView.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}