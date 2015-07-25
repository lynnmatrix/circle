package com.jadenine.circle.app;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.jadenine.circle.BuildConfig;
import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.dagger.DaggerDomainComponentProduction;
import com.jadenine.circle.domain.dagger.DomainComponentProduction;
import com.jadenine.circle.domain.dagger.DomainModule;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.utils.Device;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import mortar.MortarScope;
import timber.log.Timber;

/**
 * Created by linym on 6/6/15.
 */
@DaggerScope(CircleApplication.class)
public class CircleApplication extends Application {
    public static final String ROOT_SCOPE_NAME = "Root";
    private MortarScope rootScope;

    private RefWatcher refWatcher;

    @Inject
    Account account;

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        DomainComponentProduction domainComponent = DaggerDomainComponentProduction.builder()
                .domainModule(new DomainModule(Device.getDeviceId(this))).build();

        com.jadenine.circle.domain.dagger.DaggerService.setComponent(domainComponent);

        AppComponent appComponent = DaggerCircleApplication_AppComponent.builder()
                .domainComponentProduction(domainComponent)
                .appModule(new AppModule(this)).build();

        MortarScope.Builder builder = MortarScope.buildRootScope();
        builder.withService(DaggerService.SERVICE_NAME, appComponent);

        rootScope = builder.build(ROOT_SCOPE_NAME);

        appComponent.inject(this);

        registerUmengMessageHandler();

        FlowManager.init(this);

        refWatcher = installLeakCanary();
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

    public static RefWatcher getRefWatcher(Context context) {
        CircleApplication application = (CircleApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    protected RefWatcher installLeakCanary() {
        if(BuildConfig.DEBUG) {
            return LeakCanary.install(this);
        }
        return RefWatcher.DISABLED;
    }

    @DaggerScope(CircleApplication.class)
    @dagger.Component(dependencies = DomainComponentProduction.class, modules = {AppModule.class})
    public interface AppComponent {
        void inject(CircleApplication app);
        Account account();
        AvatarBinder avatarBinder();
    }

    @Module
    public static class AppModule {

        private final Context appContext;
        public AppModule(Application application){
            this.appContext = application;
        }

        @Provides
        @DaggerScope(CircleApplication.class)
        AvatarBinder provideAvatarBinder() {
            return new AvatarBinder(appContext);
        }

    }
}
