package com.jadenine.circle.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.utils.ApUtils;

public class ConnectedApReceiver extends BroadcastReceiver {
    public ConnectedApReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(null == activeNetworkInfo) {
            return;
        }
        if(activeNetworkInfo.isConnected() && ConnectivityManager.TYPE_WIFI == activeNetworkInfo
                .getType()) {

            ApUtils.AP ap = ApUtils.getConnectedAP(context);
            if(null != ap) {
                BusProvider.post(new EventProducer.APConnectedEvent(ap));
            }
        }
    }


}
