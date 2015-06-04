package com.jadenine.circle.request;

import com.jadenine.circle.entity.Message;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by linym on 6/3/15.
 */
public interface MessageService {
    @GET("/message/list/{ap}")
    void listMessages(@Path("ap") String ap,
                                          Callback<JSONListWrapper<Message>> callback);

    @POST("/message/add")
    void addMessage(@Body Message message, Callback<Message> callback);
}
