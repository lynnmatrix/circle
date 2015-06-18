package com.jadenine.circle.domain;

/**
 * Created by linym on 6/18/15.
 */
interface Finder<E, D> {
    D find(E e);

    D bind(E e);
}
