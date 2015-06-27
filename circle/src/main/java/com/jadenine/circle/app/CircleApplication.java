package com.jadenine.circle.app;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.dagger.DomainComponent;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.utils.Device;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import javax.inject.Inject;

import dagger.Module;
import mortar.MortarScope;

/**
 * Created by linym on 6/6/15.
 */
@DaggerScope(CircleApplication.class)
public class CircleApplication extends Application {
    public static final String ROOT_SCOPE_NAME = "Root";
    private MortarScope rootScope;

    @Inject
    Account account;

    @Override
    public void onCreate() {
        super.onCreate();
        com.jadenine.circle.domain.dagger.DaggerService.init(Device.getDeviceId(this));

        AppComponent appComponent = DaggerCircleApplication_AppComponent.builder()
                .domainComponent(com.jadenine.circle.domain.dagger.DaggerService.getDomainComponent())
                .appModule(new AppModule(this)).build();

        MortarScope.Builder builder = MortarScope.buildRootScope();
        builder.withService(DaggerService.SERVICE_NAME, appComponent);

        rootScope = builder.build(ROOT_SCOPE_NAME);

        appComponent.inject(this);

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
        if (null!=rootScope && rootScope.hasService(name)) {
            return rootScope.getService(name);
        }

        return super.getSystemService(name);
    }

    @DaggerScope(CircleApplication.class)
    @dagger.Component(dependencies = DomainComponent.class, modules = {AppModule.class})
    public interface AppComponent {
        void inject(CircleApplication app);
        Account account();
    }

    @Module
    public static class AppModule {

        private final Context appContext;
        public AppModule(Application application){
            this.appContext = application;
        }

    }
}
