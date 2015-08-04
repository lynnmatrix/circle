package com.jadenine.circle.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.common.flow.GsonParceler;

import butterknife.ButterKnife;
import flow.Flow;
import flow.FlowDelegate;
import flow.History;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

/**
 * Created by linym on 6/8/15.
 */
abstract class MortarActivity extends Activity {

    private MortarScope mortarScope;
    protected FlowDelegate flowDelegate;

    @Override
    public Object getSystemService(String name) {
        Object service = null;
        if (flowDelegate != null) {
            service = flowDelegate.getSystemService(name);
        }

        if (service == null && mortarScope != null && mortarScope.hasService(name)) {
            service = mortarScope.getService(name);
        }

        return service != null ? service : super.getSystemService(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Container container = getClass().getAnnotation(Container.class);
        setContentView(container.value());
        ButterKnife.inject(this);

        mortarScope = MortarScope.findChild(getApplicationContext(), getScopeName());

        if (mortarScope == null) {
            MortarScope.Builder builder = MortarScope.buildChild(getApplicationContext());
            builder.withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner());

            Object component = buildDaggerService();
            if(null != component) {
                builder.withService(DaggerService.SERVICE_NAME, component);
            }
            mortarScope = builder.build(getScopeName());
        }

        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);

        GsonParceler parceler = new GsonParceler(new Gson());
        @SuppressWarnings("deprecation") FlowDelegate.NonConfigurationInstance nonConfig =
                (FlowDelegate.NonConfigurationInstance) getLastNonConfigurationInstance();
        flowDelegate = FlowDelegate.onCreate(nonConfig, getIntent(), savedInstanceState,
                parceler, History.single(getFirstScreen()), getFlowDispatcher());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        flowDelegate.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flowDelegate.onResume();
    }

    @Override
    protected void onPause() {
        flowDelegate.onPause();
        super.onPause();
    }

    @SuppressWarnings("deprecation") // https://code.google.com/p/android/issues/detail?id=151346
    @Override
    public Object onRetainNonConfigurationInstance() {
        return flowDelegate.onRetainNonConfigurationInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        flowDelegate.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
    }

    @Override protected void onDestroy() {
        // mortarScope may be null in case isWrongInstance() returned true in onCreate()
        if (isFinishing() && mortarScope != null) {
            mortarScope.destroy();
            mortarScope = null;
        }

        super.onDestroy();
    }

    protected String getScopeName() {
        return this.getClass().getSimpleName();
    }

    protected abstract Object buildDaggerService();

    protected abstract Object getFirstScreen();

    protected abstract Flow.Dispatcher getFlowDispatcher();
}

