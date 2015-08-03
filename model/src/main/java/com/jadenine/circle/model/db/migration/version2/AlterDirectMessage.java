package com.jadenine.circle.model.db.migration.version2;

import com.jadenine.circle.model.db.CircleDatabase;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.entity.DirectMessageEntity$Table;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by linym on 8/3/15.
 */
@Migration(version = 2, databaseName = CircleDatabase.NAME)
public class AlterDirectMessage extends AlterTableMigration<DirectMessageEntity>{
    public AlterDirectMessage() {
        super(DirectMessageEntity.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(Boolean.class, DirectMessageEntity$Table.UNREAD);
    }
}
