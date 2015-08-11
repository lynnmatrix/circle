package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.TimelineRangeResult;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 8/11/15.
 */
public class TopLoader {

    @Inject
    BombService bombService;

    private final String user;
    public TopLoader(String deviceId) {
        this.user = deviceId;
    }

    public Observable<TimelineRangeResult<Bomb>> refresh() {
        return bombService.top(user);
    }
}
