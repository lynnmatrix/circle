package com.jadenine.circle.app;

import android.app.Application;

import com.jadenine.circle.BuildConfig;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.request.ApService;
import com.jadenine.circle.request.MessageService;
import com.jadenine.circle.ui.ApFragment;
import com.jadenine.circle.ui.MessageActivity;
import com.jadenine.circle.ui.MessageAddActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mortar.MortarScope;
import retrofit.RestAdapter;

/**
 * Created by linym on 6/6/15.
 */
public class CircleApplication extends Application {
    public static final String ROOT_SCOPE_NAME = "Root";
    private MortarScope rootScope;

    @Override
    public Object getSystemService(String name) {
        if (rootScope == null) {
            AppComponent appComponent = DaggerCircleApplication_AppComponent.builder()
                    .serviceModule(new ServiceModule()).build();

            MortarScope.Builder builder = MortarScope.buildRootScope();
            builder.withService(DaggerService.SERVICE_NAME, appComponent);

            rootScope = builder.build(ROOT_SCOPE_NAME);
        }

        if (rootScope.hasService(name)) {
            return rootScope.getService(name);
        }

        return super.getSystemService(name);
    }

    @Singleton
    @dagger.Component(modules = {ServiceModule.class})
    public interface AppComponent {

        void inject(ApFragment apFragment);
        void inject(MessageActivity messageActivity);
        void inject(MessageAddActivity messageAddActivity);
    }

    /**
     * Created by linym on 6/6/15.
     */
    @Module(
            includes = {RestAdapterModule.class}
    )
    static class ServiceModule {
        public static final RestAdapter.LogLevel LOGLEVEL = BuildConfig.DEBUG ? RestAdapter.LogLevel
                .FULL : RestAdapter.LogLevel.NONE;

        public static final String ENDPOINT_LOCAL = "http://192.168.9.220:8080";
        public static final String ENDPOINT_AZURE = "https://circle.chinacloudsites.cn:443";
        public static final String ENDPOINT = BuildConfig.DEBUG?ENDPOINT_LOCAL:ENDPOINT_AZURE;

        private final RestAdapter restAdapter;

        public ServiceModule(){
            restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
            restAdapter.setLogLevel(LOGLEVEL);
        }

        @Provides @Singleton
        ApService getApService(){
            return restAdapter.create(ApService.class);
        }

        @Provides @Singleton
        MessageService getMessageService(){
            return  restAdapter.create(MessageService.class);
        }
    }
    /**
     * Created by linym on 6/6/15.
     */

    @Module
    static class RestAdapterModule {

        public static final RestAdapter.LogLevel LOGLEVEL = BuildConfig.DEBUG ? RestAdapter.LogLevel
                .FULL : RestAdapter.LogLevel.NONE;
        public static final String ENDPOINT_LOCAL = "http://192.168.9.220:8080";
        public static final String ENDPOINT_AZURE = "https://circle.chinacloudsites.cn:443";

        public static final String ENDPOINT = BuildConfig.DEBUG?ENDPOINT_LOCAL:ENDPOINT_AZURE;
        @Provides
        @Singleton
        public RestAdapter getRestAdapter(){
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
            restAdapter.setLogLevel(LOGLEVEL);
            return restAdapter;
        }

    }
}
