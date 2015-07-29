package com.jadenine.circle.domain;

import com.jadenine.circle.model.db.DirectMessageDBService;
import com.jadenine.circle.model.db.TimelineCursorDBService;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.TimelineRangeResult;
import com.jadenine.circle.model.state.TimelineRangeCursor;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 7/20/15.
 */
public class ChatLoader implements RangeLoader<DirectMessageEntity> {
    private final int pageCount;
    private final String account;
    @Inject
    DirectMessageService messageService;
    @Inject DirectMessageDBService messageDBService;
    @Inject TimelineCursorDBService cursorDBService;

    public ChatLoader(String account, int pageCount) {
        this.account = account;
        this.pageCount = pageCount;
    }

    @Override
    public Observable<TimelineRangeResult<DirectMessageEntity>> refresh(Long top) {
        return messageService.listMessages(account, pageCount, top, null);
    }

    @Override
    public Observable<TimelineRangeResult<DirectMessageEntity>> loadMore(Long bottom) {
        return messageService.listMessages(account, pageCount, null, bottom);
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
