package com.jadenine.circle.ui.scanner;

import android.os.Bundle;
import android.util.Pair;

import java.util.List;

import mortar.ViewPresenter;

/**
 * Created by linym on 6/13/15.
 */
public class WifiPresenter  extends ViewPresenter<WifiView>{
    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        WifiScanner.scanner(getView().getContext(), new WifiScanner.ScanCallback() {
            @Override
            public void onWifiScanned(List<Pair<String, String>> result) {
                if(!hasView()) return;
                ((WifiAdapter)getView().getAdapter()).setApList(result);
            }
        });
    }
}
