package com.jadenine.circle.model.db;

import com.jadenine.circle.model.state.TimelineEntity;
import com.jadenine.circle.model.state.TimelineEntity$Table;
import com.jadenine.circle.model.state.TimelineType;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 8/3/15.
 */
public class TimelineDBService {
    public Observable<TimelineEntity> load(final String timeline, final TimelineType type) {
        return Observable.create(new Observable.OnSubscribe<TimelineEntity>() {
            @Override
            public void call(Subscriber<? super TimelineEntity> subscriber) {
                Where<TimelineEntity> where = new Select().from(TimelineEntity.class)
                        .where(Condition.column(TimelineEntity$Table.ID).eq(timeline)).and
                                (Condition.column(TimelineEntity$Table.TYPE).eq(type));

                TimelineEntity timelineEntity = where.querySingle();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(timelineEntity);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
