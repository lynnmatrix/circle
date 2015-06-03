package com.jadenine.circle.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by linym on 6/3/15.
 */
public class ApUtils {

    /**
     * @param context
     * @return MAC address of connected AP
     */
    public static String getConnectedAP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentAPAddress = null;
        if (null != wifiInfo) {
            currentAPAddress = wifiInfo.getMacAddress();
        }
        return currentAPAddress;
    }
}
