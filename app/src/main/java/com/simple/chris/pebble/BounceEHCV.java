package com.simple.chris.pebble;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;

public class BounceEHCV extends ScrollView {

    public static final int MAX_Y_OVERSCROLL_DISTANCE = 25;

    public Context mContext;
    private int mMaxYOverscrollDistance;

    public BounceEHCV(Context context){
        super(context);
        mContext = context;
        initBounceEHCV();
    }
    public BounceEHCV(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        mContext = context;
        initBounceEHCV();
    }
    public BounceEHCV(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
        mContext = context;
        initBounceEHCV();
    }

    private void initBounceEHCV(){
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent){
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }
}
