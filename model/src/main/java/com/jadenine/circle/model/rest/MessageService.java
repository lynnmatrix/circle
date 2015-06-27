package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.MessageEntity;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public interface MessageService {
    @POST("/message/list")
    Observable<JSONListWrapper<MessageEntity>> listMessages(@Query("auth") String auth,
                                                            @Query("ap") String ap,
                                                            @Query("topic") String topic);

    @POST("/message/add")
    Observable<MessageEntity> addMessage(@Body MessageEntity messageEntity);
}
