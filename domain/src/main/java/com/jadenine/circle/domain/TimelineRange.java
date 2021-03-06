package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.entity.IdentifiableEntity;
import com.jadenine.circle.model.rest.TimelineRangeResult;
import com.jadenine.circle.model.state.TimelineRangeCursor;
import com.raizlabs.android.dbflow.annotation.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.android.internal.Preconditions;
import rx.functions.Func1;

/**
 * Created by linym on 7/15/15.
 */
public class  TimelineRange<T extends IdentifiableEntity> {
    final TimelineRangeCursor cursor;
    private final List<T> list;
    private final SortedCollection<Long, Group<T>> groupList;
    private final RangeLoader<T> loader;

    private AtomicBoolean dbLoaded = new AtomicBoolean(false);

    public TimelineRange(String timeline, List<T> list, RangeLoader<T> loader) {
        this(new TimelineRangeCursor(timeline, list), list, loader, true);
    }

    public TimelineRange(TimelineRangeCursor cursor, RangeLoader<T> loader) {
        this(cursor, new LinkedList<T>(), loader, false);
    }

    private TimelineRange(TimelineRangeCursor cursor, List<T> list,
                          RangeLoader<T> loader, boolean dbLoaded) {
        Preconditions.checkNotNull(cursor, "Null cursor");
        Preconditions.checkNotNull(list, "Null list");
        Preconditions.checkNotNull(loader, "Null loader.");

        this.cursor = cursor;
        this.list = list;
        this.groupList = new SortedCollection<>(new Comparator<Group<T>>() {
            @Override
            public int compare(Group<T> lhs, Group<T> rhs) {
                Long l_r = (lhs.getGroupId() - rhs.getGroupId());
                return 0==l_r?0:l_r<0?-1:1;
            }
        });

        this.loader = loader;
        this.dbLoaded.set(dbLoaded);
    }

    /**
     * Refresh the current range.
     * @return The newest timeline range. Current range will be returned if the entities is
     * continual with current range, otherwise a new range.
     */
    public Observable<TimelineRange<T>> refresh() {
        if(!isDBLoaded()) {
            return loadLocal();
        } else {
            return loader.refresh(cursor.getTop()).flatMap(new Func1<TimelineRangeResult<T>, Observable<TimelineRange<T>>>() {

                @Override
                public Observable<TimelineRange<T>> call(TimelineRangeResult<T> timelineRangeResult) {
                    TimelineRange<T> range = TimelineRange.this;
                    if (timelineRangeResult.hasMore() && null != cursor.getTop()) {
                        TimelineRange nextRange = new TimelineRange(cursor.getTimeline(), timelineRangeResult.getAll(), loader);
                        range = nextRange;
                    } else if (timelineRangeResult.getAll().size() > 0) {
                        list.addAll(0, timelineRangeResult.getAll());
                        cursor.setTop(list.get(0).getId());
                        cursor.setBottom(list.get(list.size() - 1).getId());
                    }
                    return Observable.just(range);
                }
            });
        }
    }

    /**
     * Load more entities in current range.
     * The caller of this api should contact this range with the previous range(with greater id) if
     * they are continuous.
     * @return The current range.
     */
    public Observable<TimelineRange<T>> loadMore(Long sinceId) {
        if(!isDBLoaded()) {
            return loadLocal();
        } else {
            return loader.loadMore(cursor.getBottom(), sinceId).flatMap(new Func1<TimelineRangeResult<T>,
                    Observable<TimelineRange<T>>>() {

                @Override
                public Observable<TimelineRange<T>> call(TimelineRangeResult<T> timelineRangeResult) {
                    if (timelineRangeResult.getAll().size() > 0) {
                        list.addAll(timelineRangeResult.getAll());
                        cursor.setBottom(list.get(list.size() - 1).getId());
                    }
                    cursor.setHasMore(timelineRangeResult.hasMore());
                    return Observable.just(TimelineRange.this);
                }
            });
        }
    }

    @NonNull
    private Observable<TimelineRange<T>> loadLocal() {
        return loader.loadTimelineRange(cursor.getTop(), cursor.getBottom()).flatMap(new Func1<List<T>, Observable<TimelineRange<T>>>() {
            @Override
            public Observable<TimelineRange<T>> call(List<T> list) {
                if (list.size() > 0) {
                    TimelineRange.this.list.addAll(list);
                }
                dbLoaded.set(true);
                return Observable.just(TimelineRange.this);
            }
        });
    }

    public boolean hasMore() {
        return cursor.getHasMore();
    }

    public List<T> getAll(){
        return new ArrayList<>(list);
    }

    public int getCount() {
        return list.size();
    }

    public List<Group<T>> getAllGroups() {
        return groupList.getAll();
    }

    public int getGroupCount() {
        return groupList.size();
    }

    public Group<T> getGroup(Long groupId) {
        return groupList.get(groupId);
    }

    /**
     * contact two continual range.
     * @param range
     */
    void contact(TimelineRange<T> range) {
        if(hasMore() && range.getCount() > 0) {
            throw new IllegalStateException("Ranges which are not continuous cannot be contacted.");
        }

        list.addAll(range.getAll());
        for(Group<T> group : range.getAllGroups()) {
            groupList.put(group);
        }

        cursor.contact(range.cursor);
    }

    /**
     * Retrieve sub range of entities in [start, end)
     * @param start
     * @param end exclusive
     * @return
     */
    public List<T> getSubRange(@NotNull Long start, @NotNull Long end) {
        Preconditions.checkArgument(start < end && start >= cursor.getTop() && end <= cursor.getBottom() +1, "Invalid sub " +
                "range ["+ start + ","+end +"]. " +
                "Current range is ["+cursor.getTop() +"," + cursor.getBottom()+"]");
        int startIndex = -1;
        int endIndex = list.size();
        for (int i = 0; i< list.size(); i++) {
            T entity = list.get(i);
            if (start <= entity.getId() && startIndex < 0) {
                startIndex = i;
            } else if(entity.getId() <= end){
                endIndex = i;
            }
        }

        return list.subList(startIndex, endIndex);
    }

    public void group(T entity) {
        Group<T> group = groupList.get(entity.getGroupId());
        if(null == group) {
            group = new Group<>(entity.getGroupId());
            groupList.put(group);
        }
        group.addEntity(entity);
    }

    boolean isDBLoaded() {
        return dbLoaded.get();
    }

    /**
     * @param tryClearCount
     * @return entities cleared.
     */
    public List<T> clear(int tryClearCount) {
        int clearCount = Math.min(tryClearCount, getCount());

        ArrayList entitiesCleared = new ArrayList(clearCount);

        while (clearCount > 0) {
            T entity = list.remove(list.size() - 1);
            entitiesCleared.add(0, entity);
            unGroup(entity);
            clearCount--;
        }

        cursor.setHasMore(clearCount > 0);
        if(0 == getCount()) {
            cursor.setBottom(null);
            cursor.setTop(null);
        } else {
            cursor.setBottom(list.get(list.size() - 1).getId());
        }
        return entitiesCleared;
    }

    private void unGroup(T entity) {
        Group<T> group = groupList.get(entity.getGroupId());
        if(null != group) {
            group.remove(entity);
            if(0 == group.getCount()) {
                groupList.remove(group);
            }
        }
    }

}
