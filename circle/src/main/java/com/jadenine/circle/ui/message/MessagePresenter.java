package com.jadenine.circle.ui.message;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.Message;
import com.jadenine.circle.entity.Topic;
import com.jadenine.circle.request.JSONListWrapper;
import com.jadenine.circle.request.MessageService;
import com.jadenine.circle.utils.Device;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mortar.ViewPresenter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";

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
        if(null != savedInstanceState) {
            String content = savedInstanceState.getString(BUNDLE_TYPED_CONTENT, "");
            getView().replyEditor.setText(content);
            getView().replyEditor.setSelection(content.length());
        }

        loadMessages();
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_TYPED_CONTENT, getView().replyEditor.getText().toString());
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

    Topic getTopic() {
        return topic;
    }

    /**
     * 隐藏键盘和去掉焦点
     */
    private void hideInputMethod() {
        InputMethodManager inputManager = (InputMethodManager)getView().getContext()
                .getSystemService
                (Context
                .INPUT_METHOD_SERVICE);
        View focusView= getView().replyEditor;
        if (inputManager.isActive() && focusView != null) {
            inputManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            focusView.clearFocus();
        }
    }

    public void send() {
        String content = getView().replyEditor.getText().toString();

        if(TextUtils.isEmpty(content)){
            Toast.makeText(getView().getContext(), R.string.message_invalid_empty, Toast
                    .LENGTH_SHORT).show();
            return;
        }
        hideInputMethod();
        Message message = new Message();
        if(null != topic) {
            message.setTopicId(topic.getTopicId());
        }
        message.setUser(Device.getDeviceId(getView().getContext()));
        message.setContent(content);

        messageService.addMessage(topic.getAp(), message, new Callback<Message>
                () {
            @Override
            public void success(Message message, Response response) {
                if(!hasView()) return;
                getView().replyEditor.setText("");
                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                loadMessages();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
            }
        });
    }
}
