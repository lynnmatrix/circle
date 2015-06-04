package com.jadenine.circle.entity;

import android.text.TextUtils;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp {
    private final String user;
    private final String ap;

    private final String ssid;

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

    public String getSsid() {
        return ssid;
    }

    @Override
    public String toString(){
        return TextUtils.isEmpty(getSsid())?getAP():getSsid();
    }
}
