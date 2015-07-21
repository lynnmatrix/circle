package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.state.TimelineRangeCursor;
import com.jadenine.circle.model.state.TimelineRangeCursor$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by linym on 7/21/15.
 */
public class TimelineCursorDBService {
    public Observable<List<TimelineRangeCursor>> loadTimelineRangeCursors(final String timeline) {
        return Observable.create(new Observable.OnSubscribe<List<TimelineRangeCursor>>() {
            @Override
            public void call(Subscriber<? super List<TimelineRangeCursor>> subscriber) {
                Where<TimelineRangeCursor> where = new Select().from(TimelineRangeCursor.class)
                        .where(Condition.column(TimelineRangeCursor$Table.TIMELINE).eq(timeline));

                List<TimelineRangeCursor> topicEntities = where.orderBy(true,
                        TimelineRangeCursor$Table.TOP).queryList();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(topicEntities);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
