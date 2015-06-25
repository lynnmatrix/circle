package com.jadenine.circle.model.rest;

import java.util.List;

/**
 * Created by linym on 6/2/15.
 */
public class JSONListWrapper<T> {
    private boolean hasMore;
    private List<T> itemList;

    public List<T> getAll() {
        return itemList;
    }

    private boolean hasMore(){
        return hasMore;
    }
}