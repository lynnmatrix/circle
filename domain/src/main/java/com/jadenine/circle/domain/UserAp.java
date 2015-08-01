package com.jadenine.circle.domain;

import android.text.TextUtils;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.UserApEntity;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp implements ApSource.Updatable<UserApEntity> {

    private final UserApEntity entity;
    private final BaseTimeline<Bomb> timeline;

    @Inject
    BombComposer bombComposer;

    public static UserAp build(UserApEntity userApEntity) {
        return new UserAp(userApEntity);
    }

    public UserAp(UserApEntity entity) {
        this.entity = entity;
        DaggerService.getDomainComponent().inject(this);
        BombLoader loader = new BombLoader(getAP(), Constants.PAGE_SIZE);
        DaggerService.getDomainComponent().inject(loader);

        this.timeline = new BaseTimeline<>(getAP(), loader);
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

    public Observable<List<UserAp>> connect(Account account) {
        return account.addUserAp(this);
    }

    public Observable<List<TimelineRange<Bomb>>> refresh() {
        return timeline.refresh();
    }

    public List<TimelineRange<Bomb>> getAllTimelineRanges() {
        return timeline.getAllRanges();
    }

    public Observable<List<TimelineRange<Bomb>>> loadMore() {
        return timeline.loadMore();
    }

    public Observable<List<TimelineRange<Bomb>>> loadMore(TimelineRange range) {
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

    public Group<Bomb> getTopic(Long groupId) {
        return timeline.getRange(groupId).getGroup(groupId);
    }

    public boolean hasMore() {
        return timeline.hasMore();
    }
}