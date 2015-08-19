package com.jadenine.circle.model.entity;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by linym on 8/19/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class ApEntity extends CircleBaseModel{
    @PrimaryKey
    String mac;

    String circle;

    String ssid;

    ApEntity(){}

    public ApEntity(String mac, String ssid) {
        setMac(mac);
        setSSID(ssid);
    }

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
