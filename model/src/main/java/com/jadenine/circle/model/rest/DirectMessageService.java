package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.DirectMessageEntity;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 7/20/15.
 */
public interface DirectMessageService {
    /**
     * Returns a collection of the most recent chat messages sended from or to the authenticating
     * user.
     *
     * @param auth user
     * @param count (optional) Specifies the number of chats to try and retrieve, up to a maximum
     *              of 200. The value of count is best thought of as a limit to the number of chat
     *              to return because suspended or deleted content is removed after the count has
     *              been applied
     * @param sinceId (optional) Returns results with an ID less than (that is, more recent than)
     *                the specified ID.
     * @param beforeId (optional) Returns results with an ID greater than (that is, older than) to the specified ID.
     */
    @POST("/chat/list")
    Observable<JSONListWrapper<DirectMessageEntity>> listMessages(@Query("auth") String auth,
                                                       @Query("count") Integer count,
                                                       @Query("since_id") Long sinceId,
                                                       @Query("before_id") Long beforeId);

    /**
     * Send a chat message
     * @param message
     * @return the sent message if succeed.
     */
    @POST("/chat/add")
    Observable<DirectMessageEntity> addMessage(@Body DirectMessageEntity message);

}
