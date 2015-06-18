package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.Message;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/18/15.
 */
public interface MessageDBService {
    Observable<List<Message>> listMessages(String topic);
}
