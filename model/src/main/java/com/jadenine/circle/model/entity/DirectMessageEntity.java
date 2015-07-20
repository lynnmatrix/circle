package com.jadenine.circle.model.entity;

import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by linym on 7/15/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class DirectMessageEntity extends CircleBaseModel{

    @PrimaryKey
    String messageId;

    String ap;
    String topicId;

    String rootMessageId;
    String rootUser;

    String from;
    String to;
    String content;

    DirectMessageEntity(){}

    DirectMessageEntity(String ap, String topicId, String from, String to) {
        this.ap = ap;
        this.topicId = topicId;
        this.from = from;
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getAp() {
        return ap;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getRootMessageId() {
        return rootMessageId;
    }

    public String getRootUser() {
        return rootUser;
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
}
