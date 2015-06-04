package com.jadenine.circle.request;

import com.jadenine.circle.BuildConfig;

import retrofit.RestAdapter;

/**
 * Created by linym on 6/3/15.
 */
public class ServiceProvider {
    public static final RestAdapter.LogLevel LOGLEVEL = BuildConfig.DEBUG?RestAdapter.LogLevel
            .FULL: RestAdapter.LogLevel.NONE;

    public static final String ENDPOINT_LOCAL = "http://192.168.9.220:8080";
    public static final String ENDPOINT_AZURE = "https://circle.chinacloudsites.cn:443";
    public static final String ENDPOINT = ENDPOINT_AZURE;

    private static ServiceProvider sInstance;

    private final ApService apService;
    private final MessageService messageService;

    private ServiceProvider(){
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT)
                .build();
        restAdapter.setLogLevel(LOGLEVEL);
        apService = restAdapter.create(ApService.class);
        messageService = restAdapter.create(MessageService.class);
    }

    private static synchronized ServiceProvider getInstance(){
        if(null == sInstance) {
            sInstance = new ServiceProvider();
        }
        return sInstance;
    }

    public static ApService provideApService() {
        return getInstance().apService;
    }

    public static MessageService provideMessageService(){
        return getInstance().messageService;
    }
}
