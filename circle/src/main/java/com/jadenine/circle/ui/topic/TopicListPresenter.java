package com.jadenine.circle.ui.topic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.composer.ComposerPath;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.circle.ui.utils.ShareService;
import com.jadenine.circle.ui.widgets.LoadMoreViewHolder;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;
import com.jadenine.circle.utils.ToolbarColorizer;
import com.jadenine.common.mortar.ActivityOwner;

import java.util.List;

import flow.Flow;
import mortar.MortarScope;
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
public class TopicListPresenter extends ViewPresenter<TopicListView> implements RefreshableHomeView
        .RefreshableHomeListener {
    private final Circle circle;

    private final ActivityOwner activityOwner;
    private final ShareService shareService;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }
    private Subscription loadingMoreSubscription = Subscriptions.empty();{

        loadingMoreSubscription.unsubscribe();
    }
    public TopicListPresenter(Circle circle, ActivityOwner owner) {
        this.circle = circle;
        this.activityOwner = owner;
        this.shareService = new ShareService();
    }

    @Override
    protected void onEnterScope(MortarScope scope) {
        super.onEnterScope(scope);
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
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);

        getView().getToolbar().setTitle(circle.getName());
        getView().getToolbar().inflateMenu(R.menu.share);
        getView().getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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
            getView().getToolbar().getMenu().findItem(R.id.item_share_wechat).setVisible(false);
        }

        ToolbarColorizer.colorizeToolbar(getView().getToolbar(), Color.WHITE, activityOwner.getActivity());
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

        Observable<List<TimelineRange<Bomb>>> topicsObservable = circle.refresh()
                .observeOn(AndroidSchedulers.mainThread());

        refreshSubscription = topicsObservable.subscribe(new Observer<List<TimelineRange<Bomb>>>() {
            @Override
            public void onCompleted() {
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
                if(!hasView()) return;
                getView().stopRefreshing();
                circle.setHasUnread(false);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Failed to load topics.");
                refreshSubscription = Subscriptions.empty();
                refreshSubscription.unsubscribe();
                if(!hasView()) return;
                getView().stopRefreshing();
                updateBombGroups(circle.getAllTimelineRanges());
            }

            @Override
            public void onNext(List<TimelineRange<Bomb>> ranges) {
                if (!hasView()) return;

                updateBombGroups(ranges);
            }
        });
    }

    @Override
    public void onLoadMore() {
        if(!loadingMoreSubscription.isUnsubscribed() || !circle.hasMore()) {
            return;
        }
        Observable<List<TimelineRange<Bomb>>> topicsObservable = circle.loadMore()
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

    void addBomb() {
        Flow.get(getView().getContext()).set(new ComposerPath(circle.getCircleId()));
    }

    private void loadMore(TimelineRange range, final LoadMoreViewHolder loadMoreViewHolder) {
        loadMoreViewHolder.startLoading();
        circle.loadMore(range).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<TimelineRange<Bomb>>>() {

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
