package com.jadenine.circle.model.rest;

import java.util.List;

/**
 * Created by linym on 6/2/15.
 */
public class JSONListWrapper<T> {
    private List<T> itemList;
    private boolean hasMore;
    private String nextId;

    public JSONListWrapper(){}

    public JSONListWrapper(List<T> itemList, boolean hasMore, String nextId) {
        this.itemList = itemList;
        this.hasMore = hasMore;
        this.nextId = nextId;
    }

    public List<T> getAll() {
        return itemList;
    }

    public boolean hasMore(){
        return hasMore;
    }

    public String getNextId(){
        return nextId;
    }
}