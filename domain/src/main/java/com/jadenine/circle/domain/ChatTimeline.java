package com.jadenine.circle.domain;

/**
 * Created by linym on 7/15/15.
 */
public class ChatTimeline extends BaseTimeline<Chat> {

    public static final String MY_CHATS_TIME_LINE = "MyChats";

    public ChatTimeline(RangeLoader<Chat> loader) {
        super(loader);
    }

    @Override
    protected String getTimeline() {
        return MY_CHATS_TIME_LINE;
    }
}
