package com.jadenine.circle.ui.home;

import android.os.Bundle;

import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.utils.SectionedRecyclerViewAdapter;
import com.jadenine.circle.ui.composer.BombComposerPath;

import java.util.ArrayList;
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
class BombListPresenter extends ViewPresenter<BombListView>{
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
                if(!hasView()) return;
                getView().swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Failed to load topics.");
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
                if(!hasView()) return;
                getView().swipeRefreshLayout.setRefreshing(false);
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

    private void updateBombGroups(List<TimelineRange<Bomb>> ranges) {
        List<Group<Bomb>> bombGroupList = new LinkedList<>();
        List<SectionedRecyclerViewAdapter.Section<TimelineRange<Bomb>>> sections = new
                ArrayList<>(ranges.size());
        int offset = 0;
        int sectionOffset = 0;
        for(TimelineRange<Bomb> range: ranges) {
            SectionedRecyclerViewAdapter.Section<TimelineRange<Bomb>> section = new
                    SectionedRecyclerViewAdapter.Section<>(offset, range.getGroupCount(), range
                    .hasMore(), range);

            sections.add(sectionOffset++, section);

            offset += range.getGroupCount();

            bombGroupList.addAll(range.getAllGroups());
        }

        getView().getAdapter().setSections(sections, bombGroupList);
    }

    void loadMore() {
        if(!loadingMoreSubscription.isUnsubscribed() || !userAp.hasMore()) {
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

    void addBomb() {
        Flow.get(getView().getContext()).set(new BombComposerPath(userAp.getAP()));
    }

    public void loadMore(TimelineRange range, final LoadMoreViewHolder loadMoreViewHolder) {
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
}
