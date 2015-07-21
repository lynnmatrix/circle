package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.DirectMessageEntity;

/**
 * Created by linym on 7/15/15.
 */
public class ChatTimeline {
    private static final String MY_CHATS_TIME_LINE = "MyChats";

    private final BaseTimeline<DirectMessageEntity> messageTimeline;

    public ChatTimeline(RangeLoader<DirectMessageEntity> loader) {
        messageTimeline = new BaseTimeline<>(MY_CHATS_TIME_LINE, loader);
    }
}
