package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.JSONListWrapper;

import rx.Observable;

/**
 * Created by linym on 7/20/15.
 */
public class ChatLoader implements RangeLoader<DirectMessageEntity> {
    public static final int PAGE_COUNT = 100;
    private final Account account;
    private final DirectMessageService messageService;

    public ChatLoader(Account account, DirectMessageService service) {
        this.account = account;
        this.messageService = service;
    }

    @Override
    public Observable<JSONListWrapper<DirectMessageEntity>> refresh(Long top, Integer count) {
        return messageService.listMessages(account.getDeviceId(), PAGE_COUNT, top, null);
    }

    @Override
    public Observable<JSONListWrapper<DirectMessageEntity>> loadMore(Long bottom, Integer Count) {
        return messageService.listMessages(account.getDeviceId(), PAGE_COUNT, null, bottom);
    }
}
