package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.ui.topic.TopicPath;
import com.jadenine.circle.utils.ApUtils;
import com.jadenine.circle.utils.Device;
import com.squareup.otto.Subscribe;
import com.umeng.message.PushAgent;

import java.util.List;

import flow.Flow;
import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by linym on 6/9/15.
 */
public class ApListPresenter extends ViewPresenter<ApListView> {

    private final Account account;
    ApListPresenter(Account account) {
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
        UserAp userAp = getAdapter().getItem(position);
        Flow.get(getView()).set(new TopicPath(userAp.getAP()));
    }

    @Subscribe
    public void onApConnected(EventProducer.APConnectedEvent event) {
        if(!hasView()) return;
        addAPIfNot(event.getAP());
    }

    private Context getContext() {
        return getView().getContext();
    }

    private ArrayAdapter<UserAp> getAdapter(){
        return getView().getApAdapter();
    }

    private boolean alreadyAdded(ApUtils.AP currentAp) {
        boolean currentAPAlreadyAdded = false;

        int count = getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            UserAp userAp = getAdapter().getItem(i);
            if (null != userAp) {
                if (currentAp.equals(userAp.getAP()) && userAp.getSSID().equals(currentAp.getSSID())) {
                    currentAPAlreadyAdded = true;
                    break;
                }
            } else {
                break;
            }
        }
        return currentAPAlreadyAdded;
    }

    private void addAPIfNot(ApUtils.AP ap) {
        if (alreadyAdded(ap)) {
            return;
        }

        UserAp userAp = UserAp.build(new UserApEntity(Device
                .getDeviceId(getContext()), ap.getBSSID(), ap.getSSID()));

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
                getAdapter().clear();
                getAdapter().addAll(userAps);

                addTag(userAps);
            }
        });
    }

    void loadAPList() {
        account.listAPs().observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<UserAp>>() {

            @Override
            public void onCompleted() {
                if (!hasView()) return;
                getView().swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                if (!hasView()) return;
                getView().swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(List<UserAp> userApList) {
                if (!hasView()) return;
                getAdapter().clear();
                getAdapter().addAll(userApList);

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
                for(UserAp ap : userAps) {
                    addTag(ap.getAP());
                }
            }
        });
    }

    private void addTag(String ap) {
        try {
            PushAgent.getInstance(getContext()).getTagManager().add(ap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
