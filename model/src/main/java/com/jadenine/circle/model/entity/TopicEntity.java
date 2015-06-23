package com.jadenine.circle.model.entity;

/**
 * Created by linym on 6/10/15.
 */
public class TopicEntity implements Savable{
    private String topicId;
    private String ap;

    private String user;
    private String topic;

    private String latestMessageId;

    private long timestamp;

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

    @Override
    public void save() {
        //TODO
    }
}
