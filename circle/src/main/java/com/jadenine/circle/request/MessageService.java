package com.jadenine.circle.request;

import com.jadenine.circle.entity.Message;

import retrofit.Callback;
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
    Observable<JSONListWrapper<Message>> listMessages(@Path("topic") String topic);

    @POST("/message/add")
    void addMessage(@Query("ap")String ap, @Body Message message, Callback<Message>
            callback);
}
