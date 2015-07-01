package com.jadenine.circle.domain;

import android.text.TextUtils;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.entity.Image;
import com.jadenine.circle.model.entity.MessageEntity;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.rest.AzureBlobUploader;
import com.jadenine.circle.model.rest.ImageService;
import com.jadenine.circle.model.rest.MessageService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.Subscriber;
import rx.android.internal.Preconditions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by linym on 6/10/15.
 */
public class Topic implements Updatable<TopicEntity>{
    private static final int MESSAGE_CAPABILITY = Integer.MAX_VALUE;

    private final TopicEntity entity;
    private final List<Message> messages = new ArrayList<>();

    List<String> images;

    @Inject
    MessageService messageRestService;
    @Inject
    MessageDBService messageDBService;
    @Inject
    ImageService imageService;

    @Inject
    Lazy<Account> account;

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
        Observable<Message> observable = messageRestService.addMessage(message.getEntity()).map (new RestMapper<>(mapperDelegate));

        return observable;
    }

    public Observable<Topic> publish(final UserAp userAp) {
        return imageService.getWritableSas().map(new Func1<Image, String>() {
            @Override
            public String call(Image image) {
                return null;
            }
        }).flatMap(new Func1<String, Observable<Topic>>() {
            @Override
            public Observable<Topic> call(String imageId) {
                return userAp.publish(Topic.this);
            }
        });
    }

    public Observable<String> uploadImage(final InputStream imageInputStream, final String
            mimeType) {
        return imageService.getWritableSas().flatMap(new Func1<Image, Observable<String>>() {
            @Override
            public Observable<String> call(final Image image) {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        boolean success = AzureBlobUploader.upload(image.getWritableSas(),
                                imageInputStream, mimeType);
                        if (!subscriber.isUnsubscribed()) {
                            if (success) {
                                subscriber.onNext(image.getReadableSas());
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Exception("Fail to upload image"));
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }
        });
    }

    public void addImage(String imageReadUri) {
        Preconditions.checkArgument(null!= imageReadUri && !imageReadUri.isEmpty(), "Empty uri.");

        if(!TextUtils.isEmpty(getTopicId())) {
            throw new IllegalStateException("Cannot add images to topic which has been uploaded.");
        }

        if(null == getImages()) {
            images = new ArrayList();
        }
        boolean added = false;
        for(String uri : images) {
            if(TextUtils.equals(imageReadUri, uri)) {
                added = true;
                break;
            }
        }

        if(!added) {
            images.add(imageReadUri);
            getEntity().setImages(TextUtils.join(TopicEntity.IMAGE_DELIMITER, images));
        }
    }

    public List<String> getImages() {
        String imageUries = getEntity().getImages();

        if(null == images && null != imageUries && !imageUries.isEmpty()) {
            String[] uriArray = imageUries.split(TopicEntity.IMAGE_DELIMITER);
            images = new ArrayList<>(uriArray.length);
            for (String imageUri : uriArray) {
                imageUri = imageUri.trim();
                if (!imageUri.isEmpty()) {
                    images.add(imageUri);
                }
            }
        }

        return images;
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
                    (account.get().getDeviceId(), getAp(), getTopicId()).map(new
                    RefreshMapper<MessageEntity,
                    Message>
                    (mapperDelegate));
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
