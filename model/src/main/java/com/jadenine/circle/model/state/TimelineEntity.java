package com.jadenine.circle.model.state;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.security.InvalidParameterException;

/**
 * Created by linym on 8/3/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class TimelineEntity extends BaseModel{
    @PrimaryKey
    String id;

    TimelineType type;

    boolean unread; // 'read' is a reserved sql keyword

    TimelineEntity(){}

    public TimelineEntity(String timeline, TimelineType timelineType) {
        this.id = timeline;
        this.type = timelineType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TimelineType getType() {
        return type;
    }

    public void setType(TimelineType type) {
        this.type = type;
    }

    public boolean getUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public void merge(TimelineEntity timelineEntity) {
        if(!timelineEntity.getId().equals(getId()) || !timelineEntity.getType().equals(getType())
                ) {
            throw new InvalidParameterException("try to merge timeline which is not identical to" +
                    " current timeline.");
        }

        this.unread = timelineEntity.getUnread();
    }
}
