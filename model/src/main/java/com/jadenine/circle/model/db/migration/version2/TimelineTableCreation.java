package com.jadenine.circle.model.db.migration.version2;

import android.database.sqlite.SQLiteDatabase;

import com.jadenine.circle.model.db.CircleDatabase;
import com.jadenine.circle.model.state.TimelineEntity$Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

/**
 * Created by linym on 8/3/15.
 */
@Migration(databaseName = CircleDatabase.NAME, version = 2)
public class TimelineTableCreation extends BaseMigration {
    @Override
    public void migrate(SQLiteDatabase sqLiteDatabase) {
        TimelineEntity$Adapter adapter = new TimelineEntity$Adapter();
        sqLiteDatabase.execSQL(adapter.getCreationQuery());
    }
}
