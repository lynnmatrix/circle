package com.jadenine.circle.mortar;

import android.content.Context;

/**
 * Created by linym on 6/6/15.
 */
public class DaggerService {

    public final static String SERVICE_NAME = "CIRCLE_SERVICE_NAME";

    public static <T> T getDaggerComponent(Context context) {
        //noinspection ResourceType
        return (T)context.getSystemService(SERVICE_NAME);
    }
}
