package com.jadenine.circle.ui;

import android.app.Activity;
import android.os.Bundle;

import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

/**
 * Created by linym on 6/8/15.
 */
public class MortarActivity extends Activity {

    private MortarScope activityScope;

    @Override
    public Object getSystemService(String name) {
        MortarScope activityScope = MortarScope.findChild(getApplicationContext(), getScopeName());

        if (activityScope == null) {
            activityScope = MortarScope.buildChild(getApplicationContext())
                    .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                    .build(getScopeName());
        }

        return activityScope.hasService(name) ? activityScope.getService(name) : super
                .getSystemService(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
    }

    protected String getScopeName() {
        return this.getClass().getSimpleName();
    }
}

