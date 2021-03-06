package com.jadenine.circle.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jadenine.circle.model.entity.IdentifiableEntity;
import com.jadenine.circle.model.state.TimelineEntity;
import com.jadenine.circle.model.state.TimelineRangeCursor;
import com.jadenine.circle.model.state.TimelineType;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.DeleteModelListTransaction;
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
public class BaseTimeline<T extends IdentifiableEntity>{

    private final LinkedList<TimelineRange<T>> rangeList = new LinkedList<>();

    private final RangeLoader<T> loader;
    private AtomicBoolean cursorDBLoaded = new AtomicBoolean(false);
    private final TimelineEntity entity;

    public BaseTimeline(String timeline, TimelineType type, RangeLoader<T> loader) {
        Preconditions.checkNotNull(loader, "Null loader for timeline.");
        this.loader = loader;
        this.entity = new TimelineEntity(timeline, type);
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
        return entity.getId();
    }

    public @NotNull Observable<List<TimelineRange<T>>> refresh() {
        if (!cursorDBLoaded.get()) {
            return loader.loadTimeline(getTimelineId(), entity.getType()).flatMap(new Func1<TimelineEntity, Observable<List<TimelineRange<T>>>>() {

                @Override
                public Observable<List<TimelineRange<T>>> call(TimelineEntity timelineEntity) {
                    if(null != timelineEntity) {
                        BaseTimeline.this.entity.merge(timelineEntity);
                    }
                    return loader.loadTimelineRangeCursors(getTimelineId()).flatMap(new Func1<List<TimelineRangeCursor>, Observable<List<TimelineRange<T>>>>() {
                        @Override
                        public Observable<List<TimelineRange<T>>> call(List<TimelineRangeCursor>
                                                                               timelineRangeCursors) {
                            if (rangeList.isEmpty()) {
                                for (TimelineRangeCursor cursor : timelineRangeCursors) {
                                    rangeList.add(new TimelineRange<>(cursor, loader));
                                }
                            }
                            cursorDBLoaded.set(true);
                            return innerRefresh();
                        }
                    });
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

                clearIfTooMuch();
                return Observable.just(getAllRanges());
            }
        }).subscribeOn(Schedulers.io());
    }

    private void clearIfTooMuch() {
        int count = getCount();

        List<T> entitiesClear = new LinkedList<>();
        List<TimelineRangeCursor> cursorsCleared = new LinkedList<>();
        while (needClear()) {
            int tryClearCount = count - Constants.CAPABILITY;
            TimelineRange<T> lastRange = getLastRange();

            List<T> cleared = lastRange.clear(tryClearCount);
            entitiesClear.addAll(cleared);

            if(0 == lastRange.getCount()) {
                rangeList.removeLast();
                rangeList.getLast().contact(lastRange);
                cursorsCleared.add(lastRange.cursor);
            }

            count -= cleared.size();
        }

        if (entitiesClear.size() > 0) {
            TransactionManager.getInstance().addTransaction(new DeleteModelListTransaction<T>
                    (ProcessModelInfo.withModels(entitiesClear)));

            if (cursorsCleared.size() > 0) {
                TransactionManager.getInstance().addTransaction(new DeleteModelListTransaction<TimelineRangeCursor>
                        (ProcessModelInfo.withModels(cursorsCleared)));
            }
            rangeList.getLast().cursor.save();
        }
    }

    private int getCount(){
        int count = 0;
        for(TimelineRange<T> range : rangeList) {
            count += range.getCount();
        }
        return count;
    }

    private boolean needClear() {
        return getCount() > Constants.CAPABILITY;
    }

    public @NotNull Observable<List<TimelineRange<T>>> loadMore() {
        if(!cursorDBLoaded.get()) {
            Timber.w("TimelineCursor should be loaded before loading more.");
            return Observable.empty();
        }

        if(getCount() >= Constants.CAPABILITY) {
            return Observable.just(getAllRanges());
        }

        TimelineRange<T> rangeToLoadMore = getRangeToLoadMore();

        return loadMore(rangeToLoadMore);
    }

    public @NotNull Observable<List<TimelineRange<T>>> loadMore(TimelineRange<T> range) {
        final Long originalBottom = range.cursor.getBottom();
        final boolean rangeDbLoaded = range.isDBLoaded();

        Long sinceId = null;
        boolean isLastRange = range == getLastRange();
        final TimelineRange<T> previousRange = isLastRange?null:getPreviousRange(range);
        if(null != previousRange) {
            sinceId = previousRange.cursor.getTop();
        }

       return range.loadMore(sinceId).flatMap(new Func1<TimelineRange<T>,
               Observable<List<TimelineRange<T>>>>() {
           @Override
           public Observable<List<TimelineRange<T>>> call(TimelineRange<T> range) {
               if (range.cursor.getBottom() > originalBottom) {
                   List<T> entities = range.getSubRange(originalBottom, range.cursor.getBottom()
                           + 1);
                   group(entities, true);

                   if (!range.hasMore()) {

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

    public int getUnreadGroupCount() {
        int count = 0;
        for(TimelineRange<T> range : getAllRanges()) {
            for(Group<T> group : range.getAllGroups()) {
                if(group.getUnread() && null != group.getRoot()) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean getHasUnread() {
        return entity.getUnread();
    }

    public void setHasUnread(boolean hasUnread) {
        if(hasUnread == getHasUnread()) {
            return;
        }
        entity.setUnread(hasUnread);
        entity.save();
    }
}
