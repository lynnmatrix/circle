package com.jadenine.circle.domain;

import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public class Message implements Updatable<com.jadenine.circle.model.entity.Message>{
    private final com.jadenine.circle.model.entity.Message entity;

    public static Message bind(com.jadenine.circle.model.entity.Message entity) {
        return new Message(entity);
    }

    public Message(){
        this(new com.jadenine.circle.model.entity.Message());
    }

    public Message(com.jadenine.circle.model.entity.Message entity) {
        this.entity = entity;
    }

    com.jadenine.circle.model.entity.Message getEntity() {
        return entity;
    }

    public String getTopicId() {
        return entity.getTopicId();
    }
    public void setTopicId(String topicId) {
        entity.setTopicId(topicId);
    }

    public String getMessageId() {
        return entity.getMessageId();
    }

    public void setMessageId(String messageId) {
        entity.setMessageId(messageId);
    }

    public String getUser() {
        return entity.getUser();
    }

    public void setUser(String user) {
        this.entity.setUser(user);
    }

    public String getContent() {
        return entity.getContent();
    }

    public void setContent(String content) {
        entity.setContent(content);
    }

    public long getTimestamp() {
        return entity.getTimestamp();
    }

    public void setTimestamp(long timestamp) {
        entity.setTimestamp(timestamp);
    }

    @Override
    public void merge(com.jadenine.circle.model.entity.Message entity) {
        if(entity.getTimestamp() - this.entity.getTimestamp() > 0) {
            this.entity.setTimestamp(entity.getTimestamp());
            this.entity.save();
        }
    }

    public Observable<Message> reply(Topic topic) {
        return topic.addReply(this);
    }
}
