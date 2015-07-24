package com.jadenine.circle.ui.menu;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.ui.home.BombListPath;
import com.jadenine.circle.utils.ApUtils;
import com.jadenine.circle.utils.Device;
import com.squareup.otto.Subscribe;
import com.umeng.message.PushAgent;

import java.util.List;

import flow.Flow;
import flow.History;
import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by linym on 7/22/15.
 */
public class ApMenuPresenter extends ViewPresenter<ApMenuView>{
    private final Account account;
    private UserAp currentAp;

    public ApMenuPresenter(Account account) {
        this.account = account;
    }

    @Override
    protected void onEnterScope(MortarScope scope) {
        super.onEnterScope(scope);
        BusProvider.register(this);
    }

    @Override
    protected void onExitScope() {
        BusProvider.unregister(this);
        super.onExitScope();
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (!hasView()) return;
        loadAPList();
    }

    public void onApSelected(int position) {
        UserAp userAp = getAdapter().getAp(position);
        onApSelected(userAp);
    }

    private void onApSelected(@NonNull UserAp userAp) {
        getView().drawerLayout.closeDrawer(GravityCompat.START);

//        Path top = Path.get(getContext());
//        if(top instanceof BombListPath) {
//            if(((BombListPath) top).getAp().equals(userAp.getAP())){
//                return;
//            }
//        }

        History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.push(new BombListPath(userAp.getAP()));

        Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
        currentAp = userAp;
    }

    @Subscribe
    public void onApConnected(EventProducer.APConnectedEvent event) {
        if(!hasView()) return;
        addAPIfNot(event.getAP());
    }

    private Context getContext() {
        return getView().getContext();
    }

    private ApMenuAdapter getAdapter(){
        return getView().getAdapter();
    }

    private boolean alreadyAdded(ApUtils.AP currentAp) {
        boolean currentAPAlreadyAdded = false;
        List<UserAp> userApList = account.getUserAps();
        for (UserAp userAp : userApList) {
            if (currentAp.getBSSID().equals(userAp.getAP()) && userAp.getSSID().equals(currentAp.getSSID())) {
                currentAPAlreadyAdded = true;
                break;
            }
        }
        return currentAPAlreadyAdded;
    }

    private void addAPIfNot(ApUtils.AP ap) {
        if (null == ap || alreadyAdded(ap)) {
            return;
        }

        UserAp userAp = UserAp.build(new UserApEntity(Device.getDeviceId(getContext()), ap
                .getBSSID(), ap.getSSID()));

        userAp.connect(account).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<UserAp>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<UserAp> userAps) {
                if (!hasView()) return;
                getAdapter().setUserAps(userAps);

                addTag(userAps);
            }
        });
    }

    void loadAPList() {
        account.listAPs().observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List
                <UserAp>>() {

            @Override
            public void onCompleted() {
                openDefaultApIfNeed();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Failed to load aps.");
                openDefaultApIfNeed();
            }

            @Override
            public void onNext(List<UserAp> userApList) {
                if (!hasView()) return;
                getAdapter().setUserAps(userApList);

                addTag(userApList);

                ApUtils.AP ap = ApUtils.getConnectedAP(getContext());
                addAPIfNot(ap);
            }

            private void openDefaultApIfNeed() {
                if(!hasView()) return;
                if (null == currentAp) {
                    onApSelected(account.getDefaultAp());
                }
            }
        });
    }

    private void addTag(final List<UserAp> userAps) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (UserAp ap : userAps) {
                    addTag(ap.getAP());
                }
            }
        });
    }

    private void addTag(String ap) {
        if(!hasView()) {
            return;
        }
        try {
            PushAgent.getInstance(getContext()).getTagManager().add(ap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
