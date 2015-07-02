package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.entity.MessageEntity;
import com.jadenine.circle.model.entity.MessageEntity$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 6/18/15.
 */
public class MessageDBServiceImpl implements MessageDBService {
    @Override
    public Observable<List<MessageEntity>> listMessages(final String topic) {
        return Observable.create(new Observable.OnSubscribe<List<MessageEntity>>() {
            @Override
            public void call(Subscriber<? super List<MessageEntity>> subscriber) {
                List<MessageEntity> userApEntities = new Select().from(MessageEntity.class)
                        .where(Condition.column(MessageEntity$Table.TOPICID).eq(topic))
                        .orderBy(true, MessageEntity$Table.MESSAGEID)
                        .queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(userApEntities);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
