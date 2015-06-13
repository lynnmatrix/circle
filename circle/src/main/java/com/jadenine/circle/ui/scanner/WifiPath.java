package com.jadenine.circle.ui.scanner;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;

import dagger.Provides;

/**
 * Created by linym on 6/13/15.
 */
@Layout(R.layout.screen_wifi_list)
public class WifiPath extends flow.path.Path implements ScreenComponentFactory{
    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerWifiPath_Component.builder().homeComponent((HomeComponent) dependencies[0])
                .module(new Module()).build();
    }

    @DaggerScope(WifiPresenter.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(WifiView wifiView);
    }

    @dagger.Module
    class Module{
        @DaggerScope(WifiPresenter.class)
        @Provides
        WifiPresenter providePresenter(){
            return new WifiPresenter();
        }
    }
}
