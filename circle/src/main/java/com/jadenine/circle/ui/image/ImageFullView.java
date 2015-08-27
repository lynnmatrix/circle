package com.jadenine.circle.ui.image;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.widgets.TouchImageView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 8/27/15.
 */
public class ImageFullView extends FrameLayout{
    @InjectView(R.id.image_view)
    TouchImageView imageView;

    @Inject
    ImageFullPresenter presenter;
    public ImageFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ImagePath.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);

        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        CircleApplication.getRefWatcher(getContext()).watch(this);
    }
}
