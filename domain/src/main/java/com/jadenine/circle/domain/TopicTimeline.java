package com.jadenine.circle.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.db.impl.TimelineStateDBService;
import com.jadenine.circle.model.entity.MessageEntity;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.rest.TimelineResult;
import com.jadenine.circle.model.rest.TopicService;
import com.jadenine.circle.model.state.TimelineState;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.DeleteModelListTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linym on 7/10/15.
 */
public class TopicTimeline implements Loadable<Topic> {
    private static final Integer PAGE_SIZE = 20;
    private static final Integer INITIAL_PAGE_SIZE = 50;
    private static final int TOPIC_CAPABILITY = 200;

    private final List<Topic> topics = new ArrayList<>();
    private TimelineState timelineState;
    private boolean dbLoaded = false;

    @Inject
    TopicService topicRestService;
    @Inject
    TopicDBService topicDBService;
    @Inject
    MessageDBService messageDBService;
    @Inject
    TimelineStateDBService timelineStateDBService;

    private final WeakReference<UserAp> userApRef;

    public TopicTimeline(UserAp userAp) {
        this.userApRef = new WeakReference<>(userAp);
    }

    public Topic find(String topicId) {
        for (Topic topic : topics) {
            if (topic.getTopicId().equals(topicId)) {
                return topic;
            }
        }
        return null;
    }

    @Override
    public Observable<List<Topic>> refresh() {

        Observable<List<Topic>> observable;
        if (!dbLoaded) {
            Observable<List<Topic>> dbObservable = createDBObservable();
            observable = dbObservable.flatMap(new Func1<List<Topic>, Observable<List<Topic>>>() {
                @Override
                public Observable<List<Topic>> call(List<Topic> ds) {
                    List[] lists = {topics};
                    Observable restObservable = createRefreshRestObservable();
                    return Observable.mergeDelayError(Observable.from(lists), restObservable);
                }
            }).subscribeOn(Schedulers.io());
        } else {
            List[] lists = {topics};
            Observable restObservable = createRefreshRestObservable();
            observable = Observable.mergeDelayError(Observable.from(lists), restObservable)
                    .subscribeOn(Schedulers.io());
        }
        return observable;
    }

    @Override
    public Observable<List<Topic>> loadMore() {
        List[] lists = {topics};
        Observable restObservable = createLoadMoreRestObservable();
        return restObservable.startWith(Observable.from(lists)).subscribeOn(Schedulers.io());
    }

    @Override
    public boolean hasMore() {
        return timelineState.getHasMoreTopic();
    }

    public Topic addPublishedTopic(TopicEntity topicEntity) {
        Topic topic = null;
        if(null == find(topicEntity.getTopicId())) {
            topic = Topic.build(topicEntity);
            topics.add(0, topic);
            topicEntity.save();
            if(null != topicEntity.getMessages() && !topicEntity.getMessages().isEmpty()) {
                TransactionManager.getInstance().addTransaction(new SaveModelTransaction
                        (ProcessModelInfo.withModels(topicEntity.getMessages())));
            }
        }
        return topic;
    }

    private Observable<List<Topic>> createDBObservable() {
        Observable<TimelineState> timelineStateObservable = timelineStateDBService
                .getTimelineState(getAp()).map(new Func1<TimelineState, TimelineState>() {

            @Override
            public TimelineState call(TimelineState timelineState) {
                Timber.i("Got timeline state");
                TopicTimeline.this.timelineState = timelineState;
                return TopicTimeline.this.timelineState;
            }
        });

        final Observable<Map<String, List<Message>>> messageObservable = messageDBService
                .listMessagesOfAp(getAp()).map(new Func1<List<MessageEntity>, Map<String,
                        List<Message>>>() {

            @Override
            public Map<String, List<Message>> call(List<MessageEntity> messageEntities) {
                Timber.i("Got messages");
                HashMap<String, List<Message>> topicMessageMap = new HashMap<>();
                for (MessageEntity messageEntity : messageEntities) {
                    Message message = Message.build(messageEntity);
                    List<Message> messages = topicMessageMap.get(message.getTopicId());
                    if (null == messages) {
                        messages = new LinkedList<>();
                        topicMessageMap.put(message.getTopicId(), messages);
                    }
                    messages.add(message);
                }
                return topicMessageMap;
            }
        });
        Observable<List<Topic>> topicObservable = topicDBService.listTopics(getAp()).map(new
                TopicDBMapper());

        return  Observable.zip(timelineStateObservable, topicObservable, messageObservable, new
                Func3<TimelineState, List<Topic>, Map<String, List<Message>>, List<Topic>>() {

            @Override
            public List<Topic> call(TimelineState timelineState, List<Topic> topics, Map<String,
                    List<Message>> stringListMap) {
                Timber.i("dbloaded");
                dbLoaded = true;
                for (Topic topic : topics) {
                    List<Message> messages = stringListMap.get(topic.getTopicId());
                    if (null != messages) {
                        topic.setMessages(messages);
                    }
                }
                return topics;
            }
        });
    }

    private Observable<List<Topic>> createRefreshRestObservable() {
        return topicRestService.refresh(getAp(), INITIAL_PAGE_SIZE, timelineState
                .getOldestTopicId(), timelineState.getLatestTimestamp()
        ).map(new TopicRefreshMapper());
    }

    private Observable createLoadMoreRestObservable() {
        return topicRestService.loadMore(getAp(), PAGE_SIZE, timelineState
                .getOldestTopicId(), timelineState.getLatestTimestamp()).map(new TopicLoadMoreMapper());
    }

    private String getAp() {
        return userApRef.get().getAP();
    }

    private class TopicRefreshMapper implements Func1<TimelineResult<TopicEntity>, List<Topic>> {
        @Override
        public List<Topic> call(TimelineResult<TopicEntity> topicResult) {

            List<TopicEntity> topicNeedSave = new ArrayList<>(DomainUtils.getSize(topicResult
                    .getAdd()) + DomainUtils.getSize(topicResult.getUpdate()));
            List<MessageEntity> messageNeedSave = new LinkedList<>();

            if (!DomainUtils.checkEmpty(topicResult.getAdd())) {
                handleAdd(topicResult, topicNeedSave, messageNeedSave);
            }

            if(!DomainUtils.checkEmpty(topicResult.getUpdate())) {
                handleUpdate(topicResult, topicNeedSave, messageNeedSave);
            }

            boolean needClear = clearIfNeed(topicResult);
            if(needClear) {
                timelineState.setHasMoreTopic(true);
            }
            timelineState.setOldestTopicId(getOldestTopicId());
            timelineState.setLatestTimestamp(getLatestTopicTimestamp(topicNeedSave));

            saveEntities(messageNeedSave);
            saveEntities(topicNeedSave);
            timelineState.save();

            return topics;
        }

        private void handleAdd(TimelineResult<TopicEntity> topicResult,
                               List<TopicEntity> topicNeedSave,
                               List<MessageEntity> messageNeedSave) {
            topics.addAll(0, buildTopics(topicResult, topicNeedSave, messageNeedSave));
        }

        private void handleUpdate(TimelineResult<TopicEntity> topicResult,
                                  List<TopicEntity> topicNeedSave,
                                  List<MessageEntity> messageNeedSave) {
            Topic topic;
            for(TopicEntity topicEntity : topicResult.getUpdate()) {
                topic = find(topicEntity.getTopicId());
                if(null == topic) {
                    Timber.wtf("Entity", "No topic with id " + topicEntity.getTopicId() + " " +
                            "found for update");
                    continue;
                }

                topic.merge(topicEntity);

                topicNeedSave.add(topic.getEntity());
                if(!DomainUtils.checkEmpty(topicEntity.getMessages())) {
                    messageNeedSave.addAll(topicEntity.getMessages());
                }
            }
        }

        private boolean clearIfNeed(TimelineResult<TopicEntity> topicResult) {
            int startIndexToClear = getStartIndexToClear(topicResult);

            boolean needClear = startIndexToClear < topics.size();

            if (needClear) {
                clearTopics(startIndexToClear);
            }
            return needClear;
        }

        private int getStartIndexToClear(TimelineResult<TopicEntity> topicResult) {
            int upperIndexKept = topics.size();
            if (topicResult.hasMore()) {
                if(DomainUtils.checkEmpty(topicResult.getUpdate())) {
                    upperIndexKept = topicResult.getAdd().size();
                } else {
                    String nextTopicId = topicResult.getNextId();
                    Topic nextTopic = find(nextTopicId);
                    if (null != nextTopic) {
                        upperIndexKept = topics.indexOf(nextTopic) + 1;
                    } else {
                        Timber.wtf("Entity", "No topic found which has updates");
                    }
                }
            }

            if (upperIndexKept > TOPIC_CAPABILITY) {
                upperIndexKept = TOPIC_CAPABILITY;
            }
            return upperIndexKept;
        }

        private void clearTopics(int upperIndexKept) {
            List<Topic> keptTopics = new ArrayList<>(topics.subList(0, upperIndexKept));
            List<Topic> topicsToClear = new ArrayList<>(topics.subList(upperIndexKept, topics
                    .size()));

            topics.clear();
            topics.addAll(keptTopics);

            List<TopicEntity> topicEntitiesToClear = DomainUtils.getEntities(topicsToClear);
            List<MessageEntity> messageEntitiesToClear= getMessageEntities(topicEntitiesToClear);

            deleteEntities(messageEntitiesToClear);
            deleteEntities(topicEntitiesToClear);
        }
    }

    private class TopicLoadMoreMapper implements Func1<TimelineResult<TopicEntity>, List<Topic>> {
        @Override
        public List<Topic> call(TimelineResult<TopicEntity> topicResult) {

            List<TopicEntity> topicNeedSave = new ArrayList<>(DomainUtils.getSize(topicResult
                    .getAdd()) + DomainUtils.getSize(topicResult.getUpdate()));
            List<MessageEntity> messageNeedSave = new LinkedList<>();

            topics.addAll(buildTopics(topicResult, topicNeedSave, messageNeedSave));
            timelineState.setHasMoreTopic(topicResult.hasMore());
            timelineState.setOldestTopicId(getOldestTopicId());

            saveEntities(topicNeedSave);
            saveEntities(messageNeedSave);
            timelineState.save();
            return topics;
        }
    }

    private class TopicDBMapper implements Func1<List<TopicEntity>, List<Topic>> {
        @Override
        public List<Topic> call(List<TopicEntity> topicEntities) {
            Timber.i("Got topics");
            for (TopicEntity entity : topicEntities) {
                Topic domainModel = find(entity.getTopicId());
                if (null == domainModel) {
                    domainModel = Topic.build(entity);
                    topics.add(domainModel);
                }
            }
            return topics;
        }
    }

    @Nullable
    private String getOldestTopicId() {
        if (topics.isEmpty()) {
            return null;
        }

        Topic oldestTopic = topics.get(topics.size() - 1);
        return oldestTopic.getTopicId();
    }

    @Nullable
    private Long getLatestTopicTimestamp(List<TopicEntity> topicEntities) {
        Long latestTopicStamp = null;
        for (TopicEntity topic : topicEntities) {
            if (null == latestTopicStamp || topic.getTimestamp() > latestTopicStamp) {
                latestTopicStamp = topic.getTimestamp();
            }
        }
        return latestTopicStamp;
    }

    private void deleteEntities(List entities) {
        if (!entities.isEmpty()) {
            TransactionManager.getInstance().addTransaction(new DeleteModelListTransaction
                    (ProcessModelInfo.withModels(entities)));
        }
    }
    private void saveEntities(List entities) {
        if(!entities.isEmpty()) {
            TransactionManager.getInstance().addTransaction(new SaveModelTransaction
                    (ProcessModelInfo.withModels(entities)));
        }
    }

    @NonNull
    private List<MessageEntity> getMessageEntities(List<TopicEntity> topics) {
        List<MessageEntity> messageEntities = new LinkedList<>();
        for(TopicEntity topic : topics) {
            if(!DomainUtils.checkEmpty(topic.getMessages())) {
                for (MessageEntity message : topic.getMessages()) {
                    messageEntities.add(message);
                }
            }
        }
        return messageEntities;
    }

    private List<Topic> buildTopics(TimelineResult<TopicEntity> topicResult,
                                    List<TopicEntity> topicNeedSave,
                                    List<MessageEntity> messageNeedSave) {

        topicNeedSave.addAll(topicResult.getAdd());
        messageNeedSave.addAll(getMessageEntities(topicResult.getAdd()));

        List<Topic> addTopics = new ArrayList<>(topicResult.getAdd().size());
        for (TopicEntity topicEntity : topicResult.getAdd()) {
            addTopics.add(Topic.build(topicEntity));
        }
        return addTopics;
    }
}
