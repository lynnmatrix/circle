package com.jadenine.circle.mortar;

import android.content.Context;

import com.jadenine.com.jadenine.common.mortar.BasicScreenScoper;

import flow.path.Path;
import mortar.MortarScope;

/**
 * Created by linym on 6/8/15.
 */
public class Dagger2ScreenScoper extends BasicScreenScoper {

    @Override
    protected void configureMortarScope(Context context, String name, Path path, MortarScope parentScope, MortarScope.Builder mortarScopeBuilder) {
        if (!(path instanceof ScreenComponentFactory)) {
            throw new IllegalStateException("Path must imlement ComponentFactory");
        }

        ScreenComponentFactory screenComponentFactory = (ScreenComponentFactory) path;
        Object component = screenComponentFactory.createComponent(parentScope.getService(DaggerService.SERVICE_NAME));
        mortarScopeBuilder.withService(DaggerService.SERVICE_NAME, component);
    }
}
