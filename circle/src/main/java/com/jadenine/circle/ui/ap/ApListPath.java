package com.jadenine.circle.ui.ap;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 6/8/15.
 */
@DaggerScope(ApListPresenter.class)
@Layout(R.layout.screen_ap_list)
public class ApListPath extends Path implements ScreenComponentFactory{

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerApListPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0]).module(new ApListPath.Module()).build();
    }

    @DaggerScope(ApListPresenter.class)
    @dagger.Component(dependencies = {HomeComponent.class}, modules = Module.class)
    public interface Component{
        void inject(ApListView apListView);
    }

    @dagger.Module
    public static class Module {
        @Provides
        @DaggerScope(ApListPresenter.class)
        public ApListPresenter providePresenter(Account account) {
            return new ApListPresenter(account);
        }
    }

}
