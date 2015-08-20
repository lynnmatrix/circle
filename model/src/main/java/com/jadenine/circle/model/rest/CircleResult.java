package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.ApEntity;
import com.jadenine.circle.model.entity.CircleEntity;

import java.util.List;

/**
 * Created by linym on 8/19/15.
 */
public class CircleResult {
    private List<CircleEntity> circles;
    private List<ApEntity> aps;

    public CircleResult(){}
    public CircleResult(List<CircleEntity> circles, List<ApEntity> aps) {
        this.circles = circles;
        this.aps = aps;
    }

    public List<CircleEntity> getCircles(){
        return circles;
    }

    public List<ApEntity> getAps() {
        return aps;
    }
}
