package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.entity.MessageEntity;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.rest.MessageService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 6/10/15.
 */
public class Topic implements Updatable<TopicEntity>{
    private static final int MESSAGE_CAPABILITY = Integer.MAX_VALUE;

    private final TopicEntity entity;
    private final List<Message> messages = new ArrayList<>();

    @Inject
    MessageService messageRestService;
    @Inject
    MessageDBService messageDBService;
    private boolean loaded = false;
    private boolean hasMore = true;

    private final MessageMapperDelegate mapperDelegate = new MessageMapperDelegate();
    private final DomainLister<Message> messageLister = new DomainLister<>(new
            MessageListerDelegate());

    public static Topic build(TopicEntity entity) {
        return new Topic(entity);
    }

    public Topic(UserAp userAp, String content) {
        this(new TopicEntity(userAp.getAP(), userAp.getUser(), content));
    }

    public Topic(TopicEntity entity) {
        this.entity = entity;
        DaggerService.getDomainComponent().inject(this);
    }

    public TopicEntity getEntity() {
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

    public int getMessageCount() {
        return getEntity().getMessageCount();
    }

    public long getTimestamp() {
        return entity.getTimestamp();
    }

    @Override
    public void merge(TopicEntity entity) {
        if(entity.getTimestamp() - this.entity.getTimestamp() > 0) {
            this.entity.setTimestamp(entity.getTimestamp());
            this.entity.setLatestMessageId(entity.getLatestMessageId());
            this.entity.setMessageCount(entity.getMessageCount());
            this.entity.save();
        }
    }

    @Override
    public void remove() {
        getEntity().delete();
    }

    public Observable<List<Message>> listMessage(){
        return messageLister.list();
    }

    Observable<Message> addReply(final Message message) {
        message.setTopicId(getTopicId());
        Observable<Message> observable = messageRestService.addMessage(getAp(), message.getEntity
                ()).map(new RestMapper<>(mapperDelegate));

        return observable;
    }

    public Observable<Topic> publish(UserAp userAp) {
        return userAp.publish(this);
    }

    private class MessageMapperDelegate implements MapperDelegate<MessageEntity,
                Message> {
        @Override
        public Message find(MessageEntity messageEntity) {
            for(Message message : messages) {
                if(message.getMessageId().equals(messageEntity.getMessageId())){
                    return message;
                }
            }
            return null;
        }

        @Override
        public Message build(MessageEntity messageEntity) {
            return Message.build(messageEntity);
        }

        @Override
        public void setHasMore(boolean hasMore) {
            Topic.this.hasMore = hasMore;
        }

        @Override
        public List<Message> getOriginSource() {
            return messages;
        }

        @Override
        public int getCapability() {
            return MESSAGE_CAPABILITY;
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
        public Observable<List<Message>> createRefreshRestObservable() {
            return messageRestService.listMessages
                    (getTopicId()).map(new RefreshMapper<MessageEntity, Message>(mapperDelegate));
        }

        @Override
        public Observable<List<Message>> createLoadMoreRestObservable() {
            return null;
        }

        @Override
        public List<Message> getRestStartSource() {
            return messages;
        }

        private DBMapper getDBMapper() {
            return new DBMapper(mapperDelegate);
        }

    }
}
