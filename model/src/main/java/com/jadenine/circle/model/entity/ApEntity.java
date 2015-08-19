package com.jadenine.circle.model.entity;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;

/**
 * Created by linym on 8/19/15.
 */
public class ApEntity {
    @PrimaryKey
    String mac;

    String circle;

    String ssid;

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getMac(){
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSSID() {
        return ssid;
    }

    public void setSSID(String SSID) {
        this.ssid = SSID;
    }
}
