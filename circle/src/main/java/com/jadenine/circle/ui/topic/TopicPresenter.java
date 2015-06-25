package com.jadenine.circle.ui.topic;

import android.os.Bundle;

import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.UserAp;
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
    private final UserAp userAp;

    private Subscription running = Subscriptions.empty();
    private Subscription loadingMore = Subscriptions.empty();

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
        running.unsubscribe();
        loadingMore.unsubscribe();
    }

    void loadTopics() {
        Observable<List<Topic>> topicsObservable = userAp.refreshTopic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        running = topicsObservable.subscribe(new Observer<List<Topic>>() {
            @Override
            public void onCompleted() {
                running = Subscriptions.empty();
            }

            @Override
            public void onError(Throwable e) {
                running = Subscriptions.empty();
            }

            @Override
            public void onNext(List<Topic> topics) {
                if (!hasView()) return;

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

    void loadMore() {
        if(!userAp.hasMoreTopic()) {
            return;
        }

        Observable<List<Topic>> topicsObservable = userAp.loadMore()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        loadingMore = topicsObservable.subscribe(new Observer<List<Topic>>() {
            @Override
            public void onCompleted() {
                loadingMore = Subscriptions.empty();
            }

            @Override
            public void onError(Throwable e) {
                loadingMore = Subscriptions.empty();
            }

            @Override
            public void onNext(List<Topic> topics) {
                if (!hasView()) return;

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

    void addTopic() {
        Flow.get(getView().getContext()).set(new ComposerPath(userAp));
    }

    void onOpenTopic(int position) {
        Topic topic = getView().getTopicAdapter().getTopic(position);
        Flow.get(getView().getContext()).set(new MessagePath(topic.getAp(), topic.getTopicId()));
    }
}
