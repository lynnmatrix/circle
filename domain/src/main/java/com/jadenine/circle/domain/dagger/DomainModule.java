package com.jadenine.circle.domain.dagger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.BuildConfig;
import com.jadenine.circle.domain.Constants;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.db.BombDBService;
import com.jadenine.circle.model.db.CircleDBService;
import com.jadenine.circle.model.db.DirectMessageDBService;
import com.jadenine.circle.model.db.TimelineCursorDBService;
import com.jadenine.circle.model.db.TimelineDBService;
import com.jadenine.circle.model.entity.GsonIgnore;
import com.jadenine.circle.model.rest.AzureBlobUploader;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.CircleService;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.ImageService;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;
import javax.net.ssl.SSLSocketFactory;

import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by linym on 6/18/15.
 */
@dagger.Module
public class DomainModule {

    public static final RestAdapter.LogLevel LOG_LEVEL = BuildConfig.DEBUG ? RestAdapter.LogLevel
            .FULL : RestAdapter.LogLevel.NONE;
    public static final boolean FORE_AZURE = false;

    public static final String ENDPOINT = BuildConfig.DEBUG && !FORE_AZURE ? Constants.ENDPOINT_LOCAL :
            Constants.ENDPOINT_AZURE;

    private final String deviceId;

    private final SSLSocketFactory socketFactory;

    public DomainModule(SSLSocketFactory socketFactory, String deviceId) {
        this.socketFactory = socketFactory;
        this.deviceId = deviceId;
    }

    @Provides
    @Singleton
    public Account provideAccount() {
        return new Account(deviceId);
    }

    @Provides
    @Singleton
    public Gson provideGson(){
        return  new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaredClass().equals(ModelAdapter.class) || null != f.getAnnotation
                        (GsonIgnore.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
    }

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(Gson gson) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setSslSocketFactory(socketFactory);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(LOG_LEVEL)
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();
        return restAdapter;
    }

    @Provides
    @Singleton
    CircleService provideCircleRestService(RestAdapter restAdapter) {
        return restAdapter.create(CircleService.class);
    }

    @Provides
    @Singleton
    DirectMessageService provideChatService(RestAdapter restAdapter) {
        return restAdapter.create(DirectMessageService.class);
    }

    @Provides
    @Singleton
    BombService provideBombService(RestAdapter restAdapter) {
        return restAdapter.create(BombService.class);
    }

    @Provides
    @Singleton
    ImageService provideImageService(RestAdapter restAdapter) {
        return restAdapter.create(ImageService.class);
    }

    @Provides
    @Singleton
    AzureBlobUploader provideBlobUploader(){
        return new AzureBlobUploader();
    }

    @Provides
    @Singleton
    ApDBService provideApDBService(){
        return new ApDBService();
    }

    @Provides
    @Singleton
    CircleDBService provideCircleDBService(){
        return new CircleDBService();
    }

    @Provides
    @Singleton
    DirectMessageDBService provideChatDBService() {
        return new DirectMessageDBService();
    }

    @Provides
    @Singleton
    BombDBService provideBombDBService() {
        return new BombDBService();
    }

    @Provides
    @Singleton
    TimelineDBService provideTimelineDBService() {
        return new TimelineDBService();
    }

    @Provides
    @Singleton
    TimelineCursorDBService provideTimelineCursorDBService() {
        return new TimelineCursorDBService();
    }

}

