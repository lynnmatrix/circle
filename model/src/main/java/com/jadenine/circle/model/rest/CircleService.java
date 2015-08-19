package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.ApEntity;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 8/19/15.
 */
public interface CircleService {
    @POST("/circle/list")
    Observable<CircleResult> listCircle(@Query("user") String user);

    @POST("/circle/ap/add")
    Observable<CircleResult> addAP(@Query("user") String user, @Body ApEntity ap);
}
