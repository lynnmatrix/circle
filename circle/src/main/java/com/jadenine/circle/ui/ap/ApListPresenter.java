package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.jadenine.circle.model.entity.UserAp;
import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.model.rest.ApService;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.jadenine.circle.ui.topic.TopicPath;
import com.jadenine.circle.utils.ApUtils;
import com.jadenine.circle.utils.Device;
import com.squareup.otto.Subscribe;
import com.umeng.message.PushAgent;

import flow.Flow;
import mortar.MortarScope;
import mortar.ViewPresenter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by linym on 6/9/15.
 */
public class ApListPresenter extends ViewPresenter<ApListView> {

    private final ApService apService;

    ApListPresenter(ApService apService) {
        this.apService = apService;
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
        Flow.get(getView()).set(new TopicPath(userAp));
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

        UserAp userAp = new UserAp(Device.getDeviceId(getContext()), ap.getBSSID(), ap.getSSID());
        apService.addAP(userAp, new Callback<JSONListWrapper<UserAp>>() {
            @Override
            public void success(JSONListWrapper<UserAp> userAps, Response response) {
                if(!hasView()) return;
                getAdapter().clear();
                getAdapter().addAll(userAps.getAll());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    void loadAPList() {
        apService.listAPs(Device.getDeviceId(getContext()), new Callback<JSONListWrapper<UserAp>>() {
            @Override
            public void success(JSONListWrapper<UserAp> userAps, Response response) {
                if(!hasView()) return;
                getAdapter().clear();
                getAdapter().addAll(userAps.getAll());

                addTag(userAps);

                ApUtils.AP ap = ApUtils.getConnectedAP(getContext());
                addAPIfNot(ap);

                getView().swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                if(!hasView()) return;
                getView().swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void addTag(final JSONListWrapper<UserAp> userAps) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for(UserAp ap : userAps.getAll()) {
                    addTag(ap.getAP());
                }
            }
        });
    }

    private void addTag(String ap) {
        try {
            Log.e("PUSH", "Try to add tag " + ap);
            PushAgent.getInstance(getContext()).getTagManager().add(ap);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PUSH", "Fail to add tag", e);
        }
    }

}
