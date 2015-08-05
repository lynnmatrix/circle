package com.jadenine.circle.ui.widgets;

/**
 * Created by linym on 8/5/15.
 * http://stackoverflow.com/questions/11095222/android-imageview-change-tint-to-simulate-button-click/18724834#18724834
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jadenine.circle.R;

public class TintImageView extends ImageView {

    private ColorStateList tint;

    public TintImageView(Context context) {
        super(context);
    }

    public TintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintImageView, defStyle, 0);
        tint = a.getColorStateList(R.styleable.TintImageView_tint);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (tint != null /*&& tint.isStateful()*/)
            updateTintColor();
    }

    public void setColorFilter(ColorStateList tint) {
        this.tint = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }

    private void updateTintColor() {
        int color = tint.getColorForState(getDrawableState(), 0);
        setColorFilter(color);
    }

}
