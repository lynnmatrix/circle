package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.MessageEntity;

import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public class Message implements Updatable<MessageEntity>{
    private final MessageEntity entity;

    public static Message build(MessageEntity entity) {
        return new Message(entity);
    }

    public Message(String ap, String topicId) {
        this(new MessageEntity(ap, topicId));
    }

    public Message(MessageEntity entity) {
        this.entity = entity;
    }

    public MessageEntity getEntity() {
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

    @Override
    public void merge(MessageEntity entity) {
        if(entity.getTimestamp() - this.entity.getTimestamp() > 0) {
            this.entity.setTimestamp(entity.getTimestamp());
            this.entity.save();
        }
    }

    @Override
    public void remove() {
        getEntity().delete();
    }

    public Observable<Message> reply(Topic topic) {
        return topic.addReply(this);
    }
}
