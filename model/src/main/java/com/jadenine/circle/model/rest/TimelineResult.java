package com.jadenine.circle.model.rest;

import java.util.List;

/**
 * Created by linym on 7/11/15.
 */
public class TimelineResult<T> {
    public List<T> getAdd() {
        return add;
    }

    public List<T> getUpdate() {
        return update;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public String getNextId() {
        return nextId;
    }

    private List<T> add;
    private List<T> update;
    private boolean hasMore;
    private String nextId;
}
