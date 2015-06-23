package com.jadenine.circle.model.db;

import com.jadenine.circle.model.entity.MessageEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by linym on 6/18/15.
 */
public interface MessageDBService {
    Observable<List<MessageEntity>> listMessages(String topic);
}
