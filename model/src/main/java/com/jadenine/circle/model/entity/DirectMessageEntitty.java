package com.jadenine.circle.model.entity;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;

/**
 * Created by linym on 7/15/15.
 */
public class DirectMessageEntitty {
    @PrimaryKey
    String messageId;
    String ap;
    String topicId;

    String user;
    String content;
    String replyToUser;

    String topicUser;

}
