package com.jadenine.circle.domain;

import android.text.TextUtils;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.TopicService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp implements Updatable<UserApEntity>{

    private final UserApEntity entity;
    private final List<Topic> topics = new ArrayList<>();

    @Inject
    TopicService restService;
    @Inject
    TopicDBService dbService;
    private boolean loaded = false;

    private final TopicFinder finder = new TopicFinder();
    private final DomainLister<Topic> topicLister = new DomainLister<>(new TopicListerDelegate());

    public static UserAp build(UserApEntity userApEntity) {
        return new UserAp(userApEntity);
    }

    public UserAp(UserApEntity entity) {
        this.entity = entity;
        DaggerService.getDomainComponent().inject(this);
    }

    UserApEntity getEntity() {
        return entity;
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
    public void merge(UserApEntity userApEntity) {
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

    public Observable<List<UserAp>> connect(Account account) {
        return account.addUserAp(this);
    }

    Observable<Topic> publish(final Topic topic) {
        Observable<Topic> observable = restService.addTopic(topic.getEntity()).map(new
                RestMapper<>(finder, topics));

        return observable;
    }

    private class TopicFinder implements Finder<TopicEntity, Topic>{
        @Override
        public Topic find(TopicEntity topicEntity) {
            for (Topic domainEntity : topics) {
                if (domainEntity.getTopicId().equals(topicEntity.getTopicId())) {
                    return domainEntity;
                }
            }
            return null;
        }

        @Override
        public Topic build(TopicEntity topicEntity) {
            return Topic.build(topicEntity);
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
