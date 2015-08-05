package com.jadenine.circle.domain;

import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.rest.TimelineRangeResult;
import com.jadenine.circle.model.state.TimelineEntity;
import com.jadenine.circle.model.state.TimelineRangeCursor;
import com.jadenine.circle.model.state.TimelineType;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 7/15/15.
 */
interface RangeLoader<T extends Identifiable<Long>>{

    Observable<TimelineRangeResult<T>> refresh(Long top);

    Observable<TimelineRangeResult<T>> loadMore(Long beforeId, Long sinceId);

    Observable<TimelineEntity> loadTimeline(String timeline, TimelineType timelineType);

    Observable<List<TimelineRangeCursor>> loadTimelineRangeCursors(String timeline);

    Observable<List<T>> loadTimelineRange(Long top, Long bottom);
}
