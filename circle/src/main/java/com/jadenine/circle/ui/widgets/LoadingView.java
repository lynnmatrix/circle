package com.jadenine.circle.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jadenine.circle.R;

public class LoadingView extends View {
    private static final int LARGE_ARC_SPEED = 1000;
    private static final int SMALL_ARC_SPEED = 1500;
    private static final int ANGEL_TOTAL = 360;
    private static final int ARC_ANGEL = 160;
    private static final int INVALIDATE_INTERVAL = 50;

    private Paint mStokePaint = new Paint();
    private static int mSmallRadius;
    private static int mLargeRadius;

    private boolean mAnimationOn;
    private long mStartTime;
    private RectF mLargeRect;
    private RectF mSmallRect;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mStokePaint.setStyle(Paint.Style.STROKE);
        mStokePaint.setColor(context.getResources().getColor(R.color.gray_e0));
        mStokePaint.setStrokeWidth(context.getResources().getDimension(R.dimen.load_more_circle_thickness));
        mStokePaint.setAntiAlias(true);
        mSmallRadius = (int) context.getResources().getDimension(R.dimen.load_more_circle_large);
        mLargeRadius = (int) context.getResources().getDimension(R.dimen.load_more_circle_small);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int angelLarge = (int) ((System.currentTimeMillis() - mStartTime) % LARGE_ARC_SPEED) * ANGEL_TOTAL / LARGE_ARC_SPEED;
        int angelSmall = ANGEL_TOTAL - (int) ((System.currentTimeMillis() - mStartTime) % SMALL_ARC_SPEED) * ANGEL_TOTAL / SMALL_ARC_SPEED;
        if (mLargeRect == null) {
            mLargeRect = new RectF(canvas.getWidth() / 2 - mLargeRadius, canvas.getHeight() / 2 - mLargeRadius, canvas.getWidth() / 2 + mLargeRadius, canvas.getHeight() / 2 + mLargeRadius);
        }
        if (mSmallRect == null) {
            mSmallRect = new RectF(canvas.getWidth() / 2 - mSmallRadius, canvas.getHeight() / 2 - mSmallRadius, canvas.getWidth() / 2 + mSmallRadius, canvas.getHeight() / 2 + mSmallRadius);
        }

        canvas.drawArc(mLargeRect, angelLarge, ARC_ANGEL, false, mStokePaint);
        canvas.drawArc(mLargeRect, angelLarge + ANGEL_TOTAL / 2, ARC_ANGEL, false, mStokePaint);
        canvas.drawArc(mSmallRect, angelSmall + ANGEL_TOTAL / 4, ARC_ANGEL, false, mStokePaint);
        canvas.drawArc(mSmallRect, angelSmall + ANGEL_TOTAL * 3 / 4, ARC_ANGEL, false, mStokePaint);

        if (mAnimationOn) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            }, INVALIDATE_INTERVAL);
        }
    }

    public boolean isAnimationOn() {
        return mAnimationOn;
    }

    public void toggleAnimation(boolean show) {
        if (show) {
            mStartTime = System.currentTimeMillis();
            mAnimationOn = true;
            invalidate();
        } else {
            mAnimationOn = false;
        }
    }
}
