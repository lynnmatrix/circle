package com.jadenine.circle.entity;

import com.google.gson.Gson;

/**
 * Created by linym on 6/3/15.
 */
public class Message {
    private String messageId;
    private String ap;
    private String user;
    private String content;

    public String getAp() {
        return ap;
    }

    public void setAp(String ap) {
        this.ap = ap;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString(){
        return new Gson().toJson(this).toString();
    }

}
