package com.jadenine.circle.ui;

import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.menu.ApMenuView;
import com.jadenine.common.mortar.ActivityOwner;

/**
 * Created by linym on 6/13/15.
 */
@DaggerScope(HomeActivity.class)
@dagger.Component(dependencies = CircleApplication.AppComponent.class, modules =
        HomeActivityModule.class)
public interface HomeComponent  extends UiComponent{

    ActivityOwner activityOwner();

    DrawerHandler drawerHander();

    void inject(HomeActivity homeActivity);
    void inject(ApMenuView apMenuView);
}
