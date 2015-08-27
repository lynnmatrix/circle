package com.jadenine.circle.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.common.mortar.ActivityOwner;

import mortar.Presenter;
import mortar.bundler.BundleService;

/**
 * Created by linym on 6/15/15.
 */
class HomePresenter extends Presenter<HomeActivity> implements DrawerHandler, ActivityOwner {

    @Override
    protected BundleService extractBundleService(HomeActivity activity) {
        return BundleService.getBundleService(activity);
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        getView().drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                BusProvider.post(new EventProducer.DrawerOpenEvent());
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean isDrawerOpen(int gravity) {
        if(hasView()) {
            return getView().drawerLayout.isDrawerOpen(gravity);
        } else {
            return false;
        }
    }

    @Override
    public void openDrawer(int gravity) {
        if(hasView()) {
            getView().drawerLayout.openDrawer(gravity);
        }
    }

    @Override
    public void closeDrawer(int gravity) {
        if(hasView()) {
            getView().drawerLayout.closeDrawer(gravity);
        }
    }

    @Override
    public void setDrawerLockMode(int lockMode) {
        if(hasView()) {
            getView().drawerLayout.setDrawerLockMode(lockMode);
        }
    }

    @Override
    public Activity getActivity() {
        return getView();
    }
}
