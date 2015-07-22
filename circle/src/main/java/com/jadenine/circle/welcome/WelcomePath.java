package com.jadenine.circle.welcome;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.common.flow.Layout;

import flow.path.Path;

/**
 * Created by linym on 7/22/15.
 */
@Layout(R.layout.screen_welcome)
public class WelcomePath extends Path implements ScreenComponentFactory {
    @Override
    public Object createComponent(Object... dependencies) {
        return dependencies[0];
    }

}
