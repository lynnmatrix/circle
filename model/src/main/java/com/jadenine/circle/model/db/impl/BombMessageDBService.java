package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.Bomb$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 7/22/15.
 */
public class BombMessageDBService {
    public Observable<List<Bomb>> listMessage( final Long
                                                                      beforeId, final Long sinceId) {
        return Observable.create(new Observable.OnSubscribe<List<Bomb>>() {
            @Override
            public void call(Subscriber<? super List<Bomb>> subscriber) {
                Where<Bomb> where;
                where = new Select().from(Bomb.class)
                        .where();
                if (null != beforeId) {
                    where = where.and(Condition.column(Bomb$Table.MESSAGEID)
                            .lessThan(beforeId));
                }

                if (null != sinceId) {
                    where = where.and(Condition.column(Bomb$Table.MESSAGEID)
                            .greaterThan(sinceId));
                }

                List<Bomb> bombs = where.orderBy(true,
                        Bomb$Table.MESSAGEID).queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(bombs);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
