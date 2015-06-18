package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.rest.MessageService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by linym on 6/10/15.
 */
public class Topic implements Updatable<com.jadenine.circle.model.entity.Topic>{
    private final com.jadenine.circle.model.entity.Topic entity;
    private final List<Message> messages = new ArrayList<>();

    @Inject
    MessageService messageRestService;
    @Inject
    MessageDBService messageDBService;
    private boolean loaded = false;

    private final MessageFinder finder = new MessageFinder();
    private final DomainLister<Message> messageLister = new DomainLister<>(new
            MessageListerDelegate());

    public static Topic bind(com.jadenine.circle.model.entity.Topic entity) {
        return new Topic(entity);
    }

    public Topic(UserAp userAp, String content) {
        this(new com.jadenine.circle.model.entity.Topic(userAp.getAP(), userAp.getUser(), content));
    }

    public Topic(com.jadenine.circle.model.entity.Topic entity) {
        this.entity = entity;
        DaggerService.getDomainComponent().inject(this);
    }

    com.jadenine.circle.model.entity.Topic getEntity() {
        return entity;
    }

    public String getTopicId() {
        return entity.getTopicId();
    }

    public String getAp() {
        return entity.getAp();
    }

    public String getUser() {
        return entity.getUser();
    }

    public String getTopic() {
        return entity.getTopic();
    }

    public String getLatestMessageId() {
        return entity.getLatestMessageId();
    }

    public long getTimestamp() {
        return entity.getTimestamp();
    }

    @Override
    public void merge(com.jadenine.circle.model.entity.Topic entity) {
        if(entity.getTimestamp() - this.entity.getTimestamp() > 0) {
            this.entity.setLatestMessageId(entity.getLatestMessageId());
            this.entity.setTimestamp(entity.getTimestamp());
            this.entity.save();
        }
    }

    public Observable<List<Message>> listMessage(){
        return messageLister.list();
    }

    Observable<Message> addReply(final Message message) {
        if(null == finder.find(message.getEntity())) {
            messages.add(message);
        }
        message.setTopicId(getTopicId());
        Observable<Message> observable = messageRestService.addMessage(getAp(), message.getEntity
                ()).map(new RestMapper<>(finder, messages));

        observable.subscribe(null, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                messages.remove(message);
            }
        });
        return observable;
    }

    public Observable<Topic> publish(UserAp userAp) {
        return userAp.publish(this);
    }

    private class MessageFinder implements Finder<com.jadenine.circle.model.entity.Message,
            Message> {
        @Override
        public Message find(com.jadenine.circle.model.entity.Message messageEntity) {
            for(Message message : messages) {
                if(message.getMessageId().equals(messageEntity.getMessageId())){
                    return message;
                }
            }
            return null;
        }

        @Override
        public Message bind(com.jadenine.circle.model.entity.Message message) {
            return Message.bind(message);
        }
    }

    private class MessageListerDelegate implements DomainLister.Delegate<Message> {

        @Override
        public boolean isDBLoaded() {
            return loaded;
        }

        @Override
        public void onDBLoaded() {
            loaded = true;
        }

        @Override
        public Observable<List<Message>> createDBObservable() {
            return messageDBService.listMessages(getTopicId()).map(getDBMapper());
        }

        @Override
        public Observable<List<Message>> createRestObservable() {
            return messageRestService.listMessages
                    (getTopicId()).map(getRestMapper());
        }

        @Override
        public List<Message> getRestStartSource() {
            return messages;
        }

        private DBMapper getDBMapper() {
            return new DBMapper(finder, messages);
        }

        private RestListMapper getRestMapper() {
            return new RestListMapper(finder, messages);
        }
    }
}
