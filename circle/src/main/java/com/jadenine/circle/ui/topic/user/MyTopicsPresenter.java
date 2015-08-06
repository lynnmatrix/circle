package com.jadenine.circle.ui.topic.user;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.LoadMoreViewHolder;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;
import com.jadenine.circle.utils.ToolbarColorizer;
import com.jadenine.common.mortar.ActivityOwner;

import java.util.List;

import mortar.ViewPresenter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by linym on 8/6/15.
 */
class MyTopicsPresenter  extends ViewPresenter<MyTopicView> implements RefreshableHomeView.RefreshableHomeListener{
    private final Account account;
    private final ActivityOwner activityOwner;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }
    private Subscription loadingMoreSubscription = Subscriptions.empty();{
        loadingMoreSubscription.unsubscribe();
    }

    public MyTopicsPresenter(Account account, ActivityOwner owner) {
        this.account = account;
        this.activityOwner = owner;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        getView().getAdapter().setOnFooterClickListener(new SectionedLoadMoreRecyclerAdapter
                .OnFooterClickListener() {

            @Override
            public void onFooterClicked(TimelineRange range, LoadMoreViewHolder
                    loadMoreViewHolder) {
                loadMore(range, loadMoreViewHolder);
            }
        });

        getView().getToolbar().setTitle(R.string.title_my_topic);

        ToolbarColorizer.colorizeToolbar(getView().getToolbar(), Color.WHITE, activityOwner.getActivity());

        onRefresh();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        refreshSubscription.unsubscribe();
        loadingMoreSubscription.unsubscribe();
    }

    //<editor-fold desc="load data">
    @Override
    public void onRefresh() {
        Observable<List<TimelineRange<Bomb>>> topicsObservable = account.refreshMyTopics()
                .observeOn(AndroidSchedulers.mainThread());

        refreshSubscription = topicsObservable.subscribe(new Observer<List<TimelineRange<Bomb>>>() {
            @Override
            public void onCompleted() {
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
                if(!hasView()) return;
                getView().stopRefreshing();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Failed to load topics.");
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
                if(!hasView()) return;
                getView().stopRefreshing();
                updateBombGroups(account.getAllMyTopics());
            }

            @Override
            public void onNext(List<TimelineRange<Bomb>> ranges) {
                Timber.i("hasView():%b", hasView());
                if (!hasView()) return;

                updateBombGroups(ranges);
            }
        });
    }

    @Override
    public void onLoadMore() {
        if(!loadingMoreSubscription.isUnsubscribed() || !account.hasMoreMyTopic()) {
            return;
        }
        Observable<List<TimelineRange<Bomb>>> topicsObservable = account.loadMoreMyTopics()
                .observeOn(AndroidSchedulers.mainThread());

        loadingMoreSubscription = topicsObservable.subscribe(createAutoLoadMoreObserver());
    }

    private void loadMore(TimelineRange range, final LoadMoreViewHolder loadMoreViewHolder) {
        loadMoreViewHolder.startLoading();
        account.loadMoreMyTopics(range).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createLoadMoreObserver(loadMoreViewHolder));
    }


    //<editor-fold desc="commons to load topics">
    @NonNull
    private Observer<List<TimelineRange<Bomb>>> createAutoLoadMoreObserver() {
        return new Observer<List<TimelineRange<Bomb>>>() {
            @Override
            public void onCompleted() {
                loadingMoreSubscription = Subscriptions.empty();
                loadingMoreSubscription.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Timber.w(e, "Failed to load more topics.");
                loadingMoreSubscription = Subscriptions.empty();
                loadingMoreSubscription.unsubscribe();
            }

            @Override
            public void onNext(List<TimelineRange<Bomb>> ranges) {
                if (!hasView()) return;

                updateBombGroups(ranges);
            }
        };
    }

    @NonNull
    private Observer<List<TimelineRange<Bomb>>> createLoadMoreObserver(final LoadMoreViewHolder loadMoreViewHolder) {
        return new Observer<List<TimelineRange<Bomb>>>() {

            @Override
            public void onCompleted() {
                loadMoreViewHolder.endLoading();
            }

            @Override
            public void onError(Throwable e) {
                loadMoreViewHolder.setError();
            }

            @Override
            public void onNext(List<TimelineRange<Bomb>> ranges) {
                updateBombGroups(ranges);
            }
        };
    }

    private void updateBombGroups(List<TimelineRange<Bomb>> ranges) {
        getView().getAdapter().setSections(ranges);
    }
    //</editor-fold>
    //</editor-fold>

}
