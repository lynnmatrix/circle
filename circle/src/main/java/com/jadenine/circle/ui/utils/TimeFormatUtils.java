package com.jadenine.circle.ui.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by linym on 7/24/15.
 */
public class TimeFormatUtils {
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    public static String getFormattedTime(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
}
