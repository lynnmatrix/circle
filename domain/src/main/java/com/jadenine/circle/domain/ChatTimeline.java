package com.jadenine.circle.domain;

/**
 * Created by linym on 7/15/15.
 */
public class ChatTimeline {
    private static final String MY_CHATS_TIME_LINE = "MyChats";

    private final BaseTimeline<Message> messageTimeline;

    public ChatTimeline(RangeLoader<Message> loader) {
        messageTimeline = new BaseTimeline<Message>(loader) {
            @Override
            protected String getTimelineId() {
                return MY_CHATS_TIME_LINE;
            }
        };
    }
}
