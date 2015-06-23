package com.jadenine.circle.app;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.utils.Device;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import dagger.Module;
import dagger.Provides;
import mortar.MortarScope;

/**
 * Created by linym on 6/6/15.
 */
@DaggerScope(CircleApplication.class)
public class CircleApplication extends Application {
    public static final String ROOT_SCOPE_NAME = "Root";
    private MortarScope rootScope;

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent appComponent = DaggerCircleApplication_AppComponent.builder()
                .appModule(new AppModule(this)).build();

        MortarScope.Builder builder = MortarScope.buildRootScope();
        builder.withService(DaggerService.SERVICE_NAME, appComponent);

        rootScope = builder.build(ROOT_SCOPE_NAME);

        registerUmengMessageHandler();

        FlowManager.init(this);
    }

    private void registerUmengMessageHandler() {
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public Notification getNotification(Context context, UMessage uMessage) {
                Log.i("PUSH", uMessage.custom);
                Toast.makeText(context, uMessage.custom, Toast.LENGTH_LONG).show();
                uMessage.title = getString(R.string.notification_title_new_topic);
                return super.getNotification(context, uMessage);
            }

            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Log.i("PUSH", msg.custom);
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.setNotificaitonOnForeground(false);
        pushAgent.setMessageHandler(messageHandler);
    }

    @Override
    public Object getSystemService(String name) {
        if (rootScope.hasService(name)) {
            return rootScope.getService(name);
        }

        return super.getSystemService(name);
    }

    @DaggerScope(CircleApplication.class)
    @dagger.Component(modules = {AppModule.class})
    public interface AppComponent {
        Account account();
    }

    @Module
    public static class AppModule {

        private final Context appContext;
        public AppModule(Application application){
            this.appContext = application;
        }

        @Provides
        @DaggerScope(CircleApplication.class)
        public Account provideAccount() {
            return new Account(Device.getDeviceId(appContext));
        }

    }
}
