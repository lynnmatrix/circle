package com.jadenine.circle.ui;

import android.support.v4.widget.DrawerLayout;

import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.mortar.DaggerScope;

import retrofit.RestAdapter;

/**
 * Created by linym on 6/13/15.
 */
@DaggerScope(HomeActivity.class)
@dagger.Component(dependencies = CircleApplication.AppComponent.class, modules =
        HomeActivityModule.class)
public interface HomeComponent {
    DrawerLayout getDrawerLayout();
    RestAdapter restAdapter();
    Account account();
    void inject(HomeActivity homeActivity);
}
