package com.jadenine.circle.model.entity;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by linym on 6/10/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class TopicEntity extends BaseModel implements Savable{
    @PrimaryKey
    String topicId;
    String ap;

    String user;
    String topic;

    String latestMessageId;

    long timestamp;

    public TopicEntity(){}

    public TopicEntity(String ap, String user, String topic) {
        this.ap = ap;
        this.user = user;
        this.topic = topic;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getAp() {
        return ap;
    }

    public void setAp(String ap) {
        this.ap = ap;
    }

    public String getUser() {
        return user;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String content) {
        this.topic = content;
    }

    public String getLatestMessageId() {
        return latestMessageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setLatestMessageId(String lastMessageId) {
        this.latestMessageId = lastMessageId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
