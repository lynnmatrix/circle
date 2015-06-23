package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.entity.UserApEntity;

import java.util.Collections;
import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/18/15.
 */
public class ApDBServiceImpl implements ApDBService{
    @Override
    public Observable<List<UserApEntity>> listAps() {
        return Observable.just(Collections.<UserApEntity>emptyList());
    }
}
