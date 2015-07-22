package com.jadenine.circle.domain;

import com.jadenine.circle.model.db.impl.BombDBService;
import com.jadenine.circle.model.db.impl.TimelineCursorDBService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.jadenine.circle.model.state.TimelineRangeCursor;

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
    TimelineCursorDBService cursorDBService;

    public BombLoader(String ap, int pageCount){
        this.ap = ap;
        this.pageCount = pageCount;
    }

    @Override
    public Observable<JSONListWrapper<Bomb>> refresh(Long top) {
        return restService.list(ap, pageCount, top, null);
    }

    @Override
    public Observable<JSONListWrapper<Bomb>> loadMore(Long bottom) {
        return restService.list(ap, pageCount, null, bottom);
    }

    @Override
    public Observable<List<TimelineRangeCursor>> loadTimelineRangeCursors(String timeline) {
        return cursorDBService.loadTimelineRangeCursors(timeline);
    }

    @Override
    public Observable<List<Bomb>> loadTimelineRange(Long top, Long bottom) {
        return dbService.listMessage(bottom + 1, top - 1);
    }
}
