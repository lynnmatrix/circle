package com.jadenine.circle.model.db.impl;

import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.entity.Message;

import java.util.Collections;
import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/18/15.
 */
public class MessageDBServiceImpl implements MessageDBService {
    @Override
    public Observable<List<Message>> listMessages(String topic) {
        return Observable.just(Collections.<Message>emptyList());
    }
}
