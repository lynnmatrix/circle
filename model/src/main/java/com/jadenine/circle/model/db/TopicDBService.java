package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.TopicEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/18/15.
 */
public interface TopicDBService {
    Observable<List<TopicEntity>> listTopics(String ap);
}
