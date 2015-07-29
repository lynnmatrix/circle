package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.entity.DirectMessageEntity$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 7/21/15.
 */
public class DirectMessageDBService {
    public Observable<List<DirectMessageEntity>> listMessage( final Long
            beforeId, final Long sinceId) {
        return Observable.create(new Observable.OnSubscribe<List<DirectMessageEntity>>() {
            @Override
            public void call(Subscriber<? super List<DirectMessageEntity>> subscriber) {
                Where<DirectMessageEntity> where = new Select().from(DirectMessageEntity.class)
                        .where();
                if (null != beforeId) {
                    where = where.and(Condition.column(DirectMessageEntity$Table.MESSAGEID)
                            .lessThan(beforeId));
                }

                if (null != sinceId) {
                    where = where.and(Condition.column(DirectMessageEntity$Table.MESSAGEID)
                            .greaterThan(sinceId));
                }

                List<DirectMessageEntity> directMe = where.orderBy(true,
                        DirectMessageEntity$Table.MESSAGEID).queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(directMe);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
