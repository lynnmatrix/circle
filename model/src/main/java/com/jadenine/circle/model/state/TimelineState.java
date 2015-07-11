package com.jadenine.circle.model.state;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by linym on 7/11/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class TimelineState extends BaseModel{

    @PrimaryKey
    String ap;
    String oldestTopicId;
    Long latestTimestamp;

    boolean hasMoreTopic = true;

    TimelineState(){}

    public TimelineState(String ap) {
        this.ap = ap;
    }
    public String getOldestTopicId() {
        return oldestTopicId;
    }

    public void setOldestTopicId(String oldestTopicId) {
        this.oldestTopicId = oldestTopicId;
    }

    public Long getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(Long latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }

    public boolean getHasMoreTopic() {
        return hasMoreTopic;
    }

    public void setHasMoreTopic(boolean hasMoreTopic) {
        this.hasMoreTopic = hasMoreTopic;
    }

}
