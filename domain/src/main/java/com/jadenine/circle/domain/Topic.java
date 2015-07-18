package com.jadenine.circle.domain;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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

    private final TopicEntity entity;
    private final List<Message> messages = new ArrayList<>();

    private List<String> images;

    @Inject
    MessageService messageRestService;
    @Inject
    MessageDBService messageDBService;
    @Inject
    ImageService imageService;
    @Inject
    AzureBlobUploader blobUploader;

    @Inject
    Lazy<Account> account;

    public static Topic build(TopicEntity entity) {
        return new Topic(entity);
    }

    public Topic(UserAp userAp, String content) {
        this(new TopicEntity(userAp.getAP(), userAp.getUser(), content));
    }

    public Topic(TopicEntity entity) {
        this.entity = entity;
        if(null != entity.getMessages() && !entity.getMessages().isEmpty()) {
            for(MessageEntity messageEntity : entity.getMessages()) {
                messages.add(Message.build(messageEntity));
            }
        }

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
        long timestamp = entity.getTimestamp();
        if(!messages.isEmpty()) {
            timestamp = messages.get(messages.size() -1).getTimestamp();
        }
        return timestamp;
    }

    @Override
    public void merge(TopicEntity entity) {
        if(entity.getTimestamp() - this.entity.getTimestamp() > 0) {
            this.entity.setTimestamp(entity.getTimestamp());
            this.entity.setLatestMessageId(entity.getLatestMessageId());
            this.entity.setMessageCount(entity.getMessageCount());
            this.entity.save();

            if(!DomainUtils.checkEmpty(entity.getMessages())) {
                mergeMessages(entity);
            }
        }
    }

    private void mergeMessages(TopicEntity topicEntity) {
        List<Message> addMessages = new ArrayList<>(topicEntity.getMessages().size());
        for(MessageEntity messageEntity : topicEntity.getMessages()) {
            Message message = getMessage(messageEntity.getMessageId());
            if(null != message) {
                Log.wtf("Entity", "No ");
                continue;
            }
            message = Message.build(messageEntity);
            addMessages.add(message);
        }
        messages.addAll(addMessages);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
    }

    Observable<Message> addReply(final Message message) {
        message.setTopicId(getTopicId());
        Observable<Message> observable = messageRestService.addMessage(message.getEntity()).map
                (new Func1<MessageEntity, Message>() {

            @Override
            public Message call(MessageEntity messageEntity) {
                Message msg = getMessage(messageEntity.getMessageId());
                if(null != msg) {
                    Log.wtf("Entity", "Message same as the new reply exists.");
                    return msg;
                }
                msg = Message.build(messageEntity);
                messages.add(msg);
                msg.getEntity().save();

                return msg;
            }
        });

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
                        boolean success = blobUploader.upload(image.getWritableSas(),
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

    @Nullable
    private Message getMessage(String messageId) {
        for(Message message : messages) {
            if(message.getMessageId().equals(messageId)){
                return message;
            }
        }
        return null;
    }
}
