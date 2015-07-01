package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.Image;

import retrofit.http.POST;
import rx.Observable;

/**
 * Created by linym on 7/1/15.
 */
public interface ImageService {
    @POST("/image/requestWritableSas")
    Observable<Image> getWritableSas();
}
