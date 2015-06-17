package com.jadenine.circle.model.entity;

/**
 * Created by linym on 6/10/15.
 */
public class Topic {
    private String topicId;
    private String ap;

    private String user;
    private String topic;

    private String latestMessageId;

    private long timestamp;

    public String getTopicId() {
        return topicId;
    }

    public String getAp() {
        return ap;
    }

    public String getUser() {
        return user;
    }

    public String getTopic() {
        return topic;
    }

    public String getLatestMessageId() {
        return latestMessageId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
