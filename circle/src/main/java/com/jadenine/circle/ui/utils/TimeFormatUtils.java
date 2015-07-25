package com.jadenine.circle.ui.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

/**
 * Created by linym on 7/24/15.
 */
public class TimeFormatUtils {
    private final static PrettyTime prettyTimer = new PrettyTime();

    public static String getFormattedTime(long timestamp) {
        return prettyTimer.format(new Date(timestamp));
    }
}
