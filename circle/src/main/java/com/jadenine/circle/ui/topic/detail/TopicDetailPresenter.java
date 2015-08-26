package com.jadenine.circle.ui.topic.detail;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.chat.detail.ChatPath;
import com.jadenine.circle.ui.utils.ContentValidator;
import com.jadenine.circle.ui.utils.ShareService;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.jadenine.circle.ui.widgets.TopicHeader;
import com.jadenine.circle.utils.ToolbarColorizer;
import com.jadenine.common.mortar.ActivityOwner;
import com.raizlabs.android.dbflow.annotation.NotNull;

import java.util.Collections;
import java.util.List;

import flow.Flow;
import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by linym on 7/24/15.
 */
class TopicDetailPresenter extends ViewPresenter<TopicDetailView> {
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";
    private static final String BUNDLE_REPLY_TO = "reply_to";

    private final Account account;
    private final Circle circle;
    private final Group<Bomb> bombGroup;
    private final Bomb rootBomb;

    private String replyTo;

    private final AvatarBinder avatarBinder;

    private final ActivityOwner activityOwner;

    private final ShareService shareService;

    public TopicDetailPresenter(Account account, Circle circle, Group<Bomb> bombGroup, AvatarBinder avatarBinder, ActivityOwner owner) {
        this.account = account;
        this.circle = circle;
        this.bombGroup = bombGroup;
        this.rootBomb = bombGroup.getRoot();
        this.replyTo = rootBomb.getRootUser();
        this.avatarBinder = avatarBinder;
        this.activityOwner = owner;
        this.shareService = new ShareService();
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

        getView().getBombAdapter().setOnBombItemClick(new BombListAdapter.OnBombItemClickListener
                () {
            @Override
            public boolean onBombItemClicked(Bomb bomb) {
                setReplyTo(bomb);
                return false;
            }
        });

        updateHint();

        loadMessages();

        getView().toolbar.inflateMenu(R.menu.drawer);
        getView().toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_share_wechat:
                        shareService.shareToWeChatTimeline();
                        return true;
                    case R.id.item_share:
                        shareService.share();
                        return true;
                }
                return false;
            }
        });
        if(!shareService.start(getView().getContext())){
            getView().toolbar.getMenu().findItem(R.id.item_share_wechat).setVisible(false);
        }

        getView().toolbar.setTitle(circle.getName());
        ToolbarColorizer.colorizeToolbar(getView().toolbar, Color.WHITE, activityOwner.getActivity());
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_TYPED_CONTENT, getView().replyEditor.getText().toString());
        outState.putString(BUNDLE_REPLY_TO, replyTo);
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        shareService.stop();
    }

    void loadMessages() {
        if (hasView()) {
            List<Bomb> bombList = bombGroup.getEntities();
            Collections.reverse(bombList);
            getView().getBombAdapter().setBombs(bombGroup.getRoot(), bombList);
            bombGroup.setUnread(false);
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

        getView().replyEditor.setHint(avatarBinder.getAtAvatarSpan(context, avatarBinder
                .getAvatar(replyTo, rootBomb.getRootMessageId()), getView().replyEditor
                .getTextSize()));
    }

    public void send() {
        String content = getView().replyEditor.getText().toString();
        if (!ContentValidator.validate(getView().getContext(), content)) {
            return;
        }

        SoftKeyboardToggler.toggleInputMethod(getView().replyEditor, false);

        final Bomb bomb = new Bomb(circle.getCircleId(), account.getDeviceId());
        bomb.setContent(content);
        bomb.setTo(replyTo);
        bomb.setRootMessageId(rootBomb.getRootMessageId());
        bomb.setRootUser(rootBomb.getRootUser());

        account.publish(bomb).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Bomb>() {

            @Override
            public void onCompleted() {
                if (!hasView()) return;

                TopicDetailPresenter.this.replyTo = bomb.getRootUser();
                updateHint();
                getView().replyEditor.setText("");

                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();

                Action1 afterRefreshAction = new Action1<List<TimelineRange<Bomb>>>() {

                    @Override
                    public void call(List<TimelineRange<Bomb>> ranges) {
                        loadMessages();
                    }
                };

                account.refreshTop().observeOn(AndroidSchedulers.mainThread()).subscribe(afterRefreshAction);
                account.refreshMyTopics().observeOn(AndroidSchedulers.mainThread()).subscribe(afterRefreshAction);
                circle.refresh().observeOn(AndroidSchedulers.mainThread()).subscribe(afterRefreshAction);
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

