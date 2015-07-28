package com.jadenine.circle.ui.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.ui.utils.ContentValidater;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.raizlabs.android.dbflow.annotation.NotNull;

import java.util.Collections;
import java.util.List;

import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by linym on 7/25/15.
 */
public class ChatPresenter extends ViewPresenter<ChatView> {
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";

    private String ap;
    private String topicId;
    private String from, to;

    private
    @Nullable
    Group<DirectMessageEntity> chatGroup;
    private Account account;

    public ChatPresenter(Account account, String ap, @NotNull String topicId, @NotNull String
            from, @NotNull String to, @Nullable Group<DirectMessageEntity> chatGroup) {
        this.account = account;
        this.ap = ap;
        this.topicId = topicId;
        this.from = from;
        this.to = to;
        this.chatGroup = chatGroup;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (!hasView()) return;

        if (null != savedInstanceState) {
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

    void loadMessages() {
        if (hasView()) {
            List<DirectMessageEntity> chatMessages = Collections.emptyList();
            if (null != chatGroup) {
                chatMessages = chatGroup.getEntities();
                Collections.reverse(chatMessages);
            }
            getView().getChatAdapter().setChatMessages(chatMessages);
        }
    }

    public void send() {
        String content = getView().replyEditor.getText().toString();
        if (!ContentValidater.validate(getView().getContext(), content)) {
            return;
        }

        SoftKeyboardToggler.toggleInputMethod(getView().replyEditor, false);

        final DirectMessageEntity chatMessage = new DirectMessageEntity(ap, topicId, from, to);

        chatMessage.setContent(content);
        if (null != chatGroup) {
            List<DirectMessageEntity> messages = chatGroup.getEntities();
            chatMessage.setRootMessageId(String.valueOf(chatGroup.getGroupId()));
            if (messages.size() > 0) {
                DirectMessageEntity anyMessage = chatGroup.getEntities().get(0);
                chatMessage.setRootUser(anyMessage.getRootUser());
            }
        }
        account.publish(chatMessage).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<DirectMessageEntity>() {
            @Override
            public void onCompleted() {
                if (!hasView()) return;
                getView().replyEditor.setText("");

                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                account.refreshChats().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<TimelineRange<DirectMessageEntity>>>() {

                    @Override
                    public void call(List<TimelineRange<DirectMessageEntity>> timelineRanges) {

                        loadMessages();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                if (!hasView()) return;
                Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
            }

            @Override
            public void onNext(DirectMessageEntity chatMessage) {

            }
        });
    }
}
