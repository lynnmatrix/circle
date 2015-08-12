package com.jadenine.common.mortar;

import android.content.Context;

import flow.path.Path;
import mortar.MortarScope;
import timber.log.Timber;

public abstract class BasicScreenScoper implements ScreenScoper {

    public MortarScope getScreenScope(Context parentContext, String name, Path path) {
        MortarScope parentScope = MortarScope.getScope(parentContext);
        Timber.d("ScreenScoper - Screen scoper with parent " +
                parentScope.getName());

        MortarScope childScope = parentScope.findChild(name);
        if (childScope != null) {
            Timber.d("ScreenScoper - Screen scoper returns existing scope " + name);
            return childScope;
        }

        MortarScope.Builder builder = parentScope.buildChild();
        configureMortarScope(parentContext, name, path, parentScope, builder);

        Timber.d( "ScreenScoper - Screen scoper builds and returns new scope " + name);
        return builder.build(name);
    }

    protected abstract void configureMortarScope(Context parentContext, String name, Path path, MortarScope parentScope, MortarScope.Builder mortarScopeBuilder);
}