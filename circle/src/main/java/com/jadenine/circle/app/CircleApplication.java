package com.jadenine.circle.app;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.jadenine.circle.BuildConfig;
import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.request.MessageService;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import dagger.Module;
import dagger.Provides;
import mortar.MortarScope;
import retrofit.RestAdapter;

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
        AppComponent appComponent = DaggerCircleApplication_AppComponent.builder().restAdapterModule(new RestAdapterModule()).build();

        MortarScope.Builder builder = MortarScope.buildRootScope();
        builder.withService(DaggerService.SERVICE_NAME, appComponent);

        rootScope = builder.build(ROOT_SCOPE_NAME);

        registerUmengMessageHandler();
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
        pushAgent.setNotificaitonOnForeground(true);
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
    @dagger.Component(modules = {RestAdapterModule.class})
    public interface AppComponent {
        RestAdapter restAdapter();
    }

    @Module
    public static class RestAdapterModule {

        public static final RestAdapter.LogLevel LOGLEVEL = BuildConfig.DEBUG ? RestAdapter.LogLevel
                .FULL : RestAdapter.LogLevel.NONE;
        public static final String ENDPOINT_LOCAL = "http://192.168.9.220:8080";
        public static final String ENDPOINT_AZURE = "https://circle.chinacloudsites.cn:443";
        public static final boolean FORE_AZURE = false;

        public static final String ENDPOINT = BuildConfig.DEBUG && !FORE_AZURE ? ENDPOINT_LOCAL :
                ENDPOINT_AZURE;

        @Provides
        @DaggerScope(CircleApplication.class)
        public RestAdapter provideRestAdapter(){
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
            restAdapter.setLogLevel(LOGLEVEL);
            return restAdapter;
        }

        @Provides
        @DaggerScope(CircleApplication.class)
        public MessageService provideMessageService(RestAdapter restAdapter) {
            return restAdapter.create(MessageService.class);
        }

    }
}
