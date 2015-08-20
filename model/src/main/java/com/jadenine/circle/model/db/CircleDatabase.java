package com.jadenine.circle.model.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by linym on 6/23/15.
 */
@Database(name = CircleDatabase.NAME, version = CircleDatabase.VERSION)
public class CircleDatabase {
    public static final String NAME = "circle";

    public static final int VERSION = 3;
}
