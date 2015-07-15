package com.jadenine.circle.domain;

import com.jadenine.circle.model.rest.JSONListWrapper;
import com.jadenine.circle.model.state.TimelineRangeCursor;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.internal.Preconditions;
import rx.functions.Func1;

/**
 * Created by linym on 7/15/15.
 */
public class TimelineRange<T extends Identifiable<Long>> {
    final TimelineRangeCursor cursor;
    private final List<T> list;
    private final RangeLoader<T> loader;

    public TimelineRange(String timeline, List<T> list, RangeLoader<T> loader) {
        Preconditions.checkNotNull(timeline, "Invalid timeline.");
        Preconditions.checkNotNull(list, "Null list");
        Preconditions.checkNotNull(loader, "Null loader.");

        this.list = list;
        this.cursor = new TimelineRangeCursor(timeline);
        this.loader = loader;
        if (list.size() > 0) {
            this.cursor.setTop(list.get(0).getId());
            this.cursor.setBottom(list.get(list.size() - 1).getId());
        }
    }

    /**
     * Refresh the current range.
     * @return The newest timeline range. Current range will be returned if the entities is
     * continual with current range, otherwise a new range.
     */
    public Observable<TimelineRange<T>> refresh() {
        return loader.refresh(cursor.getTop(), Constants.PAGE_SIZE).flatMap(new Func1<JSONListWrapper<T>,
                Observable<TimelineRange<T>>>() {

            @Override
            public Observable<TimelineRange<T>> call(JSONListWrapper<T> jsonListWrapper) {
                TimelineRange<T> range = TimelineRange.this;
                if(jsonListWrapper.hasMore()) {
                    TimelineRange nextRange = new TimelineRange(cursor.getTimeline(),
                            jsonListWrapper.getAll(), loader);
                    range = nextRange;
                } else if(jsonListWrapper.getAll().size() > 0)  {
                    list.addAll(0, jsonListWrapper.getAll());
                    cursor.setTop(list.get(0).getId());
                }
                return Observable.just(range);
            }
        });
    }

    /**
     * Load more entities in current range.
     * The caller of this api should contact this range with the previous range if they are
     * continuous.
     * @return The current range.
     */
    public Observable<TimelineRange<T>> loadMore() {
        return loader.loadMore(cursor.getBottom(), Constants.PAGE_SIZE).flatMap(new Func1<JSONListWrapper<T>,
                Observable<TimelineRange<T>>>() {

            @Override
            public Observable<TimelineRange<T>> call(JSONListWrapper<T> jsonListWrapper) {
                if (jsonListWrapper.getAll().size() > 0) {
                    list.addAll(jsonListWrapper.getAll());
                    cursor.setBottom(jsonListWrapper.getAll().get(jsonListWrapper.getAll().size()
                            - 1).getId());
                }
                cursor.setHasMore(jsonListWrapper.hasMore());
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
 }
