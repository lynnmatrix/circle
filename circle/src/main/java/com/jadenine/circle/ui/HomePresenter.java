package com.jadenine.circle.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.jadenine.circle.R;
import com.jadenine.circle.ui.scanner.WifiPath;
import com.jadenine.common.mortar.ActivityOwner;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;

import flow.Flow;
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
