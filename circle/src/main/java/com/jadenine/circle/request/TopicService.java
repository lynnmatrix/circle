package com.jadenine.circle.request;

import com.jadenine.circle.entity.Topic;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by linym on 6/10/15.
 */
public interface TopicService {
    @GET("/topic/list/{ap}")
    Observable<JSONListWrapper<Topic>> listTopics(@Path("ap") String ap);
}
