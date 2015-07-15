package com.jadenine.circle.domain;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 7/15/15.
 */
public interface Loadable<T> {
    Observable<List<T>> refresh();
    Observable<List<T>> loadMore();
    boolean hasMore();
}
