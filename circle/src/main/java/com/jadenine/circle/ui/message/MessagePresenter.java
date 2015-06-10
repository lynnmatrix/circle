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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by linym on 6/9/15.
 */
public class MessagePresenter extends ViewPresenter<MessageListView>{
    private final Topic topic;
    private final MessageService messageService;

    public MessagePresenter(MessageService messageService, final Topic topic) {
        this.messageService = messageService;
        this.topic = topic;
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (!hasView()) return;
        loadMessages();
    }

    void loadMessages() {
        messageService.listMessages(topic.getTopicId(), new Callback<JSONListWrapper<Message>>() {

            @Override
            public void success(JSONListWrapper<Message> messageJSONListWrapper, Response
                    response) {
                List<Message> messages = messageJSONListWrapper.getAll();
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message lhs, Message rhs) {
                        return (int) (rhs.getTimestamp() - lhs.getTimestamp());
                    }
                });
                getView().getMessageAdapter().setMessages(messages);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void addMessage() {
        Flow.get(getView().getContext()).set(new ComposerPath(topic));
    }
}
