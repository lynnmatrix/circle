package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.rest.JSONListWrapper;
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
public class  TimelineRange<T extends Identifiable<Long>> {
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
                return (int) (lhs.getGroupId() - rhs.getGroupId());
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
            return loader.refresh(cursor.getTop()).flatMap(new Func1<JSONListWrapper<T>, Observable<TimelineRange<T>>>() {

                @Override
                public Observable<TimelineRange<T>> call(JSONListWrapper<T> jsonListWrapper) {
                    TimelineRange<T> range = TimelineRange.this;
                    if (jsonListWrapper.hasMore() && null != cursor.getTop()) {
                        TimelineRange nextRange = new TimelineRange(cursor.getTimeline(), jsonListWrapper.getAll(), loader);
                        range = nextRange;
                    } else if (jsonListWrapper.getAll().size() > 0) {
                        list.addAll(0, jsonListWrapper.getAll());
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
    //TODO Limit the result by the top of previous range.
    public Observable<TimelineRange<T>> loadMore() {
        if(!isDBLoaded()) {
            return loadLocal();
        } else {
            return loader.loadMore(cursor.getBottom()).flatMap(new Func1<JSONListWrapper<T>,
                    Observable<TimelineRange<T>>>() {

                @Override
                public Observable<TimelineRange<T>> call(JSONListWrapper<T> jsonListWrapper) {
                    if (jsonListWrapper.getAll().size() > 0) {
                        list.addAll(jsonListWrapper.getAll());
                        cursor.setBottom(list.get(list.size() - 1).getId());
                    }
                    cursor.setHasMore(jsonListWrapper.hasMore());
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

    public List<Group<T>> getAllGroups() {
        return groupList.getAll();
    }

    /**
     * contact two continual range.
     * @param range
     */
    public void contact(TimelineRange<T> range) {
        if(hasMore()) {
            throw new IllegalStateException("Ranges which are not continuous cannot be contacted.");
        }

        list.addAll(range.getAll());
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
}
