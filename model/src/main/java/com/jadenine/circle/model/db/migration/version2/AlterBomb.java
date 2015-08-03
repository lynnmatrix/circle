package com.jadenine.circle.model.db.migration.version2;

import com.jadenine.circle.model.db.CircleDatabase;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.Bomb$Table;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by linym on 8/3/15.
 */
@Migration(version = 2, databaseName = CircleDatabase.NAME)
public class AlterBomb extends AlterTableMigration<Bomb> {
    public AlterBomb(){
        super(Bomb.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(Boolean.class, Bomb$Table.UNREAD);
    }
}
