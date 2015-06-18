package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.entity.Topic;

import java.util.Collections;
import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/18/15.
 */
public class TopicDBServiceImpl implements TopicDBService {
    @Override
    public Observable<List<Topic>> listTopics(String ap) {
        return Observable.just(Collections.<Topic>emptyList());
    }
}
