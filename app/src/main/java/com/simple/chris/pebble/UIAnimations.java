package com.simple.chris.pebble;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.ViewGroup;
import android.widget.GridView;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import eightbitlab.com.blurview.BlurView;


public class UIAnimations {

    public static void blurViewObjectAnimator(BlurView layout, String propertyName, float finishedPos, int duration, int delay, TimeInterpolator interpolator) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, finishedPos);
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(interpolator);
            objectAnimator.start();
        }, delay);
    }

    public static void constraintLayoutVisibility(ConstraintLayout layout, int visibility, int delay){
        Handler handler = new Handler();
        handler.postDelayed(() -> layout.setVisibility(visibility), delay);
    }

    public static void constraintLayoutAlpha(ConstraintLayout layout, int value, int delay){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            layout.setAlpha(value);
        }, delay);
    }

    public static void gridViewObjectAnimator(GridView layout, String propertyName, int finishedPos, int duration, int delay, TimeInterpolator interpolator) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, finishedPos);
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(interpolator);
            objectAnimator.start();
        }, delay);
    }

    public static void constraintLayoutObjectAnimator(ConstraintLayout layout, String propertyName, float finishedPos, int duration, int delay, TimeInterpolator interpolator) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, finishedPos);
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(interpolator);
            objectAnimator.start();
        }, delay);
    }

    public static void constraintLayoutValueAnimator(ConstraintLayout layout, float startPos, float endPos, int duration, int delay, TimeInterpolator interpolator) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
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
        }, delay);
    }

    public static void imageViewObjectAnimator(ImageView layout, String propertyName, int value, int duration, int delay, TimeInterpolator interpolator) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, value);
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(interpolator);
            objectAnimator.start();
        }, delay);
    }

    public static void textViewObjectAnimator(TextView layout, String propertyName, int value, int duration, int delay, TimeInterpolator interpolator) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, value);
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(interpolator);
            objectAnimator.start();
        }, delay);
    }

    public static void textViewChanger(TextView layout, String text, int delay){
        Handler handler = new Handler();
        handler.postDelayed(() -> layout.setText(text), delay);
    }

}
