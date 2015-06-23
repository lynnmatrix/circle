package com.jadenine.circle.model.entity;

import android.text.TextUtils;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by linym on 6/3/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class UserApEntity extends BaseModel implements Savable{
    @PrimaryKey
    String ap;

    String user;

    String ssid;

    //TODO Date
    long timestamp;

    public UserApEntity(){}

    public UserApEntity(String user, String ap, String ssid) {
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

    public void setSSID(String SSID) {
        this.ssid = SSID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString(){
        return TextUtils.isEmpty(getSSID())?getAP(): getSSID();
    }

}
