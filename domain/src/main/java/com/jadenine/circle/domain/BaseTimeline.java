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
            lastRange = new TimelineRange(getTimeline(), new ArrayList(), loader);
        }
        return lastRange;
    }

    public @NotNull TimelineRange<T> getLastRange(){
        TimelineRange lastRange;
        if(getRangeCount() > 0) {
            lastRange = rangeList.getLast();
        } else {
            lastRange = new TimelineRange(getTimeline(), new ArrayList(), loader);
        }
        return lastRange;
    }

    protected abstract String getTimeline();

    @Override
    public @NotNull Observable<List<TimelineRange<T>>> refresh() {
        final TimelineRange<T> firstRange = getFirstRange();
        return firstRange.refresh().flatMap(new Func1<TimelineRange<T>,
                Observable<List<TimelineRange<T>>>>() {
            @Override
            public Observable<List<TimelineRange<T>>> call(TimelineRange<T> ts) {
                if(firstRange != ts) {
                    rangeList.add(0, ts);
                }
                return Observable.just(getAllRanges());
            }
        });
    }

    @Override
    public @NotNull Observable<List<TimelineRange<T>>> loadMore() {
        //TODO load more for the last range which has one visible range at least.
        //For now, we load more for the last range which may be not visible now.
        final TimelineRange<T> lastRange = getLastRange();
        return lastRange.loadMore().flatMap(new Func1<TimelineRange<T>,
                Observable<List<TimelineRange<T>>>>() {
            @Override
            public Observable<List<TimelineRange<T>>> call(TimelineRange<T> ts) {
                return Observable.just(getAllRanges());
            }
        });
    }

    @Override
    public boolean hasMore() {
        return getLastRange().hasMore();
    }
}
