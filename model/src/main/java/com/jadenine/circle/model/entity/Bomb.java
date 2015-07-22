package com.jadenine.circle.model.entity;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by linym on 7/21/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class Bomb extends CircleBaseModel implements Identifiable<Long> {

    @PrimaryKey
    String messageId;
    String ap;

    private String rootMessageId;
    private String rootUser;

    private String from;
    private String to;

    private String content;

    private String images;

    public String getAp() {
        return ap;
    }
    public void setAp(String ap) {
        this.ap = ap;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public String getTo() {
        return to;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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
