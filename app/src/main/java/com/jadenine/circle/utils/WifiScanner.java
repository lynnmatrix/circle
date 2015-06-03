package com.jadenine.circle.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linym on 5/29/15.
 */
public class WifiScanner {
    private static final String TAG = WifiScanner.class.getSimpleName();

    public interface ScanCallback{
        /**
         * Called after wifi mac scanned.
         * @param result mac address list for wifi routers
         */
        void onWifiScanned(List<String> result);
    }

    public static void scanner(Context context, final ScanCallback callback) {
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                List<ScanResult> results = wifiManager.getScanResults();
                List<String> wifiInfoResults = new ArrayList<String>(results.size());
                for (ScanResult ap : results) {
                    wifiInfoResults.add(ap.BSSID);
                }
                if(null != callback) {
                    callback.onWifiScanned(wifiInfoResults);
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

}
