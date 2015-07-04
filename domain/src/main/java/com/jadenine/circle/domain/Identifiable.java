package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

/**
 * Created by linym on 4/11/15.
 */
public interface Identifiable<K> {
    @NonNull K getId();
}
