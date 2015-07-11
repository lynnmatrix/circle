package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.TopicEntity;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 6/10/15.
 */
public interface TopicService {
    /**
     *
     * List topics of the most recent topics satisfied with the following specification.
     * Including entities of these topics.
     *
     * This API is usually used to refresh the list of the most recent topics.
     *
     * @param ap
     * @param count (optional, default 20)  Specifies the number of topics to try and retrieve,
     *              up to a maximum of 200.
     * @param sinceTopicId (optional) The id of topic in result must be <b>less than</b> or
     *                     <b>equal to</b> the specified sinceTopicId.
     * @param sinceTimestamp (optional) the topic or entity in result must has a modified-timestamp
     *                       <b>greater than</b> or <b>equals to</b> the specified timestamp.
     *
     */
    @POST("/topic/list")
    Observable<TimelineResult<TopicEntity>> refresh(@Query("ap") String ap,
                                                     @Query("count") Integer count,
                                                     @Query("since_topic_id") String sinceTopicId,
                                                     @Query("since_timestamp") Long sinceTimestamp);

    /**
     * Return a collection of the most recent topics under the specified AP.
     * Including entities of these topics.
     * <p>
     *  NOTE: The topic id is auto <b>decrement</b>.
     * </p>
     *
     * This API is usually used to load more topics in the the timeline of the most recent topics.
     *
     * @param ap
     * @param count See {@link TopicService#refresh(String, Integer, String, Long)}
     * @param beforeTopicId (optional) Returns results with a topic id <b>greater than</b> the
     *                    beforeId
     * @param beforeTimestamp (optional) the topic or entity in result must has a
     *                        created-timestamp <b>less than</b> the specified timestamp.
     */
    @POST("/topic/list")
    Observable<TimelineResult<TopicEntity>> loadMore(@Query("ap") String ap,
                                                      @Query("count") Integer count,
                                                      @Query("before_topic_id") String beforeTopicId,
                                                      @Query("before_timestamp") Long beforeTimestamp);

    @POST("/topic/add")
    Observable<TopicEntity> addTopic(@Body TopicEntity entity);
}
