package com.jadenine.circle.domain;

import com.jadenine.circle.model.db.BombDBService;
import com.jadenine.circle.model.db.TimelineCursorDBService;
import com.jadenine.circle.model.db.TimelineDBService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.TimelineRangeResult;
import com.jadenine.circle.model.state.TimelineEntity;
import com.jadenine.circle.model.state.TimelineRangeCursor;
import com.jadenine.circle.model.state.TimelineType;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 7/22/15.
 */
public class BombLoader implements RangeLoader<Bomb> {
    private final String ap;
    private final int pageCount;
    @Inject
    BombService restService;

    @Inject
    BombDBService dbService;

    @Inject
    TimelineDBService timelineDBService;

    @Inject
    TimelineCursorDBService cursorDBService;

    public BombLoader(String ap, int pageCount){
        this.ap = ap;
        this.pageCount = pageCount;
    }

    @Override
    public Observable<TimelineRangeResult<Bomb>> refresh(Long top) {
        return restService.list(ap, pageCount, top, null);
    }

    @Override
    public Observable<TimelineRangeResult<Bomb>> loadMore(Long bottom, Long sinceId) {
        return restService.list(ap, pageCount, sinceId, bottom);
    }

    @Override
    public Observable<TimelineEntity> loadTimeline(String timeline, TimelineType timelineType) {
        return timelineDBService.load(timeline, timelineType);
    }

    @Override
    public Observable<List<TimelineRangeCursor>> loadTimelineRangeCursors(String timeline) {
        return cursorDBService.loadTimelineRangeCursors(timeline);
    }

    @Override
    public Observable<List<Bomb>> loadTimelineRange(Long top, Long bottom) {
        return dbService.listMessage(ap, bottom + 1, top - 1);
    }
}
