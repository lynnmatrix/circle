package com.jadenine.circle.model.entity;

/**
 * Created by linym on 7/1/15.
 */
public class Image {
    private String mediaId;

    private String writableSas;

    private String readableSas;

    public String getMediaId() {
        return mediaId;
    }

    public String getWritableSas() {
        return writableSas;
    }

    public String getReadableSas() {
        return readableSas;
    }

    public void setReadableSas(String readableSas) {
        this.readableSas = readableSas;
    }
}
