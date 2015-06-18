package com.jadenine.circle.model.entity;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp implements Savable{
    private final String user;
    private final String ap;

    @SerializedName("ssid")
    private String ssid;

    //TODO Date
    private long timestamp;

    public UserAp(String user, String ap, String ssid) {
        this.user = user;
        this.ap = ap;
        this.ssid = ssid;
    }

    public String getAP() {
        return ap;
    }

    public String getUser() {
        return user;
    }

    public String getSSID() {
        return ssid;
    }

    @Override
    public String toString(){
        return TextUtils.isEmpty(getSSID())?getAP(): getSSID();
    }

    @Override
    public void save() {
        //TODO
    }

    public void setSSID(String SSID) {
        this.ssid = SSID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
