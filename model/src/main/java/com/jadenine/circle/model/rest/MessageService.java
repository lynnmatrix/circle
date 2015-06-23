package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.MessageEntity;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public interface MessageService {
    @GET("/message/list/{topic}")
    Observable<JSONListWrapper<MessageEntity>> listMessages(@Path("topic") String topic);

    @POST("/message/add")
    Observable<MessageEntity> addMessage(@Query("ap")String ap, @Body MessageEntity messageEntity);
}
