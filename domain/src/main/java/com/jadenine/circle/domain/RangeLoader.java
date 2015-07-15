package com.jadenine.circle.domain;

import com.jadenine.circle.model.rest.JSONListWrapper;

import rx.Observable;

/**
 * Created by linym on 7/15/15.
 */
public interface RangeLoader<T extends Identifiable<Long>>{

    Observable<JSONListWrapper<T>> refresh(Long top, Integer count);

    Observable<JSONListWrapper<T>> loadMore(Long bottom, Integer Count);
}
