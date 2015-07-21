package com.jadenine.circle.domain;

import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.rest.JSONListWrapper;

import rx.Observable;

/**
 * Created by linym on 7/15/15.
 */
public interface RangeLoader<T extends Identifiable<Long>>{

    Observable<JSONListWrapper<T>> refresh(Long top);

    Observable<JSONListWrapper<T>> loadMore(Long bottom);
}
