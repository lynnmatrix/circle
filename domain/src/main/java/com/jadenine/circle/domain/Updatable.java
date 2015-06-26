package com.jadenine.circle.domain;

/**
 * Created by linym on 6/18/15.
 */
interface Updatable<E> {
    void merge(E e);

    void remove();

    E getEntity();
}
