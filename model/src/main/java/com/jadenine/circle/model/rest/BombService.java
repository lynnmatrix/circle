package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.Bomb;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 7/22/15.
 */
public interface BombService {
    /**
     * Returns a collection of the most recent timeline messages under the specified ap.
     *
     * @param circle
     * @param count (optional) Specifies the number of messages to try and retrieve, up to a maximum
     *              of 200. The value of count is best thought of as a limit to the number of messages
     *              to return because suspended or deleted content is removed after the count has
     *              been applied.
     * @param sinceId (optional) Returns results with an ID less than (that is, more recent than)
     *                the specified ID.
     * @param beforeId (optional) Returns results with an ID greater than (that is, older than) to the specified ID.
     */
    @POST("/bomb/list")
    Observable<TimelineRangeResult<Bomb>> apTimeline(@Query("circle") String circle,
                                                     @Query("count") Integer count,
                                                     @Query("since_id") Long sinceId,
                                                     @Query("before_id") Long beforeId);

    /**
     * Send a  message
     * @param message
     * @return the sent message if succeed.
     */
    @POST("/bomb/add")
    Observable<Bomb> add(@Body Bomb message);


    /**
     * Returns a collection of the most recent timeline messages belongs to topics started by myself.
     *
     * @param auth user
     * @param count (optional) See{@link BombService#apTimeline(String, Integer, Long, Long)}
     * @param sinceId (optional) See{@link BombService#apTimeline(String, Integer, Long, Long)}
     * @param beforeId (optional) See{@link BombService#apTimeline(String, Integer, Long, Long)}
     */
    @POST("/bomb/list/user")
    Observable<TimelineRangeResult<Bomb>> myTopicsTimeline(@Query("auth") String auth,
                                                           @Query("count") Integer count,
                                                           @Query("since_id") Long sinceId,
                                                           @Query("before_id") Long beforeId);

    /**
     * Return the top 10 topic
     *
     * @param auth
     */
    @POST("/bomb/list/top")
    Observable<TimelineRangeResult<Bomb>> top(@Query("auth") String auth);

}
