package com.jadenine.circle.mortar;

import android.content.Context;
import android.util.AttributeSet;

import com.jadenine.circle.R;
import com.jadenine.common.mortar.BasicMortarContextFactory;
import com.jadenine.common.flow.FramePathContainerView;
import com.jadenine.common.flow.SimplePathContainer;

import flow.path.Path;

public class MortarPathContainerView extends FramePathContainerView {

    public MortarPathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs, new SimplePathContainer(R.id.screen_switcher_tag, Path
                .contextFactory(new BasicMortarContextFactory(new Dagger2ScreenScoper()))));
    }
}