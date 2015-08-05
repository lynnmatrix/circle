package com.jadenine.circle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.menu.ApMenuPresenter;
import com.jadenine.common.mortar.ActivityOwner;

import dagger.Provides;

/**
 * Created by linym on 6/13/15.
 */
@dagger.Module
class HomeActivityModule {

    @DaggerScope(HomeActivity.class)
    @Provides
    HomePresenter providePresenter(){
        return new HomePresenter();
    }

    @DaggerScope(HomeActivity.class)
    @Provides
    ApMenuPresenter provideMenuPresenter(Account account) {
        return new ApMenuPresenter(account);
    }

    @DaggerScope(HomeActivity.class)
    @Provides
    DrawerHandler provideDrawerHandler(HomePresenter homePresenter) {
        return homePresenter;
    }

    @DaggerScope(HomeActivity.class)
    @Provides
    ActivityOwner provideActivityOwner(HomePresenter homePresenter) {
        return homePresenter;
    }

    @DaggerScope(HomeActivity.class)
    @Provides
    Drawable provideErrorDrawable(Context appContext){
        Drawable errorDrawable = appContext.getResources().getDrawable(R.drawable
                .ic_error_outline_black);

        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.colorPrimary};
        int indexOfAttrColorPrimary = 0;
        TypedArray a = appContext.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int colorPrimary = a.getColor(indexOfAttrColorPrimary, Color.BLACK);
        a.recycle();

        errorDrawable.setColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN);
        return errorDrawable;
    }
}
