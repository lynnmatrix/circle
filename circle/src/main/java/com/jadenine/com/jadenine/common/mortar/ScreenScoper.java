package com.jadenine.com.jadenine.common.mortar;


import android.content.Context;

import flow.path.Path;
import mortar.MortarScope;

public interface ScreenScoper {
    MortarScope getScreenScope(Context context, String name, Path path);
}
