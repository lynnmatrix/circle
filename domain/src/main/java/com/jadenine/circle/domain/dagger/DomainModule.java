package com.jadenine.circle.domain.dagger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.BuildConfig;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.db.impl.ApDBServiceImpl;
import com.jadenine.circle.model.db.impl.BombDBService;
import com.jadenine.circle.model.db.impl.DirectMessageDBService;
import com.jadenine.circle.model.db.impl.MessageDBServiceImpl;
import com.jadenine.circle.model.db.impl.TimelineCursorDBService;
import com.jadenine.circle.model.db.impl.TimelineStateDBService;
import com.jadenine.circle.model.db.impl.TopicDBServiceImpl;
import com.jadenine.circle.model.entity.GsonIgnore;
import com.jadenine.circle.model.rest.ApService;
import com.jadenine.circle.model.rest.AzureBlobUploader;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.ImageService;
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
    public static final String ENDPOINT_LOCAL = "http://192.168.9.117:8080";
    public static final String ENDPOINT_AZURE = "https://circle.chinacloudsites.cn:443";
    public static final boolean FORE_AZURE = false;

    public static final String ENDPOINT = BuildConfig.DEBUG && !FORE_AZURE ? ENDPOINT_LOCAL :
            ENDPOINT_AZURE;

    private final String deviceId;

    public DomainModule(String deviceId) {
        this.deviceId = deviceId;
    }

    @Provides
    @Singleton
    public Account provideAccount() {
        return new Account(deviceId);
    }

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(){
        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
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
    TimelineCursorDBService provideTimelineCursorDBService() {
        return new TimelineCursorDBService();
    }

    @Provides
    @Singleton
    TimelineStateDBService provideTimelineStateService(){
        return new TimelineStateDBService();
    }

//    @Provides
//    @Singleton
//    BombComposer provideBombComposer(ImageService messageService, AzureBlobUploader blobUploader) {
//        return new BombComposer(messageService, blobUploader);
//    }

}

