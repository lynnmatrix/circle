package com.jadenine.circle.ui.topic;

import android.os.Bundle;

import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.composer.ComposerPath;
import com.jadenine.circle.ui.widgets.LoadMoreViewHolder;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import mortar.ViewPresenter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by linym on 7/22/15.
 */
class TopicListPresenter extends ViewPresenter<TopicListView> implements RefreshableHomeView
        .RefreshableHomeListener {
    private final UserAp userAp;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }
    private Subscription loadingMoreSubscription = Subscriptions.empty();{
        loadingMoreSubscription.unsubscribe();
    }

    @Inject
    public TopicListPresenter(UserAp userAp) {
        this.userAp = userAp;
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

        getView().getToolbar().setTitle(userAp.getSSID());

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
        if(!refreshSubscription.isUnsubscribed()){
            return;
        }

        Observable<List<TimelineRange<Bomb>>> topicsObservable = userAp.refresh()
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
                updateBombGroups(userAp.getAllTimelineRanges());
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
        if(!loadingMoreSubscription.isUnsubscribed() || !userAp.hasMore()) {
            return;
        }
        Observable<List<TimelineRange<Bomb>>> topicsObservable = userAp.loadMoreBomb()
                .observeOn(AndroidSchedulers.mainThread());

        loadingMoreSubscription = topicsObservable.subscribe(new Observer<List<TimelineRange<Bomb>>>() {
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
        });
    }

    @Override
    public boolean onRowClick(int position) {
        return false;
    }

    void addBomb() {
        Flow.get(getView().getContext()).set(new ComposerPath(userAp.getAP()));
    }

    private void loadMore(TimelineRange range, final LoadMoreViewHolder loadMoreViewHolder) {
        loadMoreViewHolder.startLoading();
        userAp.loadMoreBomb(range).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<TimelineRange<Bomb>>>() {

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
        });
    }

    private void updateBombGroups(List<TimelineRange<Bomb>> ranges) {
        getView().getAdapter().setSections(ranges);
    }
}
