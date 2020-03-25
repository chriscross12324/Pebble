package com.simple.chris.pebble

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Handler
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.roundToInt

object UIElements {

    fun setTheme(context: Context) {
        when (Values.theme) {
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "black" -> context.setTheme(R.style.ThemeBlack)
        }
    }

    fun constraintLayoutVisibility(layout: ConstraintLayout, visibility: Int, delay: Long) {
        Handler().postDelayed({
            layout.visibility = visibility
        }, delay)
    }

    fun constraintLayoutObjectAnimator(layout: ConstraintLayout, propertyName: String, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, endValue)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun constraintLayoutHeightAnimator(layout: ConstraintLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun linearLayoutObjectAnimator(layout: LinearLayout, propertyName: String, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, endValue)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun linearLayoutHeightAnimator(layout: LinearLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun linearLayoutElevationAnimator(layout: LinearLayout, startElevation: Float, endElevation: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofFloat(startElevation, endElevation)
            valueAnimator.addUpdateListener {
                val current = it.animatedValue as Float
                layout.elevation = current
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun imageViewObjectAnimator(layout: ImageView, propertyName: String, value: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, value)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun textViewObjectAnimator(layout: TextView, propertyName: String, value: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, value)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun textViewTextChanger(layout: TextView, text: String, delay: Long) {
        Handler().postDelayed({
            layout.text = text
        }, delay)
    }

    fun contraintElevationColour(layout: ConstraintLayout, colour: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layout.outlineSpotShadowColor = colour
        }
    }

}