package com.jadenine.circle.ui.chat;

import android.graphics.Color;
import android.os.Bundle;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.LoadMoreViewHolder;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;
import com.jadenine.circle.utils.ToolbarColorizer;
import com.jadenine.common.mortar.ActivityOwner;

import java.util.List;

import mortar.ViewPresenter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by linym on 7/27/15.
 */
class MyChatsPresenter extends ViewPresenter<MyChatsView> implements RefreshableHomeView
        .RefreshableHomeListener {
    private final Account account;
    private final ActivityOwner activityOwner;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }
    private Subscription loadingMoreSubscription = Subscriptions.empty();{
        loadingMoreSubscription.unsubscribe();
    }

    public MyChatsPresenter(Account account, ActivityOwner owner) {
        this.account = account;
        this.activityOwner = owner;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        ToolbarColorizer.colorizeToolbar(getView().getToolbar(), Color.WHITE, activityOwner.getActivity());
        getView().getAdapter().setOnFooterClickListener(new SectionedLoadMoreRecyclerAdapter
                .OnFooterClickListener() {

            @Override
            public void onFooterClicked(TimelineRange range, LoadMoreViewHolder
                    loadMoreViewHolder) {
                loadMore(range, loadMoreViewHolder);
            }
        });

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

        refreshSubscription = account.refreshChats().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<TimelineRange<DirectMessageEntity>>>() {
                    @Override
                    public void onCompleted() {
                        refreshSubscription = Subscriptions.empty();
                        refreshSubscription.unsubscribe();
                        if(!hasView()) return;
                        getView().stopRefreshing();
                        account.setHasUnreadChat(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Fail to refresh my private chats.");
                        refreshSubscription = Subscriptions.empty();
                        refreshSubscription.unsubscribe();
                        if(!hasView()) return;
                        getView().stopRefreshing();
                        updateChats(account.getAllChats());
                    }

                    @Override
                    public void onNext(List<TimelineRange<DirectMessageEntity>> timelineRanges) {
                        if (!hasView()) return;
                        updateChats(timelineRanges);
                    }
                });
    }

    @Override
    public void onLoadMore() {
        if(!loadingMoreSubscription.isUnsubscribed() || account.hasMoreChat()) {
            return;
        }

        loadingMoreSubscription = account.loadMoreChat().observeOn(AndroidSchedulers.mainThread())
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
                        if (!hasView()) return;

                        updateChats(timelineRanges);
                    }
                });
    }

    @Override
    public boolean onRowClick(int position) {
        return false;
    }

    private void loadMore(TimelineRange range, final LoadMoreViewHolder loadMoreViewHolder) {
        loadMoreViewHolder.startLoading();
        account.loadMoreChat(range).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<TimelineRange<DirectMessageEntity>>>() {

            @Override
            public void onCompleted() {
                loadMoreViewHolder.endLoading();
            }

            @Override
            public void onError(Throwable e) {
                loadMoreViewHolder.setError();
            }

            @Override
            public void onNext(List<TimelineRange<DirectMessageEntity>> ranges) {
                updateChats(ranges);
            }
        });
    }

    private void updateChats(List<TimelineRange<DirectMessageEntity>> ranges) {
        getView().getAdapter().setSections(ranges);
    }
}
