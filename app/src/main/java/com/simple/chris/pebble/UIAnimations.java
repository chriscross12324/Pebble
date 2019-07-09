package com.simple.chris.pebble;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class UIAnimations {
    public static void gridViewObjectAnimator(GridView layout, String propertyName, int finishedPos, int duration, TimeInterpolator interpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, finishedPos);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(interpolator);
        objectAnimator.start();
    }

    public static void constraintLayoutObjectAnimator(ConstraintLayout layout, String propertyName, int finishedPos, int duration, TimeInterpolator interpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, finishedPos);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(interpolator);
        objectAnimator.start();
    }

    public static void constraintLayoutValueAnimator(ConstraintLayout layout, float startPos, int endPos, int duration, TimeInterpolator interpolator) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(Math.round(startPos), Math.round(endPos));
        valueAnimator.addUpdateListener(animation -> {
            int val = (Integer) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.height = val;
            layout.setLayoutParams(layoutParams);
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}
