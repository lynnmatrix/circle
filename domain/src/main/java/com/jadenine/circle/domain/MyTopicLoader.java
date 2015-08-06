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
 * Created by linym on 8/6/15.
 */
public class MyTopicLoader implements RangeLoader<Bomb> {
    private final String auth;
    private final int pageCount;

    @Inject
    BombService restService;

    @Inject
    BombDBService dbService;

    @Inject
    TimelineDBService timelineDBService;

    @Inject
    TimelineCursorDBService cursorDBService;

    public MyTopicLoader(String auth, int pageCount) {
        this.auth = auth;
        this.pageCount = pageCount;
    }

    @Override
    public Observable<TimelineRangeResult<Bomb>> refresh(Long top) {
        return restService.myTopicsTimeline(auth, pageCount, top, null);
    }

    @Override
    public Observable<TimelineRangeResult<Bomb>> loadMore(Long beforeId, Long sinceId) {
        return restService.myTopicsTimeline(auth, pageCount, sinceId, beforeId);
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
        return dbService.myTopics(auth, bottom + 1, top - 1);
    }
}
