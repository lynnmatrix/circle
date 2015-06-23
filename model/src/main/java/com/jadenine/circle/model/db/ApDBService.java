package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.UserApEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/17/15.
 */
public interface ApDBService {
    Observable<List<UserApEntity>> listAps();
}
