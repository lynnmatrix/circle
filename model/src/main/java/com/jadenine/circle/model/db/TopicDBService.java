package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.Topic;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/18/15.
 */
public interface TopicDBService {
    Observable<List<Topic>> listTopics(String ap);
}
