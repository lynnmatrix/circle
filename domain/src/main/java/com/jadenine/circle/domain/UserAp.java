package com.jadenine.circle.domain;

import android.text.TextUtils;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.rest.TopicService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp implements Updatable<com.jadenine.circle.model.entity.UserAp>{

    private final com.jadenine.circle.model.entity.UserAp entity;
    private final List<Topic> topics = new ArrayList<>();

    @Inject
    TopicService restService;
    @Inject
    TopicDBService dbService;
    private boolean loaded = false;

    private final TopicFinder finder = new TopicFinder();
    private final DomainLister<Topic> topicLister = new DomainLister<>(new TopicListerDelegate());

    public static UserAp build(com.jadenine.circle.model.entity.UserAp userApEntity) {
        return new UserAp(userApEntity);
    }

    public UserAp(com.jadenine.circle.model.entity.UserAp entity) {
        this.entity = entity;
        DaggerService.getDomainComponent().inject(this);
    }

    public String getAP() {
        return entity.getAP();
    }

    public String getUser() {
        return entity.getUser();
    }

    public String getSSID() {
        return entity.getSSID();
    }

    @Override
    public void merge(com.jadenine.circle.model.entity.UserAp userApEntity) {
        if(userApEntity.getTimestamp() - entity.getTimestamp() > 0) {
            entity.setSSID(userApEntity.getSSID());
            entity.save();
        }
    }

    @Override
    public String toString(){
        return TextUtils.isEmpty(getSSID())?getAP(): getSSID();
    }

    public Observable<List<Topic>> listTopic(){
        return topicLister.list();
    }

    public Observable<List<UserAp>> save(Account account) {
        return account.addUserAp(this);
    }

    public com.jadenine.circle.model.entity.UserAp getEntity() {
        return entity;
    }

    Observable<Topic> publish(final Topic topic) {
        if(null == finder.find(topic.getEntity())) {
            topics.add(topic);
        }
        Observable<Topic> observable = restService.addTopic(topic.getEntity()).map(new
                RestMapper<>(finder, topics));

        observable.subscribe(new Observer<Topic>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                topics.remove(topic);
            }

            @Override
            public void onNext(Topic topic1) {
            }
        });
        return observable;
    }

    private class TopicFinder implements Finder<com.jadenine.circle.model.entity.Topic, Topic>{
        @Override
        public Topic find(com.jadenine.circle.model.entity.Topic topic) {
            for (Topic domainEntity : topics) {
                if (domainEntity.getTopicId().equals(topic.getTopicId())) {
                    return domainEntity;
                }
            }
            return null;
        }

        @Override
        public Topic bind(com.jadenine.circle.model.entity.Topic topic) {
            return Topic.bind(topic);
        }
    }

    private class TopicListerDelegate implements DomainLister.Delegate<Topic> {

        @Override
        public boolean isDBLoaded() {
            return loaded;
        }

        @Override
        public void onDBLoaded() {
            loaded = true;
        }

        @Override
        public Observable<List<Topic>> createDBObservable() {
            return dbService.listTopics(getAP()).map(new DBMapper<>(finder, topics));
        }

        @Override
        public Observable<List<Topic>> createRestObservable() {
            return restService.listTopics(getAP()).map(new RestListMapper<>(finder, topics));
        }

        @Override
        public List<Topic> getRestStartSource() {
            return topics;
        }
    }
}
