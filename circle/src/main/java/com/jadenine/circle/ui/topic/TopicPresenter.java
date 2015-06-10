package com.jadenine.circle.ui.topic;

import android.os.Bundle;

import com.jadenine.circle.entity.Topic;
import com.jadenine.circle.entity.UserAp;
import com.jadenine.circle.request.JSONListWrapper;
import com.jadenine.circle.request.TopicService;
import com.jadenine.circle.ui.composer.ComposerPath;
import com.jadenine.circle.ui.message.MessagePath;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import flow.Flow;
import mortar.ViewPresenter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by linym on 6/10/15.
 */
public class TopicPresenter extends ViewPresenter<TopicView> {
    private final TopicService topicService;
    private final UserAp userAp;

    private Subscription running = Subscriptions.empty();

    public TopicPresenter(TopicService topicService, UserAp userAp) {
        this.topicService = topicService;
        this.userAp = userAp;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(!hasView()) return;

        loadTopics();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        running.unsubscribe();
    }

    void loadTopics() {
        Observable<JSONListWrapper<Topic>> topicsObservable = topicService.listTopics
                (userAp.getAP())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        running = topicsObservable.subscribe(new Observer<JSONListWrapper<Topic>>() {
            @Override
            public void onCompleted() {
                running = Subscriptions.empty();
            }

            @Override
            public void onError(Throwable e) {
                running = Subscriptions.empty();
            }

            @Override
            public void onNext(JSONListWrapper<Topic> topicJSONListWrapper) {
                if (!hasView()) return;
                List<Topic> topics = topicJSONListWrapper.getAll();

                Collections.sort(topics, new Comparator<Topic>() {
                    @Override
                    public int compare(Topic lhs, Topic rhs) {
                        return (int) (rhs.getTimestamp() - lhs.getTimestamp());
                    }
                });
                getView().getTopicAdapter().setTopics(topics);
            }
        });
    }

    void addMessage() {
        Flow.get(getView().getContext()).set(new ComposerPath(userAp));
    }

    void onOpenTopic(int position) {
        Topic topic = getView().getTopicAdapter().getTopic(position);
        Flow.get(getView().getContext()).set(new MessagePath(topic));
    }
}