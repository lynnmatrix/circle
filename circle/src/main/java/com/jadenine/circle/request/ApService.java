package com.jadenine.circle.request;

import com.jadenine.circle.entity.UserAp;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by linym on 6/3/15.
 */
public interface ApService {
    @GET("/ap/list/{user}")
    void listAPs(@Path("user") String user, Callback<JSONListWrapper<UserAp>> callback);

    @GET("/ap/add/{user}/{ap}")
    void addAP(@Path("user") String user, @Path("ap") String ap, Callback<JSONListWrapper<UserAp>>
            callback);
}
