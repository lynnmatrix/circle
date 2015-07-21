package com.jadenine.circle.domain;

import com.jadenine.circle.model.db.impl.DirectMessageDBService;
import com.jadenine.circle.model.db.impl.TimelineCursorDBService;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.jadenine.circle.model.state.TimelineRangeCursor;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 7/20/15.
 */
public class ChatLoader implements RangeLoader<DirectMessageEntity> {
    private final int pageCount;
    private final Account account;
    private final DirectMessageService messageService;
    private final DirectMessageDBService messageDBService;
    private final TimelineCursorDBService cursorDBService;

    public ChatLoader(Account account, DirectMessageService service, DirectMessageDBService
            dbService, TimelineCursorDBService timelineCursorDBService, int pageCount) {
        this.account = account;
        this.messageService = service;
        this.messageDBService = dbService;
        this.cursorDBService = timelineCursorDBService;
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

    @Override
    public Observable<List<TimelineRangeCursor>> loadTimelineRangeCursors(String timeline) {
        return cursorDBService.loadTimelineRangeCursors(timeline);
    }

    @Override
    public Observable<List<DirectMessageEntity>> loadTimelineRange(Long top, Long bottom) {
        return messageDBService.listMessage(bottom + 1, top - 1);
    }
}
