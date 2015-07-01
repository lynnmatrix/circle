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
    private static final Integer PAGE_SIZE = 20;
    private static final Integer INITIAL_PAGE_SIZE = 50;
    private static final int TOPIC_CAPABILITY = 200;

    private final UserApEntity entity;
    private final List<Topic> topics = new ArrayList<>();
    private boolean hasMoreTopic = true;

    @Inject
    TopicService topicRestService;

    @Inject
    TopicDBService topicDBService;

    private boolean loaded = false;
    private final TopicMapperDelegate mapperDelegate = new TopicMapperDelegate();
    private final DomainLister<Topic> topicLister = new DomainLister<>(new TopicListerDelegate());

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
    public void remove() {
        getEntity().delete();
    }

    @Override
    public String toString(){
        return TextUtils.isEmpty(getSSID())?getAP(): getSSID();
    }

    public Observable<List<Topic>> refreshTopic(){
        return topicLister.list();
    }

    public Observable<List<Topic>> loadMore(){
        return topicLister.loadMore();
    }

    public Observable<List<UserAp>> connect(Account account) {
        return account.addUserAp(this);
    }

    Observable<Topic> publish(final Topic topic) {
        Observable<Topic> observable = topicRestService.addTopic(topic.getEntity()).map(new
                RestMapper<>(mapperDelegate));

        return observable;
    }

    public Topic getTopic(String topicId) {
        for (Topic topic : topics) {
            if (topic.getTopicId().equals(topicId)) {
                return topic;
            }
        }
        return null;
    }

    public boolean hasMoreTopic(){
        return hasMoreTopic;
    }

    private class TopicMapperDelegate implements MapperDelegate<TopicEntity, Topic> {
        @Override
        public Topic find(TopicEntity topicEntity) {
            return getTopic(topicEntity.getTopicId());
        }

        @Override
        public Topic build(TopicEntity topicEntity) {
            return Topic.build(topicEntity);
        }

        @Override
        public void setHasMore(boolean hasMore) {
            UserAp.this.hasMoreTopic = hasMore;
        }

        @Override
        public List<Topic> getOriginSource() {
            return topics;
        }

        @Override
        public int getCapability() {
            return TOPIC_CAPABILITY;
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
            return topicDBService.listTopics(getAP()).map(new DBMapper<>(mapperDelegate));
        }

        @Override
        public Observable<List<Topic>> createRefreshRestObservable() {
            return topicRestService.listTopics(getAP(), INITIAL_PAGE_SIZE, getLatestTopicTimestamp(), null).map
                    (new RefreshMapper(mapperDelegate));
        }

        @Override
        public Observable<List<Topic>> createLoadMoreRestObservable() {
            return topicRestService.listTopics(getAP(), PAGE_SIZE, (String) null, getOldestTopicId())
                    .map(new LoadMoreMapper(mapperDelegate));
        }

        @Override
        public List<Topic> getRestStartSource() {
            return topics;
        }

        private String getOldestTopicId(){
            String oldestTopicId = null;
            // Id on circle server is auto decrement
            for(Topic topic : topics) {
                if (null == oldestTopicId
                        || topic.getTopicId().length() > oldestTopicId.length()
                        || topic.getTopicId().compareTo(oldestTopicId) > 0) {
                    oldestTopicId = topic.getTopicId();
                }
            }
            return oldestTopicId;
        }

        private Long getLatestTopicTimestamp() {
            Long latestTopicStamp = null;
            for(Topic topic : topics) {
                if (null == latestTopicStamp
                        || topic.getTimestamp() > latestTopicStamp) {
                    latestTopicStamp = topic.getTimestamp();
                }
            }
            return latestTopicStamp;
        }
    }

}