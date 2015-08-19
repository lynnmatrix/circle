package com.jadenine.circle.ui.topic.top;

import android.graphics.Color;
import android.os.Bundle;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.widgets.RefreshableHomeView;
import com.jadenine.circle.utils.ToolbarColorizer;
import com.jadenine.common.mortar.ActivityOwner;

import java.util.ArrayList;
import java.util.List;

import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by linym on 8/11/15.
 */
class TopPresenter extends ViewPresenter<TopView> implements RefreshableHomeView.RefreshableHomeListener{
    private final Account account;
    private final ActivityOwner activityOwner;

    private Subscription refreshSubscription = Subscriptions.empty();{
        refreshSubscription.unsubscribe();
    }

    public TopPresenter(Account account, ActivityOwner owner) {
        this.account = account;
        this.activityOwner = owner;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        getView().getToolbar().setTitle(R.string.title_top_topics);

        ToolbarColorizer.colorizeToolbar(getView().getToolbar(), Color.WHITE, activityOwner.getActivity());

        onRefresh();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        refreshSubscription.unsubscribe();
    }

    @Override
    public void onRefresh() {
        refreshSubscription = account.refreshTop().observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<Group<Bomb>>>() {
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
                updateBombGroups(account.getTops());
            }

            @Override
            public void onNext(ArrayList<Group<Bomb>> groups) {
                if (!hasView()) return;

                updateBombGroups(groups);
            }
        });
    }

    private void updateBombGroups(List<Group<Bomb>> topics) {
        getView().getAdapter().setBombGroups(topics);
    }

    @Override
    public void onLoadMore() {
        //DO NOTHING
    }
}
