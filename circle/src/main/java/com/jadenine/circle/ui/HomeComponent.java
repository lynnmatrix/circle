package com.jadenine.circle.ui;

import android.graphics.drawable.Drawable;

import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.menu.ApMenuView;
import com.jadenine.common.mortar.ActivityOwner;

/**
 * Created by linym on 6/13/15.
 */
@DaggerScope(HomeActivity.class)
@dagger.Component(dependencies = CircleApplication.AppComponent.class, modules =
        HomeActivityModule.class)
public interface HomeComponent {
    Drawable getErrorDrawable();
    AvatarBinder avatarBinder();

    Account account();

    DrawerHandler drawerHandler();

    ActivityOwner activityOwner();

    void inject(HomeActivity homeActivity);
    void inject(ApMenuView apMenuView);
}
