package com.jadenine.circle.domain;

import android.text.TextUtils;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.TopicService;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp implements Updatable<UserApEntity>{

    public static final int BOMB_PAGE_COUNT = 200;
    private final UserApEntity entity;
    private final BaseTimeline<Bomb> timeline;

    @Inject
    TopicService topicRestService;

    @Inject
    BombComposer bombComposer;

    private final TopicTimeline topicTimeline = new TopicTimeline(this);

    public static UserAp build(UserApEntity userApEntity) {
        return new UserAp(userApEntity);
    }

    public UserAp(UserApEntity entity) {
        this.entity = entity;
        BombLoader loader = new BombLoader(getAP(), BOMB_PAGE_COUNT);
        DaggerService.getDomainComponent().inject(loader);

        this.timeline = new BaseTimeline<>(getAP(), loader);

        DaggerService.getDomainComponent().inject(this);
        DaggerService.getDomainComponent().inject(topicTimeline);
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

    public Observable<List<TimelineRange<Bomb>>> refresh() {
        return timeline.refresh();
    }

    public List<TimelineRange<Bomb>> getAllTimelineRanges() {
        return timeline.getAllRanges();
    }

    public Observable<List<TimelineRange<Bomb>>> loadMoreBomb() {
        return timeline.loadMore();
    }

    public Observable<List<TimelineRange<Bomb>>> loadMoreBomb(TimelineRange range) {
        return timeline.loadMore(range);
    }

    public Observable<Bomb> publish(final Bomb bomb) {
        Observable<Bomb> observable = bombComposer.send(bomb)
                .map(new Func1<Bomb, Bomb>() {
                    @Override
                    public Bomb call(Bomb bomb1) {
                        timeline.addPublished(bomb1);
                        return bomb1;
                    }
                });

        return observable;
    }

    public Observable<String> uploadImage(InputStream inputStream, String mimeType) {
        return bombComposer.uploadImage(inputStream, mimeType);
    }

    public Group<Bomb> getBombGroup(Long groupId) {
        return timeline.getRange(groupId).getGroup(groupId);
    }

    public boolean hasMore() {
        return timeline.hasMore();
    }
}