package com.jadenine.circle.ui.message;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Message;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.jadenine.circle.utils.Device;

import mortar.ViewPresenter;
import rx.Observer;
import rx.android.internal.Preconditions;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by linym on 6/9/15.
 */
public class MessagePresenter extends ViewPresenter<MessageListView>{
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";
    private static final String BUNDLE_PRIVATE = "is_private";
    private static final String BUNDLE_REPLYTO = "reply_to";

    private final Topic topic;

    private String replyTo;

    public MessagePresenter(final Topic topic) {
        Preconditions.checkNotNull(topic, "Null topic");
        this.topic = topic;
        replyTo = topic.getUser();
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (!hasView()) return;
        getView().collapsingToolbarLayout.setTitle(topic.getTopic());
        if(null != savedInstanceState) {
            boolean isPrivate = savedInstanceState.getBoolean(BUNDLE_PRIVATE);
            replyTo = savedInstanceState.getString(BUNDLE_REPLYTO);
            String content = savedInstanceState.getString(BUNDLE_TYPED_CONTENT, "");

            getView().privateCheckBox.setChecked(isPrivate);
            getView().replyEditor.setText(content);
            getView().replyEditor.setSelection(content.length());
        }

        updateHint();

        loadMessages();
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_TYPED_CONTENT, getView().replyEditor.getText().toString());
        outState.putBoolean(BUNDLE_PRIVATE, getView().privateCheckBox.isChecked());
        outState.putString(BUNDLE_REPLYTO, replyTo);
    }

    void loadMessages() {
        if(hasView()) {
            getView().getMessageAdapter().setMessages(topic.getMessages());
        }
    }

    Topic getTopic() {
        return topic;
    }

    public void send() {
        boolean isPrivate  = getView().privateCheckBox.isChecked();
        String replyTo;
        if(isPrivate) {
            replyTo = topic.getUser();
        } else {
            replyTo = topic.getUser();
        }

        String content = getView().replyEditor.getText().toString();

        if(TextUtils.isEmpty(content)){
            Toast.makeText(getView().getContext(), R.string.message_invalid_empty, Toast
                    .LENGTH_SHORT).show();
            return;
        }
        SoftKeyboardToggler.toggleInputMethod(getView().replyEditor, false);
        Message message = new Message(topic.getAp(), topic.getTopicId());

        message.setUser(Device.getDeviceId(getView().getContext()));
        message.setContent(content);
        message.setIsPrivate(isPrivate, replyTo);

        message.reply(topic).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Message>() {

            @Override
            public void onCompleted() {
                if (!hasView()) return;

                getView().privateCheckBox.setChecked(false);
                MessagePresenter.this.replyTo = MessagePresenter.this.topic.getUser();
                updateHint();
                getView().replyEditor.setText("");

                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                loadMessages();
            }

            @Override
            public void onError(Throwable e) {
                if (!hasView()) return;
                Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
            }

            @Override
            public void onNext(Message message) {

            }
        });
    }

    void setReplyTo(int position) {
        Message message = getView().getMessageAdapter().getMessage(position);
        replyTo = message.getUser();
        if(hasView()) {
            updateHint();
            SoftKeyboardToggler.toggleInputMethod(getView().replyEditor, true);
        }
    }

    void onPrivateCheckedChanged(boolean isChecked) {
        if(!isChecked) {
            replyTo = getTopic().getUser();
        }
        updateHint();
    }

    private void updateHint() {
        Context context = getView().getContext();

        boolean isPrivate = getView().privateCheckBox.isChecked();
        int replyTypeStringId = isPrivate ? R.string.reply_type_private : R.string
                .reply_type_public;

        String replyType = context.getString(replyTypeStringId);

        boolean isOwner = topic.getUser().equals(replyTo);

        getView().replyEditor.setHint(context.getString(R.string.reply_hint,
                replyType, isOwner? context.getString(R.string.topic_owner) : replyTo));
    }
}
