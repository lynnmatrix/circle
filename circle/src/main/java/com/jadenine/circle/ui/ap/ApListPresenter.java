package com.jadenine.circle.ui.ap;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.jadenine.circle.entity.UserAp;
import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.request.ApService;
import com.jadenine.circle.request.JSONListWrapper;
import com.jadenine.circle.utils.ApUtils;
import com.jadenine.circle.utils.Device;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import mortar.MortarScope;
import mortar.ViewPresenter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by linym on 6/9/15.
 */
public class ApListPresenter extends ViewPresenter<ApListView> {

    ApService apService;

    private ArrayAdapter<UserAp> userApAdapter;

    ApListPresenter(ApService apService) {
        this.apService = apService;
    }

    @Override
    protected void onEnterScope(MortarScope scope) {
        super.onEnterScope(scope);
        BusProvider.register(this);
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (!hasView()) return;

        if (null == userApAdapter) {
            userApAdapter = new ArrayAdapter<>(getView().getContext(), android.R.layout.simple_list_item_1, new ArrayList<UserAp>(0));

            getView().apListView.setAdapter(userApAdapter);
        }

        loadAPList();
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        BusProvider.unregister(this);
    }

    public void onApSelected(int position) {
        UserAp userAp = userApAdapter.getItem(position);
//        Flow.get(getView()).set(new MessagePath(userAp));
    }

    @Subscribe
    public void onApConnected(EventProducer.APConnectedEvent event) {
        addAPIfNot(event.getAP());
    }

    private Context getContext() {
        return getView().getContext();
    }

    private boolean alreadyAdded(ApUtils.AP currentAp) {
        boolean currentAPAlreadyAdded = false;

        int count = userApAdapter.getCount();
        for (int i = 0; i < count; i++) {
            UserAp userAp = userApAdapter.getItem(i);
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
                userApAdapter.clear();
                userApAdapter.addAll(userAps.getAll());
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

                userApAdapter.clear();
                userApAdapter.addAll(userAps.getAll());

                ApUtils.AP ap = ApUtils.getConnectedAP(getContext());
                addAPIfNot(ap);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
