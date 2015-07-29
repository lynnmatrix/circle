package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.UserApEntity;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 6/18/15.
 */
public class ApDBService {
    public Observable<List<UserApEntity>> listAps() {

        return Observable.create(new Observable.OnSubscribe<List<UserApEntity>>() {
            @Override
            public void call(Subscriber<? super List<UserApEntity>> subscriber) {
                List<UserApEntity> userApEntities = new Select().from(UserApEntity.class)
                        .queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(userApEntities);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
