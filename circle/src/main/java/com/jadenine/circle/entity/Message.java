package com.jadenine.circle.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by linym on 6/3/15.
 */
public class Message {
    private String messageId;
    private String ap;
    private String user;
    private String content;
    private long timestamp;

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getFormattedTime(){
        return dateFormat.format(new Date(timestamp));
    }

    @Override
    public String toString(){
        return user + "\n" +dateFormat.format(new Date(timestamp))  + "\n" + content;
    }

}
