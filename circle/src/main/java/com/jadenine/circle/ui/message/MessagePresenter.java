package com.jadenine.circle.ui.message;

import android.os.Bundle;

import com.jadenine.circle.entity.Message;
import com.jadenine.circle.entity.Topic;
import com.jadenine.circle.request.JSONListWrapper;
import com.jadenine.circle.request.MessageService;
import com.jadenine.circle.ui.composer.ComposerPath;

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
 * Created by linym on 6/9/15.
 */
public class MessagePresenter extends ViewPresenter<MessageListView>{
    private final Topic topic;
    private final MessageService messageService;
    private Subscription running = Subscriptions.empty();

    public MessagePresenter(MessageService messageService, final Topic topic) {
        this.messageService = messageService;
        this.topic = topic;
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (!hasView()) return;
        getView().collapsingToolbarLayout.setTitle(topic.getTopic());

        loadMessages();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        running.unsubscribe();
    }

    void loadMessages() {
        Observable<JSONListWrapper<Message>> messageObservable = messageService.listMessages
                (topic.getTopicId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread());

        running = messageObservable.subscribe(new Observer<JSONListWrapper<Message>>() {
            @Override
            public void onCompleted() {
                running = Subscriptions.empty();
            }

            @Override
            public void onError(Throwable e) {
                running = Subscriptions.empty();
            }

            @Override
            public void onNext(JSONListWrapper<Message> messageJSONListWrapper) {
                if(!hasView()) return;
                List<Message> messages = messageJSONListWrapper.getAll();
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message lhs, Message rhs) {
                        return (int) (rhs.getTimestamp() - lhs.getTimestamp());
                    }
                });
                getView().getMessageAdapter().setMessages(messages);
            }
        });
    }

    public void addMessage() {
        Flow.get(getView().getContext()).set(new ComposerPath(topic));
    }
}
