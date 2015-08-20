package com.jadenine.circle.domain;

import android.text.TextUtils;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.CircleEntity;
import com.jadenine.circle.model.state.TimelineType;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 8/19/15.
 */
public class Circle{

    private final CircleEntity entity;
    private final BaseTimeline<Bomb> timeline;

    @Inject
    BombComposer bombComposer;

    public Circle(CircleEntity entity) {
        this.entity = entity;
        DaggerService.getDomainComponent().inject(this);
        BombLoader loader = new BombLoader(getCircleId(), Constants.PAGE_SIZE);
        DaggerService.getDomainComponent().inject(loader);

        this.timeline = new BaseTimeline<>(getCircleId(), TimelineType.TOPIC, loader);
    }

    public CircleEntity getEntity() {
        return entity;
    }

    public String getCircleId() {
        return entity.getCircleId();
    }

    public String getName() {
        return entity.getName();
    }

    public void merge(CircleEntity circleEntity) {
        if(circleEntity.getTimestamp() - entity.getTimestamp() > 0) {
            entity.setName(circleEntity.getName());
        }
    }

    @Override
    public String toString(){
        return TextUtils.isEmpty(getName())? getCircleId(): getName();
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

    public Observable<String> uploadImage(InputStream inputStream, String mimeType) {
        return bombComposer.uploadImage(inputStream, mimeType);
    }

    public Group<Bomb> getTopic(Long groupId) {
        return timeline.getRange(groupId).getGroup(groupId);
    }

    public boolean hasMore() {
        return timeline.hasMore();
    }

    public boolean hasUnread() {
        return timeline.getHasUnread();
    }

    public void setHasUnread(boolean hasUnread){
        timeline.setHasUnread(hasUnread);
    }
}
