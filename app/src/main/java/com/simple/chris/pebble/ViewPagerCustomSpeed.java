package com.simple.chris.pebble;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class ViewPagerCustomSpeed extends ViewPager {

    public ViewPagerCustomSpeed(Context context) {
        super(context);
        postInitViewPager();
    }

    public ViewPagerCustomSpeed(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }

    private ScrollerCustomSpeed mScroller = null;

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomSpeed(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        mScroller.setScrollDurationFactor(scrollFactor);
    }

}
