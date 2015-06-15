package com.jadenine.circle.ui;

import android.support.v4.widget.DrawerLayout;

import com.jadenine.circle.mortar.DaggerScope;

import dagger.Provides;

/**
 * Created by linym on 6/13/15.
 */
@dagger.Module
class HomeActivityModule {
    private final HomeActivity homeActivity;
    public HomeActivityModule(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @DaggerScope(HomeActivity.class)
    @Provides
    DrawerLayout provideDrawerLayout(){
        return homeActivity.drawerLayout;
    }

    @DaggerScope(HomeActivity.class)
    @Provides
    HomePresenter providePresenter(){
        return new HomePresenter();
    }
}
