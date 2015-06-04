package com.jadenine.circle.request;

import com.jadenine.circle.entity.UserAp;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by linym on 6/3/15.
 */
public interface ApService {
    @GET("/ap/list/{user}")
    void listAPs(@Path("user") String user, Callback<JSONListWrapper<UserAp>> callback);

    @POST("/ap/add")
    void addAP(@Body UserAp userAp, Callback<JSONListWrapper<UserAp>>
            callback);
}
