package com.jadenine.circle.ui.home;

import android.os.Bundle;

import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.composer.BombComposerPath;
import com.jadenine.circle.ui.detail.BombGroupPath;

import java.util.LinkedList;
import java.util.List;

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
public class BombListPresenter extends ViewPresenter<BombListView>{
    private final UserAp userAp;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }
    private Subscription loadingMoreSubscription = Subscriptions.empty();{
        loadingMoreSubscription.unsubscribe();
    }

    public BombListPresenter(UserAp userAp) {
        this.userAp = userAp;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(!hasView()) return;

        getView().toolbar.setTitle(userAp.getSSID());

        refresh();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        refreshSubscription.unsubscribe();
        loadingMoreSubscription.unsubscribe();
    }

    void refresh() {
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
                getView().swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Failed to load topics.");
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
                getView().swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(List<TimelineRange<Bomb>> ranges) {
                Timber.i("hasView():%b", hasView());
                if (!hasView()) return;

                updateBombGroups(ranges);
            }
        });
    }

    private void updateBombGroups(List<TimelineRange<Bomb>> ranges) {
        List<Group<Bomb>> bombGroupList = new LinkedList<>();
        for(TimelineRange<Bomb> range: ranges) {
            bombGroupList.addAll(range.getAllGroups());
        }

        getView().getAdapter().setBombGroups(bombGroupList);
    }

    void loadMore() {
        if(!loadingMoreSubscription.isUnsubscribed()) {
            return;
        }
        Observable<List<TimelineRange<Bomb>>> topicsObservable = userAp.loadMoreBomb()
                .observeOn(AndroidSchedulers.mainThread());

        topicsObservable.subscribe(new Observer<List<TimelineRange<Bomb>>>() {
            @Override
            public void onCompleted() {
                loadingMoreSubscription = Subscriptions.empty();
                loadingMoreSubscription.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Failed to load more topics.");
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

    void onDetail(int position) {
        Group<Bomb> topic= getView().getAdapter().getBombGroup(position);
        Flow.get(getView().getContext()).set(new BombGroupPath(userAp.getAP(), topic.getGroupId()));
    }

    void addBomb() {
        Flow.get(getView().getContext()).set(new BombComposerPath(userAp.getAP()));
    }
}
