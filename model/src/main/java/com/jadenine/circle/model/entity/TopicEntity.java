package com.jadenine.circle.model.entity;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by linym on 6/10/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class TopicEntity extends CircleBaseModel {
    public static final String IMAGE_DELIMITER = ",";

    @PrimaryKey
    String topicId;
    String ap;

    String user;
    String topic;

    String images;

    long createdTimestamp;

    int messageCount;

    String latestMessageId;

    TopicEntity(){}

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

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getLatestMessageId() {
        return latestMessageId;
    }

    public void setLatestMessageId(String lastMessageId) {
        this.latestMessageId = lastMessageId;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}
