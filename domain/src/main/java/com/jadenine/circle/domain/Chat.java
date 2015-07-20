package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.entity.MessageEntity;
import com.jadenine.circle.model.rest.MessageService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by linym on 7/14/15.
 */
public class Chat implements Identifiable<Long> {
    private String ap;
    private String topicId;
    private String topicUser;
    private String chatUser;
    private String chatIdStr;//the original message id
    private Long chatId;//
    private final List<Message> messages = new ArrayList<>();
    @Inject
    MessageService messageRestService;

    @Inject
    MessageDBService messageDBService;

    public Chat(String ap, String topicId, String topicPublisher, String chatFounder) {
        this.ap = ap;
        this.topicId = topicId;
        this.topicUser = topicPublisher;
        this.chatUser = chatFounder;

        DaggerService.getDomainComponent().inject(this);
    }

    @NonNull
    @Override
    public Long getId() {
        return chatId;
    }

    @NonNull
    @Override
    public Long getGroupId() {
        return chatId;
    }

    public List<Message> getMessages(){
        return messages;
    }

    public Observable<Message> send(Message message) {
        message.setTopicId(getTopicId());
        Observable<Message> observable = messageRestService.addMessage(message.getEntity()).map
                (new Func1<MessageEntity, Message>() {

                    @Override
                    public Message call(MessageEntity messageEntity) {
                        Message msg = Message.build(messageEntity);
                        messages.add(msg);
                        msg.getEntity().save();

                        return msg;
                    }
                });

        return observable;
    }

    public String getTopicId() {
        return topicId;
    }

}
