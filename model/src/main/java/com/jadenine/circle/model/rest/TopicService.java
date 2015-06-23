package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.TopicEntity;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by linym on 6/10/15.
 */
public interface TopicService {
    @GET("/topic/list/{ap}")
    Observable<JSONListWrapper<TopicEntity>> listTopics(@Path("ap") String ap);

    @POST("/topic/add")
    Observable<TopicEntity> addTopic(@Body TopicEntity entity);
}
