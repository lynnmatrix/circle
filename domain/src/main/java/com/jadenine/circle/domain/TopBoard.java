package com.jadenine.circle.domain;

import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.rest.TimelineRangeResult;
import com.raizlabs.android.dbflow.annotation.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by linym on 8/11/15.
 */
public class TopBoard {
    private final int topK;
    private final TopLoader loader;
    private List<Group<Bomb>> topics = Collections.emptyList();

    public TopBoard(TopLoader loader, int estimateTopK) {
        this.loader = loader;
        this.topK = estimateTopK;
    }

    public @NotNull Observable<ArrayList<Group<Bomb>>> refresh(){
        return loader.refresh().flatMap(new Func1<TimelineRangeResult<Bomb>, Observable<ArrayList<Group<Bomb>>>>() {
            @Override
            public Observable<ArrayList<Group<Bomb>>> call(TimelineRangeResult<Bomb> bombTimelineRangeResult) {
                ArrayList<Group<Bomb>> topTopics = new ArrayList<>(topK);

                LongSparseArray<Group<Bomb>> groupedBomb = groupBomb(bombTimelineRangeResult.getAll());

                for (int i = 0; i < groupedBomb.size(); i++) {
                    Group<Bomb> topic = groupedBomb.valueAt(i);
                    if (topic.hasRootEntity()) {
                        topTopics.add(topic);
                    }
                }

                Collections.sort(topTopics, new Comparator<Group<Bomb>>() {
                    @Override
                    public int compare(Group<Bomb> lhs, Group<Bomb> rhs) {
                        return rhs.getCount() - lhs.getCount();
                    }
                });

                topics = topTopics;

                return Observable.just(new ArrayList<>(topics));
            }

            @NonNull
            private LongSparseArray<Group<Bomb>> groupBomb(List<Bomb> bombs) {
                LongSparseArray<Group<Bomb>> groupedBomb = new LongSparseArray<>(topK);
                for (Bomb bomb : bombs) {
                    final Long groupId = bomb.getGroupId();
                    Group<Bomb> topic = groupedBomb.get(groupId);
                    if (null == topic) {
                        topic = getTopic(groupId);
                        if (null == topic) {
                            topic = new Group<>(groupId);
                        }
                        groupedBomb.put(groupId, topic);
                    }
                    topic.addEntity(bomb);
                }
                return groupedBomb;
            }
        }).subscribeOn(Schedulers.io());
    }

    public List<Group<Bomb>> getTops() {
        return new ArrayList<>(topics);
    }

    public Group<Bomb> getTopic(Long groupId) {
        Group<Bomb> result = null;
         for(Group<Bomb> topic : getTops()) {
            if(topic.getId().equals(groupId)){
                result = topic;
                break;
            }
        }
        return result;
    }
}
