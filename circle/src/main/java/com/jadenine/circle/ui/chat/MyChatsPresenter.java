package com.jadenine.circle.ui.chat;

import android.os.Bundle;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import mortar.ViewPresenter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by linym on 7/27/15.
 */
public class MyChatsPresenter extends ViewPresenter<MyChatsView> implements RefreshableHomeView
        .RefreshableHomeListener {
    private final Account account;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }
    private Subscription loadingMoreSubscription = Subscriptions.empty();{
        loadingMoreSubscription.unsubscribe();
    }

    @Inject @DaggerScope(MyChatPath.class)
    public MyChatsPresenter(Account account) {
        this.account = account;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        onRefresh();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        refreshSubscription.unsubscribe();
        loadingMoreSubscription.unsubscribe();
    }

    @Override
    public void onRefresh() {
        if(!refreshSubscription.isUnsubscribed()) {
            return;
        }

        account.refreshChats().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<TimelineRange<DirectMessageEntity>>>() {
                    @Override
                    public void onCompleted() {
                        refreshSubscription = Subscriptions.empty();
                        refreshSubscription.unsubscribe();
                        if(!hasView()) {
                            return;
                        }
                        getView().stopRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Fail to refresh my private chats.");
                        refreshSubscription = Subscriptions.empty();
                        refreshSubscription.unsubscribe();
                        if(!hasView()) {
                            return;
                        }
                        getView().stopRefreshing();
                        updateChats(account.getAllChats());
                    }

                    @Override
                    public void onNext(List<TimelineRange<DirectMessageEntity>> timelineRanges) {
                        if(!hasView()) {
                            return;
                        }
                        updateChats(timelineRanges);
                    }
                });
    }

    private void updateChats(List<TimelineRange<DirectMessageEntity>> timelineRanges) {
        List<Group<DirectMessageEntity>> chatGroupList = new LinkedList<>();
        for(TimelineRange<DirectMessageEntity> range : timelineRanges) {
            chatGroupList.addAll(range.getAllGroups());
        }
        getView().getAdapter().setChatGroups(chatGroupList);
    }

    @Override
    public void onLoadMore() {
        if(!loadingMoreSubscription.isUnsubscribed()) {
            return;
        }

        account.loadMoreChat().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<TimelineRange<DirectMessageEntity>>>() {
                    @Override
                    public void onCompleted() {
                        loadingMoreSubscription = Subscriptions.empty();
                        loadingMoreSubscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Fail to load more my private chats.");
                        loadingMoreSubscription = Subscriptions.empty();
                        loadingMoreSubscription.unsubscribe();
                    }

                    @Override
                    public void onNext(List<TimelineRange<DirectMessageEntity>> timelineRanges) {
                        if(!hasView()) {
                            return;
                        }
                        updateChats(timelineRanges);
                    }
                });
    }

    @Override
    public boolean onRowClick(int position) {
        Group<DirectMessageEntity> chat = getView().getAdapter().getChat(position);
        DirectMessageEntity lastMessage = chat.getLatest();
        ChatPath chatPath = new ChatPath(lastMessage.getAp(), Long.valueOf(lastMessage.getTopicId
                ()), lastMessage.getRootUser(), lastMessage.getRootUser(), Long.valueOf
                (lastMessage.getRootMessageId()));
        Flow.get(getView()).set(chatPath);
        return true;
    }
}
