package com.jadenine.circle.request;

import com.jadenine.circle.entity.Message;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by linym on 6/3/15.
 */
public interface MessageService {
    @GET("/message/list/{topic}")
    void listMessages(@Path("topic") String topic, Callback<JSONListWrapper<Message>> callback);

    //TODO 改为rx
    @POST("/message/add")
    void addMessage(@Query("ap")String ap, @Body Message message, Callback<Message> callback);
}
