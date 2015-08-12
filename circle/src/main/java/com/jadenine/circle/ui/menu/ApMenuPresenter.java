package com.jadenine.circle.ui.menu;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.ui.topic.TopicListPath;
import com.jadenine.circle.ui.topic.top.TopPath;
import com.jadenine.circle.utils.ApUtils;
import com.jadenine.circle.utils.Device;
import com.squareup.otto.Subscribe;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

import flow.Flow;
import flow.History;
import flow.path.Path;
import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by linym on 7/22/15.
 */
public class ApMenuPresenter extends ViewPresenter<ApMenuView>{
    private static final String BUNDLE_CURRENT_AP = "current_ap";
    private final Account account;
    private String currentAp;

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
        if(null != savedInstanceState) {
            currentAp = savedInstanceState.getString(BUNDLE_CURRENT_AP);
        }
        if (!hasView()) return;
        loadAPList();
        openDefaultItem();
    }

    private void openDefaultItem() {
        replaceWithPath(new TopPath());
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_CURRENT_AP, currentAp);
    }

    public boolean onApSelected(int position) {
        boolean validApPosition = position >= ApMenuAdapter.NON_AP_ITEM_COUNT;
        if(validApPosition) {
            UserAp userAp = getAdapter().getAp(position);
            getAdapter().setSelected(position);
            onApSelected(userAp);
        }
        return validApPosition;
    }

    private void onApSelected(@NonNull UserAp userAp) {
        if(null == userAp) {
            return;
        }
        replaceWithPath(new TopicListPath(userAp.getAP()));
        currentAp = userAp.getAP();
    }

    private void replaceWithPath(@NonNull Path path) {
        History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.push(path);

        Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
    }
    @Subscribe
    public void onApConnected(EventProducer.APConnectedEvent event) {
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
            }

            @Override
            public void onError(Throwable e) {
                Timber.w(e, "Failed to load aps.");
                getAdapter().setUserAps(account.getUserAps());
            }

            @Override
            public void onNext(List<UserAp> userApList) {
                if (!hasView()) return;
                getAdapter().setUserAps(userApList);

                addTag(userApList);

                ApUtils.AP ap = ApUtils.getConnectedAP(getContext());
                addAPIfNot(ap);
            }
        });
    }

    private void addTag(final List<UserAp> userAps) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<UserAp> tmpUserAps = new ArrayList<>(userAps);
                for (UserAp ap : tmpUserAps) {
                    addTag(ap.getAP());
                }
                addTag(account.getDeviceId());
            }
        });
    }

    private void addTag(String tag) {
        if(!hasView()) {
            return;
        }
        try {
            PushAgent.getInstance(getContext()).getTagManager().add(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
