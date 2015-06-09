package com.jadenine.circle.ui.ap;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.request.ApService;
import com.jadenine.common.flow.Layout;

import dagger.Provides;
import flow.path.Path;
import retrofit.RestAdapter;

/**
 * Created by linym on 6/8/15.
 */
@DaggerScope(ApListPresenter.class)
@Layout(R.layout.screen_ap_list)
public class ApListPath extends Path implements ScreenComponentFactory{

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerApListPath_Component.builder().appComponent((CircleApplication.AppComponent)
                dependencies[0]).module(new ApListPath.Module()).build();
    }

    @DaggerScope(ApListPresenter.class)
    @dagger.Component(dependencies = CircleApplication.AppComponent.class, modules = Module.class)
    public interface Component{
        void inject(ApListView apListView);
    }

    @dagger.Module
    public static class Module {
        @Provides
        @DaggerScope(ApListPresenter.class)
        public ApListPresenter providePresenter(ApService apService) {
            return new ApListPresenter(apService);
        }
        @Provides
        @DaggerScope(ApListPresenter.class)
        public ApService provideApService(RestAdapter restAdapter) {
            return restAdapter.create(ApService.class);
        }
    }

}
