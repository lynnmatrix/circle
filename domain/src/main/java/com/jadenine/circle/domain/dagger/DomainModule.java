package com.jadenine.circle.domain.dagger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadenine.circle.domain.BuildConfig;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.db.impl.ApDBServiceImpl;
import com.jadenine.circle.model.db.impl.MessageDBServiceImpl;
import com.jadenine.circle.model.db.impl.TopicDBServiceImpl;
import com.jadenine.circle.model.rest.ApService;
import com.jadenine.circle.model.rest.MessageService;
import com.jadenine.circle.model.rest.TopicService;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import javax.inject.Singleton;

import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by linym on 6/18/15.
 */
@dagger.Module
public class DomainModule {

    public static final RestAdapter.LogLevel LOGLEVEL = BuildConfig.DEBUG ? RestAdapter.LogLevel
            .FULL : RestAdapter.LogLevel.NONE;
    public static final String ENDPOINT_LOCAL = "http://192.168.9.220:8080";
    public static final String ENDPOINT_AZURE = "https://circle.chinacloudsites.cn:443";
    public static final boolean FORE_AZURE = false;

    public static final String ENDPOINT = BuildConfig.DEBUG && !FORE_AZURE ? ENDPOINT_LOCAL :
            ENDPOINT_AZURE;

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(){
        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaredClass().equals(ModelAdapter.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(LOGLEVEL)
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();
        return restAdapter;
    }

    @Provides
    @Singleton
    ApService provideApRestService(RestAdapter restAdapter) {
        return restAdapter.create(ApService.class);
    }

    @Provides
    @Singleton
    TopicService provideTopicRestService(RestAdapter restAdapter) {
        return restAdapter.create(TopicService.class);
    }

    @Provides
    @Singleton
    MessageService provideMessageService(RestAdapter restAdapter) {
        return restAdapter.create(MessageService.class);
    }

    @Provides
    @Singleton
    ApDBService provideApDBService(){
        return new ApDBServiceImpl();
    }

    @Provides
    @Singleton
    TopicDBService provideTopicDBService(){
        return new TopicDBServiceImpl();
    }

    @Provides
    @Singleton
    MessageDBService provideMessageDBService(){
        return new MessageDBServiceImpl();
    }

}

