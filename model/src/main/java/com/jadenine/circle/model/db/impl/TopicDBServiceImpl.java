package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.entity.TopicEntity$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 6/18/15.
 */
public class TopicDBServiceImpl implements TopicDBService {
    @Override
    public Observable<List<TopicEntity>> listTopics(final String ap) {
        return Observable.create(new Observable.OnSubscribe<List<TopicEntity>>() {
            @Override
            public void call(Subscriber<? super List<TopicEntity>> subscriber) {
                List<TopicEntity> topicEntities = new Select().from(TopicEntity.class)
                        .where(Condition.column(TopicEntity$Table.AP).eq(ap))
                        .orderBy(true, TopicEntity$Table.TOPICID).queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(topicEntities);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
