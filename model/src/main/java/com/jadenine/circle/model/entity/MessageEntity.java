package com.jadenine.circle.model.entity;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by linym on 6/3/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class MessageEntity extends CircleBaseModel{
    @PrimaryKey
    String messageId;
    String topicId;

    String user;
    String content;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getMessageId() {
        return messageId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
