package com.jadenine.circle.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;

import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.menu.ApMenuView;

/**
 * Created by linym on 6/13/15.
 */
@DaggerScope(HomeActivity.class)
@dagger.Component(dependencies = CircleApplication.AppComponent.class, modules =
        HomeActivityModule.class)
public interface HomeComponent {
    Activity getActivity();
    DrawerLayout getDrawerLayout();
    Drawable getErrorDrawable();
    AvatarBinder avatarBinder();

    Account account();
    void inject(HomeActivity homeActivity);

    void inject(ApMenuView apMenuView);
}
