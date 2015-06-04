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
    public static AP getConnectedAP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (null != wifiInfo) {
            return new AP(wifiInfo.getBSSID(), wifiInfo.getSSID());
        }
        return null;
    }

    public static class AP{
        private final String BSSID, SSID;

        private AP(String BSSID, String SSID) {
            this.BSSID = BSSID;
            this.SSID = SSID;
        }

        public String getBSSID() {
            return BSSID;
        }

        public String getSSID() {
            return SSID;
        }
    }
}
