package com.jadenine.circle.model.entity;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by linym on 8/19/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class CircleEntity extends CircleBaseModel {
    @PrimaryKey
    String circleId;

    String name;

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId){
        this.circleId = circleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
