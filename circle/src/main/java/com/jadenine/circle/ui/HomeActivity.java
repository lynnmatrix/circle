package com.jadenine.circle.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.jadenine.circle.BuildConfig;
import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.mortar.MortarPathContainerView;
import com.jadenine.circle.ui.chat.MyChatPath;
import com.jadenine.circle.ui.menu.DrawerMenuView;
import com.jadenine.circle.ui.topic.TopicListPath;
import com.jadenine.circle.ui.welcome.WelcomePath;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

import javax.inject.Inject;

import butterknife.InjectView;
import flow.Flow;
import flow.History;

@Container(R.layout.activity_home)
public class HomeActivity extends MortarActivity {
    private static final String ARGUMENT_CIRCLE_ID = "circleID";
    private static final String INTENT_ACTION_OPEN_CIRCLE = "OPEN_CIRCLE";
    private static final String INTENT_ACTION_OPEN_CHAT = "OPEN_CHAT";

    @InjectView(R.id.nav_drawer)
    DrawerLayout drawerLayout;

    @InjectView(R.id.nav_view)
    DrawerMenuView navigationView;

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

        if (savedInstanceState == null) {
            handleNotificationIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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

    public static Intent getOpenCircleIntent(Context context, String circleId) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(ARGUMENT_CIRCLE_ID, circleId);
        i.setAction(INTENT_ACTION_OPEN_CIRCLE);
        return i;
    }

    public static Intent getOpenChatIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(INTENT_ACTION_OPEN_CHAT);
        return i;
    }

    private void handleNotificationIntent(Intent intent) {
        String action = intent.getAction();
        if (INTENT_ACTION_OPEN_CIRCLE.equals(action)) {
            String circleId = intent.getStringExtra(ARGUMENT_CIRCLE_ID);
            if (circleId != null) {
                handleSwitchToCircle(circleId);
            }
        } else if (INTENT_ACTION_OPEN_CHAT.equals(action)) {
            handleSwitchToChat();
        }
    }

    private void handleSwitchToCircle(String circleId) {
        History.Builder historyBuilder = Flow.get(this).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.push(new TopicListPath(circleId));

        Flow.get(this).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
    }

    private void handleSwitchToChat() {
        History.Builder historyBuilder = Flow.get(this).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.push(new MyChatPath());

        Flow.get(this).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
    }
}