package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.UserAp;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public interface ApService {
    @GET("/ap/list/{user}")
    Observable<JSONListWrapper<UserAp>> listAPs(@Path("user") String user);

    @POST("/ap/add")
    Observable<JSONListWrapper<UserAp>> addAP(@Body UserAp userAp);
}
