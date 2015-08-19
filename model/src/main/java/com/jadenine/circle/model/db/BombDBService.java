package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.Bomb$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.security.InvalidParameterException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 7/22/15.
 */
public class BombDBService {
    public Observable<List<Bomb>> listMessage(final String circle, final Long sinceId, final Long beforeId) {

        return Observable.create(new Observable.OnSubscribe<List<Bomb>>() {
            @Override
            public void call(Subscriber<? super List<Bomb>> subscriber) {

                if(null == circle) {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onError(new InvalidParameterException("Invalid ap."));
                    }
                    return;
                }

                Where<Bomb> where;
                where = new Select().from(Bomb.class).where();
                where.and(Condition.column(Bomb$Table.CIRCLE).eq(circle));
                where = rangeFilterAndOrder(where, beforeId, sinceId);

                List<Bomb> bombs = where.queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(bombs);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<List<Bomb>> myTopics(final String auth, final Long sinceId, final Long beforeId) {
        return Observable.create(new Observable.OnSubscribe<List<Bomb>>() {
            @Override
            public void call(Subscriber<? super List<Bomb>> subscriber) {
                if(null == auth) {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onError(new InvalidParameterException("Invalid auth."));
                    }
                    return;
                }

                Where<Bomb> where;
                where = new Select().from(Bomb.class).where();
                where.and(Condition.column(Bomb$Table.ROOTUSER).eq(auth));

                where = rangeFilterAndOrder(where, beforeId, sinceId);

                List<Bomb> bombs = where.queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(bombs);
                    subscriber.onCompleted();
                }
            }
        });
    }

    private Where<Bomb> rangeFilterAndOrder(Where<Bomb> where, Long beforeId, Long sinceId) {
        if (null != beforeId) {
            where = where.and(Condition.column(Bomb$Table.MESSAGEID).greaterThan(beforeId));
        }

        if (null != sinceId) {
            where = where.and(Condition.column(Bomb$Table.MESSAGEID).lessThan(sinceId));
        }
        return where.orderBy(true, Bomb$Table.MESSAGEID);
    }
}
