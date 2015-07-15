package com.jadenine.circle.domain;

import android.text.TextUtils;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.TopicService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp implements Updatable<UserApEntity>{

    private final UserApEntity entity;
    @Inject
    TopicService topicRestService;

    private final TopicTimeline topicTimeline = new TopicTimeline(this);

    public static UserAp build(UserApEntity userApEntity) {
        return new UserAp(userApEntity);
    }

    public UserAp(UserApEntity entity) {
        this.entity = entity;
        DaggerService.getDomainComponent().inject(this);
    }

    public UserApEntity getEntity() {
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

    public Observable<List<Topic>> refreshTopic(){
        return topicTimeline.refresh();
    }

    public Observable<List<Topic>> loadMore(){
        return topicTimeline.loadMore();
    }

    public boolean hasMoreTopic(){
        return topicTimeline.hasMore();
    }

    public Topic getTopic(String topicId) {
        return topicTimeline.find(topicId);
    }

    public Observable<List<UserAp>> connect(Account account) {
        return account.addUserAp(this);
    }

    Observable<Topic> publish(final Topic topic) {
        Observable<Topic> observable = topicRestService.addTopic(topic.getEntity())
                .map(new Func1<TopicEntity, Topic>() {
                    @Override
                    public Topic call(TopicEntity topicEntity) {
                        return topicTimeline.addPublishedTopic(topicEntity);
                    }
                });

        return observable;
    }

}