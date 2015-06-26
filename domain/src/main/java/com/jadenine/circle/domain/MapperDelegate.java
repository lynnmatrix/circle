package com.jadenine.circle.domain;

import java.util.List;

/**
 * Created by linym on 6/18/15.
 */
interface MapperDelegate<E, D> {
    D find(E e);

    D build(E e);

    void setHasMore(boolean hasMore);

    List<D> getOriginSource();

    int getCapability();
}
