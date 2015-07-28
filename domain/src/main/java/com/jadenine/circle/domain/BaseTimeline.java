package com.jadenine.circle.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jadenine.circle.model.entity.IdentifiableEntity;
import com.jadenine.circle.model.state.TimelineRangeCursor;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.android.internal.Preconditions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linym on 7/15/15.
 */
public class BaseTimeline<T extends IdentifiableEntity> implements
        Loadable<TimelineRange<T>> {

    private final LinkedList<TimelineRange<T>> rangeList = new LinkedList<>();

    private final RangeLoader<T> loader;
    private final String timeline;
    private AtomicBoolean cursorDBLoaded = new AtomicBoolean(false);

    public BaseTimeline(String timeline, RangeLoader<T> loader) {
        Preconditions.checkNotNull(loader, "Null loader for timeline.");
        this.timeline = timeline;
        this.loader = loader;
    }

    private int getRangeCount() {
        return rangeList.size();
    }

    public @NotNull List<TimelineRange<T>> getAllRanges(){
        return new ArrayList<>(rangeList);
    }

    public @NotNull TimelineRange getFirstRange() {
        TimelineRange firstRange;
        if(getRangeCount() > 0) {
            firstRange = rangeList.getFirst();
        } else {
            firstRange = new TimelineRange(getTimelineId(), new ArrayList(), loader);
            rangeList.add(firstRange);
        }
        return firstRange;
    }

    public @NotNull TimelineRange<T> getLastRange(){
        TimelineRange lastRange;
        if(getRangeCount() > 0) {
            lastRange = rangeList.getLast();
        } else {
            lastRange = new TimelineRange(getTimelineId(), new ArrayList(), loader);
            rangeList.add(lastRange);
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

    private @Nullable TimelineRange<T> getPreviousRange(TimelineRange<T> range) {
        TimelineRange previous = null;
        Iterator<TimelineRange<T>> rangeIterator = rangeList.descendingIterator();
        while (rangeIterator.hasNext()){
            TimelineRange currentRange = rangeIterator.next();
            if( currentRange == range) {
                return previous;
            }
            previous = rangeIterator.next();
        }
        return null;
    }

    protected String getTimelineId() {
        return this.timeline;
    }

    @Override
    public @NotNull Observable<List<TimelineRange<T>>> refresh() {
        if(!cursorDBLoaded.get()) {
            return loader.loadTimelineRangeCursors(getTimelineId()).flatMap(new Func1<List<TimelineRangeCursor>, Observable<List<TimelineRange<T>>>>() {
                @Override
                public Observable<List<TimelineRange<T>>> call(List<TimelineRangeCursor> timelineRangeCursors) {
                    if(rangeList.isEmpty()) {
                        for(TimelineRangeCursor cursor : timelineRangeCursors) {
                            rangeList.add(new TimelineRange<>(cursor, loader));
                        }
                    }
                    cursorDBLoaded.set(true);
                    return innerRefresh();
                }
            }).subscribeOn(Schedulers.io());
        } else {
            return innerRefresh();
        }
    }

    @NonNull
    private Observable<List<TimelineRange<T>>> innerRefresh() {
        final TimelineRange<T> refreshRange = getFirstRange();
        final Long originalTop = refreshRange.cursor.getTop();
        final boolean rangeDBLoaded = refreshRange.isDBLoaded();
        final boolean emptyRange = null == originalTop;

        return refreshRange.refresh().flatMap(new Func1<TimelineRange<T>,
                Observable<List<TimelineRange<T>>>>() {
            @Override
            public Observable<List<TimelineRange<T>>> call(TimelineRange<T> ts) {

                if (refreshRange != ts) {
                    if(emptyRange) {
                        rangeList.remove(refreshRange);
                    }
                    rangeList.add(0, ts);
                    group(ts.getAll(), rangeDBLoaded);
                } else {
                    if(emptyRange) {
                        group(ts.getAll(), rangeDBLoaded);
                    } else if(refreshRange.cursor.getTop() < originalTop){
                        group(ts.getSubRange(refreshRange.cursor.getTop(), originalTop),
                                rangeDBLoaded);
                    } else if(!rangeDBLoaded) {
                        group(ts.getAll(), rangeDBLoaded);
                    }
                }
                return Observable.just(getAllRanges());
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public @NotNull Observable<List<TimelineRange<T>>> loadMore() {
        if(!cursorDBLoaded.get()) {
            Timber.w("TimelineCursor should be loaded before loading more.");
            return Observable.empty();
        }

        TimelineRange<T> rangeToLoadMore = getRangeToLoadMore();

        return loadMore(rangeToLoadMore);
    }

    private TimelineRange<T> getRangeToLoadMore() {
        TimelineRange<T> rangeToLoadMore = null;
        for(TimelineRange range : rangeList) {
            if(!range.isDBLoaded()){
                rangeToLoadMore = range;
                break;
            }
        }
        if(null == rangeToLoadMore){
            rangeToLoadMore = getLastRange();
        }
        return rangeToLoadMore;
    }

    public  @NotNull Observable<List<TimelineRange<T>>> loadMore(TimelineRange<T> range) {
        final Long originalBottom = range.cursor.getBottom();
        final boolean rangeDbLoaded = range.isDBLoaded();

       return range.loadMore().flatMap(new Func1<TimelineRange<T>,
               Observable<List<TimelineRange<T>>>>() {
           @Override
           public Observable<List<TimelineRange<T>>> call(TimelineRange<T> range) {
               if (range.cursor.getBottom() > originalBottom) {
                   List<T> entities = range.getSubRange(originalBottom, range.cursor.getBottom()
                           + 1);
                   for (T entity : entities) {
                       range.group(entity);
                   }
                   if (!range.hasMore()) {
                       TimelineRange<T> previousRange = getPreviousRange(range);
                       if (null != previousRange) {
                           range.contact(previousRange);
                           previousRange.cursor.delete();
                       }
                   }
                   TransactionManager.getInstance().addTransaction(new SaveModelTransaction
                           (ProcessModelInfo.withModels(range.cursor)));

                   TransactionManager.getInstance().addTransaction(new SaveModelTransaction
                           (ProcessModelInfo.withModels(entities)));
               } else if (!rangeDbLoaded) {
                   for (T entity : range.getAll()) {
                       range.group(entity);
                   }
               }

               return Observable.just(getAllRanges());
           }
       }).subscribeOn(Schedulers.io());
    }

    @Override
    public boolean hasMore() {
        return getRangeToLoadMore().hasMore();
    }

    private void group(Collection<T> entities, boolean fromRest) {
        TimelineRange<T> range;
        Set<TimelineRangeCursor> cursorsNeedSave = new LinkedHashSet<>();
        for(T entity : entities) {
            range = getRange(entity.getGroupId());
            range.group(entity);
            cursorsNeedSave.add(range.cursor);
        }

        if(fromRest) {
            TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(cursorsNeedSave)));

            TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(entities)));
        }
    }

    public void addPublished(T entity) {
        //TODO
    }

}
