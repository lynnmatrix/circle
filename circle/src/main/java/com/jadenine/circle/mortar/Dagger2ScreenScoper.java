package com.jadenine.circle.mortar;

import android.content.Context;

import com.jadenine.common.mortar.BasicScreenScoper;

import flow.path.Path;
import mortar.MortarScope;
import timber.log.Timber;

/**
 * Created by linym on 6/8/15.
 */
public class Dagger2ScreenScoper extends BasicScreenScoper {

    @Override
    protected void configureMortarScope(Context parentContext, String name, Path path, MortarScope parentScope, MortarScope.Builder mortarScopeBuilder) {
        if (!(path instanceof ScreenComponentFactory)) {
            Timber.w("Path must implement ComponentFactory");
            return;
        }

        ScreenComponentFactory screenComponentFactory = (ScreenComponentFactory) path;
        Object parentComponent = parentScope.getService(DaggerService.SERVICE_NAME);
        Object component = screenComponentFactory.createComponent(parentComponent);
        mortarScopeBuilder.withService(DaggerService.SERVICE_NAME, component);
        Timber.i(DaggerService.SERVICE_NAME + " "+ component.toString() +" " + parentComponent.toString());
    }
}
