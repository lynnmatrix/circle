package com.jadenine.circle.mortar;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.jadenine.circle.R;
import com.jadenine.common.flow.ActivityResultSupport;
import com.jadenine.common.mortar.MortarScopeContextFactory;
import com.jadenine.common.flow.FramePathContainerView;
import com.jadenine.common.flow.SimplePathContainer;

import flow.path.Path;

public class MortarPathContainerView extends FramePathContainerView implements PreferenceManager.OnActivityResultListener {

    public MortarPathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs, new SimplePathContainer(R.id.screen_switcher_tag, Path
                .contextFactory(new MortarScopeContextFactory(new Dagger2ScreenScoper()))));
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return ActivityResultSupport.onActivityResult(getCurrentChild(), requestCode, resultCode,
                data);
    }
}