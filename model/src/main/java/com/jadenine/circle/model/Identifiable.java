package com.jadenine.circle.model;

import android.support.annotation.NonNull;

/**
 * Created by linym on 4/11/15.
 */
public interface Identifiable<K> {
    @NonNull K getId();
    @NonNull K getGroupId();
}
