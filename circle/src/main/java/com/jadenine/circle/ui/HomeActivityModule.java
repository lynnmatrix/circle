package com.jadenine.circle.ui;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;

import com.jadenine.circle.R;
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
    Activity provideActivity(){
        return homeActivity;
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

    @DaggerScope(HomeActivity.class)
    @Provides
    Drawable provideErrorDrawable(Activity activity){
        Drawable errorDrawable = activity.getResources().getDrawable(R.drawable
                .ic_error_outline_black);

        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.colorPrimary};
        int indexOfAttrColorPrimary = 0;
        TypedArray a = activity.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int colorPrimary = a.getColor(indexOfAttrColorPrimary, Color.BLACK);
        a.recycle();

        errorDrawable.setColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN);
        return errorDrawable;
    }
}
