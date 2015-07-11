package com.jadenine.circle.domain;

/**
 * Created by linym on 7/10/15.
 */
public interface Binder<E, D>  {
    D find(E e);

    D build(E e);
}
