package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.UserApEntity;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public interface ApService {
    @POST("/ap/list")
    Observable<TimelineRangeResult<UserApEntity>> listAPs(@Query("user") String user);

    @POST("/ap/add")
    Observable<TimelineRangeResult<UserApEntity>> addAP(@Body UserApEntity userApEntity);
}
