package com.jadenine.circle.mortar;

import android.content.Context;

/**
 * Created by linym on 6/6/15.
 */
public class DaggerService {

    public final static String SERVICE_NAME = "CIRCLE_SERVICE_NAME";

    private static <T> T getComponent(Context context, String serviceName) {
        //noinspection ResourceType
        return (T) context.getApplicationContext().getSystemService(serviceName);
    }

    public static <T> T getDaggerComponent(Context context) {
        return DaggerService.getComponent(context, SERVICE_NAME);
    }
}
