package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.entity.MessageEntity;

import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public class Message implements Updatable<MessageEntity>, Identifiable<Long> {
    private final MessageEntity entity;
    private Long messageId;
    private Long groupId;

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

    @NonNull
    @Override
    public Long getId() {
        if(null == messageId && null != getMessageId()) {
            messageId = Long.valueOf(getMessageId());
        }
        return messageId;
    }

    @NonNull
    @Override
    public Long getGroupId() {
        if(null == groupId && null != getTopicId()) {
            groupId = Long.valueOf(getTopicId());
        }
        return groupId;
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

    public boolean isPrivate() {
        return getEntity().getPrivacy();
    }

    public void setIsPrivate(boolean isPrivate, String replyTo) {
        getEntity().setPrivacy(isPrivate);
        getEntity().setReplyToUser(replyTo);
    }

    @Override
    public void merge(MessageEntity entity) {
        if(entity.getTimestamp() - this.entity.getTimestamp() > 0) {
            this.entity.setTimestamp(entity.getTimestamp());
            this.entity.save();
        }
    }

    public Observable<Message> reply(Topic topic) {
        return topic.addReply(this);
    }

    public Observable<Message> reply(Chat chat) {
        return chat.send(this);
    }

}
