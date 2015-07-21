package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.JSONListWrapper;

import rx.Observable;

/**
 * Created by linym on 7/20/15.
 */
public class ChatLoader implements RangeLoader<DirectMessageEntity> {
    private final int pageCount;
    private final Account account;
    private final DirectMessageService messageService;

    public ChatLoader(Account account, DirectMessageService service, int pageCount) {
        this.account = account;
        this.messageService = service;
        this.pageCount = pageCount;
    }

    @Override
    public Observable<JSONListWrapper<DirectMessageEntity>> refresh(Long top) {
        return messageService.listMessages(account.getDeviceId(), pageCount, top, null);
    }

    @Override
    public Observable<JSONListWrapper<DirectMessageEntity>> loadMore(Long bottom) {
        return messageService.listMessages(account.getDeviceId(), pageCount, null, bottom);
    }
}
