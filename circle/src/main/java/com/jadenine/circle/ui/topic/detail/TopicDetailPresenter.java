package com.jadenine.circle.ui.topic.detail;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.chat.detail.ChatPath;
import com.jadenine.circle.ui.utils.ContentValidator;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.jadenine.circle.ui.widgets.TopicHeader;
import com.raizlabs.android.dbflow.annotation.NotNull;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by linym on 7/24/15.
 */
class TopicDetailPresenter extends ViewPresenter<TopicDetailView> {
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";
    private static final String BUNDLE_REPLY_TO = "reply_to";

    private final UserAp userAp;
    private final Group<Bomb> bombGroup;
    private final Bomb rootBomb;

    private String replyTo;

    private final AvatarBinder avatarBinder;

    @DaggerScope(TopicDetailPresenter.class)
    @Inject
    public TopicDetailPresenter(UserAp userAp, Group<Bomb> bombGroup, AvatarBinder avatarBinder) {
        this.userAp = userAp;
        this.bombGroup = bombGroup;
        this.rootBomb = bombGroup.getRoot();
        this.replyTo = rootBomb.getRootUser();
        this.avatarBinder = avatarBinder;
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (!hasView()) return;

        if (null != savedInstanceState) {
            replyTo = savedInstanceState.getString(BUNDLE_REPLY_TO);
            String content = savedInstanceState.getString(BUNDLE_TYPED_CONTENT, "");

            getView().replyEditor.setText(content);
            getView().replyEditor.setSelection(content.length());
        }

        getView().toolbar.setTitle(userAp.getSSID());
        updateHint();

        loadMessages();
        getView().getBombAdapter().setOnAvatarClickListener(new TopicHeader.OnAvatarClickListener
                () {
            @Override
            public void onClick() {
                ChatPath chatPath = new ChatPath(userAp.getAP(), bombGroup.getGroupId(), userAp
                        .getUser(), bombGroup.getRoot().getFrom());
                Flow.get(getView()).set(chatPath);
            }
        });
        getView().getBombAdapter().setOnBombItemClick(new BombListAdapter.OnBombItemClickListener() {
            @Override
            public boolean onBombItemClicked(Bomb bomb) {
                setReplyTo(bomb);
                return false;
            }
        });
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_TYPED_CONTENT, getView().replyEditor.getText().toString());
        outState.putString(BUNDLE_REPLY_TO, replyTo);
    }

    void loadMessages() {
        if (hasView()) {
            List<Bomb> bombList = bombGroup.getEntities();
            Collections.reverse(bombList);
            getView().getBombAdapter().setBombs(bombGroup.getRoot(), bombList);
        }
    }

    public void setReplyTo(@NotNull Bomb bomb) {
        replyTo = bomb.getFrom();

        if (hasView()) {
            updateHint();
            SoftKeyboardToggler.toggleInputMethod(getView().replyEditor, true);
        }
    }

    private void updateHint() {
        Context context = getView().getContext();

        getView().replyEditor.setHint(avatarBinder.getAtAvatarSpan(context, avatarBinder.getAvatar
                (replyTo, rootBomb.getRootMessageId()), getView().replyEditor.getTextSize()));
    }

    public void send() {
        String content = getView().replyEditor.getText().toString();
        if (!ContentValidator.validate(getView().getContext(), content)) {
            return;
        }

        SoftKeyboardToggler.toggleInputMethod(getView().replyEditor, false);

        final Bomb bomb = new Bomb(userAp.getAP(), userAp.getUser());
        bomb.setContent(content);
        bomb.setTo(replyTo);
        bomb.setRootMessageId(rootBomb.getRootMessageId());
        bomb.setRootUser(rootBomb.getRootUser());

        userAp.publish(bomb).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Bomb>() {

            @Override
            public void onCompleted() {
                if (!hasView()) return;

                TopicDetailPresenter.this.replyTo = bomb.getRootUser();
                updateHint();
                getView().replyEditor.setText("");

                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                userAp.refresh().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<TimelineRange<Bomb>>>() {

                    @Override
                    public void call(List<TimelineRange<Bomb>> ranges) {
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
            public void onNext(Bomb bomb1) {

            }
        });
    }

}
