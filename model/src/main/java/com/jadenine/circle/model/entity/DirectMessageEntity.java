package com.jadenine.circle.model.entity;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by linym on 7/15/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class DirectMessageEntity extends CircleBaseModel implements IdentifiableEntity {

    @PrimaryKey
    String messageId;

    String circle;
    String topicId;

    String rootMessageId;
    String rootUser;

    @Column(name = "fromUser") // 'from' is sql keyword
    String from;
    @Column(name = "toUser") // 'to' is sql keyword
    String to;
    String content;

    boolean unread;

    DirectMessageEntity(){}

    public DirectMessageEntity(@NonNull String circle, @NonNull String topicId, @NonNull String from, @NonNull String to) {
        this.circle = circle;
        this.topicId = topicId;
        this.from = from;
        this.to = to;
    }


    public String getMessageId() {
        return messageId;
    }

    public String getCircle() {
        return circle;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getRootMessageId() {
        return rootMessageId;
    }

    public void setRootMessageId(String rootMessageId) {
        this.rootMessageId = rootMessageId;
    }
    public String getRootUser() {
        return rootUser;
    }

    public void setRootUser(String rootUser) {
        this.rootUser = rootUser;
    }
    public String getTo() {
        return to;
    }
    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean getUnread() {
        return unread;
    }

    @Override
    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    @NonNull
    @Override
    public Long getId() {
        return Long.valueOf(getMessageId());
    }

    @NonNull
    @Override
    public Long getGroupId() {
        return Long.valueOf(getRootMessageId());
    }

}
