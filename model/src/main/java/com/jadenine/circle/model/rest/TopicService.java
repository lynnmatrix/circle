package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.TopicEntity;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 6/10/15.
 */
public interface TopicService {
    /**
     * Return a collection of the most recent topics under the specified AP.
     * <p>
     *  NOTE: The topic id is auto <b>decrement</b>.
     * </p>
     *
     *
     * @param ap
     * @param count (optional, default 20)  Specifies the number of topics to try and retrieve,
     *              up to a maximum of 200.
     * @param sinceId (optional) Returns results with a topic id <b>less than</b> the
     *                       specified ID.
     * @param beforeId (optional) Returns results with a topic id <b>greater than</b> the
     *                        specified ID.
     */
    @GET("/topic/list")
    Observable<JSONListWrapper<TopicEntity>> listTopics(@Query("ap") String ap,
                                                        @Query("count") Integer count,
                                                        @Query("since_id") String sinceId,
                                                        @Query("before_id") String beforeId);

    @POST("/topic/add")
    Observable<TopicEntity> addTopic(@Body TopicEntity entity);
}
