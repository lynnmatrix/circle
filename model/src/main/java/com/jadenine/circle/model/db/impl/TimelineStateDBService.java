package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.state.TimelineState;
import com.jadenine.circle.model.state.TimelineState$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 7/11/15.
 */
public class TimelineStateDBService {
    public Observable<TimelineState> getTimelineState(final String ap) {
        return Observable.create(new Observable.OnSubscribe<TimelineState>() {
            @Override
            public void call(Subscriber<? super TimelineState> subscriber) {
                TimelineState timelineState = new Select().from(TimelineState.class)
                        .where(Condition.column(TimelineState$Table.AP).eq(ap))
                        .querySingle();
                if(null == timelineState) {
                    timelineState = new TimelineState(ap);
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(timelineState);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
