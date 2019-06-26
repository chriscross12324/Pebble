package com.simple.chris.pebble;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Handler;
import android.widget.GridView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class UIAnimations {
    public static void gridViewSlideAnimation(GridView layout, String propertyName, int finishedPos, TimeInterpolator interpolator){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, finishedPos);
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(interpolator);
        objectAnimator.start();

    }
}
