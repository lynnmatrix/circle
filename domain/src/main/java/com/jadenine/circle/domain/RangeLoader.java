package com.jadenine.circle.domain;

import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.jadenine.circle.model.state.TimelineRangeCursor;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 7/15/15.
 */
public interface RangeLoader<T extends Identifiable<Long>>{

    Observable<JSONListWrapper<T>> refresh(Long top);

    Observable<JSONListWrapper<T>> loadMore(Long bottom);

    Observable<List<TimelineRangeCursor>> loadTimelineRangeCursors(String timeline);

    Observable<List<T>> loadTimelineRange(Long top, Long bottom);
}
