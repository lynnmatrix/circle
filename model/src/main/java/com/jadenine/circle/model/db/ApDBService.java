package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.ApEntity;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 6/18/15.
 */
public class ApDBService {

    public Observable<List<ApEntity>> listAps() {

        return Observable.create(new Observable.OnSubscribe<List<ApEntity>>() {
            @Override
            public void call(Subscriber<? super List<ApEntity>> subscriber) {
                List<ApEntity> entities = new Select().from(ApEntity.class)
                        .queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(entities);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
