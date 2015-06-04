package com.jadenine.circle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

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

        if(activeNetworkInfo.isConnected() && ConnectivityManager.TYPE_WIFI == activeNetworkInfo
                .getType()) {

            String macAddress = ApUtils.getConnectedAP(context);
            if(!TextUtils.isEmpty(macAddress)) {
                BusProvider.getInstance().post(new EventProducer.APConnectedEvent(macAddress));
            }
        }
    }


}
