package com.jadenine.circle.model.entity;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * Created by linym on 6/10/15.
 */
@Table(databaseName = CircleDatabase.NAME)
public class TopicEntity extends CircleBaseModel {
    public static final String IMAGE_DELIMITER = ",";

    @PrimaryKey
    @Column(name = "topicId")
    String topicId;

    @Column(name = "ap")
    String ap;

    @Column(name = "user")
    String user;
    @Column(name = "topic")
    String topic;

    @Column(name = "images")
    String images;

    @Column(name = "createdTimestamp")
    long createdTimestamp;

    @Column(name = "messageCount")
    int messageCount;

    @Column(name = "latestMessageId")
    String latestMessageId;

    List<MessageEntity> messages;

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

    public List<MessageEntity> getMessages(){
        return messages;
    }
    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }


}
