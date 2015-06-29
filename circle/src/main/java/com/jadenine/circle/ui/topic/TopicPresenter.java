package com.jadenine.circle.ui.topic;

import android.os.Bundle;

import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.ui.composer.ComposerPath;
import com.jadenine.circle.ui.message.MessagePath;

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
    private final UserAp userAp;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }
    private Subscription loadingMoreSubscription = Subscriptions.empty();{
        loadingMoreSubscription.unsubscribe();
    }

    public TopicPresenter(UserAp userAp) {
        this.userAp = userAp;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(!hasView()) return;

        getView().collapsingToolbarLayout.setTitle(userAp.getSSID());

        loadTopics();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        refreshSubscription.unsubscribe();
        loadingMoreSubscription.unsubscribe();
    }

    void loadTopics() {
        Observable<List<Topic>> topicsObservable = userAp.refreshTopic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        refreshSubscription = topicsObservable.subscribe(new Observer<List<Topic>>() {
            @Override
            public void onCompleted() {
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
            }

            @Override
            public void onNext(List<Topic> topics) {
                if (!hasView()) return;

                getView().getTopicAdapter().setTopics(topics);
            }
        });
    }

    void loadMore() {
        if(!userAp.hasMoreTopic() || !loadingMoreSubscription.isUnsubscribed() ) {
            return;
        }

        Observable<List<Topic>> topicsObservable = userAp.loadMore()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        loadingMoreSubscription = topicsObservable.subscribe(new Observer<List<Topic>>() {
            @Override
            public void onCompleted() {
                loadingMoreSubscription = Subscriptions.empty();
                loadingMoreSubscription.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                loadingMoreSubscription = Subscriptions.empty();
                loadingMoreSubscription.unsubscribe();
            }

            @Override
            public void onNext(List<Topic> topics) {
                if (!hasView()) return;

                getView().getTopicAdapter().setTopics(topics);
            }
        });
    }

    void addTopic() {
        Flow.get(getView().getContext()).set(new ComposerPath(userAp.getAP()));
    }

    void onOpenTopic(int position) {
        Topic topic = getView().getTopicAdapter().getTopic(position);
        Flow.get(getView().getContext()).set(new MessagePath(topic.getAp(), topic.getTopicId()));
    }
}
