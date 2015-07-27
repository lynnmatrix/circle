package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.rest.DirectMessageService;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 7/27/15.
 */
public class ChatComposer {
    private final DirectMessageService chatService;

    @Inject
    public ChatComposer(DirectMessageService chatService) {
        this.chatService = chatService;
    }

    public Observable<DirectMessageEntity> send(DirectMessageEntity chatMessage) {
        return chatService.addMessage(chatMessage);
    }
}
