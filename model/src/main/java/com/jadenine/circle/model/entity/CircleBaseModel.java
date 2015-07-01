package com.jadenine.circle.model.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by linym on 6/24/15.
 */
public abstract class CircleBaseModel extends BaseModel implements Savable{
    @Column
    String etag;

    @Column
    long timestamp;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
