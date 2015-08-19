package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.CircleEntity;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 8/19/15.
 */
public class CircleDBService {
    public Observable<List<CircleEntity>> listCircles() {

        return Observable.create(new Observable.OnSubscribe<List<CircleEntity>>() {
            @Override
            public void call(Subscriber<? super List<CircleEntity>> subscriber) {
                List<CircleEntity> userApEntities = new Select().from(CircleEntity.class)
                        .queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(userApEntities);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
