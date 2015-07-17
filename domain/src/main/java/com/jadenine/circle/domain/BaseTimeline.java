package com.jadenine.circle.domain;

import com.raizlabs.android.dbflow.annotation.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.android.internal.Preconditions;
import rx.functions.Func1;

/**
 * Created by linym on 7/15/15.
 */
public abstract class BaseTimeline<T extends Identifiable<Long> > implements Loadable<TimelineRange<T>> {

    private final LinkedList<TimelineRange<T>> rangeList = new LinkedList<>();

    private final RangeLoader<T> loader;
    public BaseTimeline(RangeLoader<T> loader) {
        Preconditions.checkNotNull(loader, "Null loader for timeline.");
        this.loader = loader;
    }

    private int getRangeCount() {
        return rangeList.size();
    }

    private @NotNull List<TimelineRange<T>> getAllRanges(){
        return new ArrayList<>(rangeList);
    }

    public @NotNull TimelineRange getFirstRange() {
        TimelineRange lastRange;
        if(getRangeCount() > 0) {
            lastRange = rangeList.getFirst();
        } else {
            lastRange = new TimelineRange(getTimelineId(), new ArrayList(), loader);
        }
        return lastRange;
    }

    public @NotNull TimelineRange<T> getLastRange(){
        TimelineRange lastRange;
        if(getRangeCount() > 0) {
            lastRange = rangeList.getLast();
        } else {
            lastRange = new TimelineRange(getTimelineId(), new ArrayList(), loader);
        }
        return lastRange;
    }

    public @NotNull TimelineRange<T> getRange(Long id) {
        if(rangeList.size() < 0) {
            throw new IllegalStateException("Empty range list.");
        }
        TimelineRange<T> result = null;
        for(TimelineRange range : rangeList) {
            if(id >= range.cursor.getTop()) {
                result = range;
                if (id <=range.cursor.getBottom()){
                    break;// current range contains the id.
                }
            } else {
                break;// No range contains the id now, but the last range may include this is
                // after loading more.
            }
        }
        if(null == result) {
            throw new IllegalStateException("No range found for id:" +id + ", which should not happened." );
        }
        return result;
    }

    protected abstract String getTimelineId();

    @Override
    public @NotNull Observable<List<TimelineRange<T>>> refresh() {
        final TimelineRange<T> firstRange = getFirstRange();
        final Long originalTop = firstRange.cursor.getTop();
        return firstRange.refresh().flatMap(new Func1<TimelineRange<T>,
                Observable<List<TimelineRange<T>>>>() {
            @Override
            public Observable<List<TimelineRange<T>>> call(TimelineRange<T> ts) {
                if(firstRange != ts) {
                    rangeList.add(0, ts);
                    group(ts.getAll());
                } else if(firstRange.cursor.getTop() < originalTop) {
                    group(ts.getSubRange(firstRange.cursor.getTop(), originalTop));
                }
                return Observable.just(getAllRanges());
            }
        });
    }

    @Override
    public @NotNull Observable<List<TimelineRange<T>>> loadMore() {
        final TimelineRange<T> lastRange = getLastRange();
        final Long originalBottom = lastRange.cursor.getBottom();

        return lastRange.loadMore().flatMap(new Func1<TimelineRange<T>,
                Observable<List<TimelineRange<T>>>>() {
            @Override
            public Observable<List<TimelineRange<T>>> call(TimelineRange<T> range) {
                if(range.cursor.getBottom() > originalBottom) {
                    for(T entity : range.getSubRange(originalBottom, range.cursor.getBottom() +
                            1)) {
                        range.group(entity);
                    }
                }
                return Observable.just(getAllRanges());
            }
        });
    }

    @Override
    public boolean hasMore() {
        return getLastRange().hasMore();
    }

    private void group(List<T> entities) {
        TimelineRange<T> range;
        for(T entity : entities) {
            range = getRange(entity.getGroupId());
            range.group(entity);
        }
    }
}
