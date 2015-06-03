package com.jadenine.circle.request;

import java.util.List;

/**
 * Created by linym on 6/2/15.
 */
public class JSONListWrapper<T> {
    public List<T> itemlist;
    public JSONListWrapper(List<T> itemlist) {
        this.itemlist = itemlist;
    }

    public List<T> getAll() {
        return itemlist;
    }
}