package com.simple.chris.pebble

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Handler
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.postDelayed
import kotlin.math.roundToInt

object UIElements {

    fun setTheme(context: Context) {
        when (Values.theme) {
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "black" -> context.setTheme(R.style.ThemeBlack)
        }
    }

    fun constraintLayoutVisibility(layout: ConstraintLayout, value: Int, delay: Long) {
        Handler().postDelayed({
            layout.visibility = value
        }, delay)
    }

    fun constraintLayoutObjectAnimator(layout: ConstraintLayout, propertyName: String, endPos: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            var objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, endPos)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun constraintLayoutValueAnimator(layout: ConstraintLayout, startPos: Float, endPos: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            var valueAnimator = ValueAnimator.ofInt(startPos.roundToInt(), endPos.roundToInt())
            valueAnimator.addUpdateListener {
                val value:Int = it.animatedValue as Int
                var layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun imageViewObjectAnimator(layout: ImageView, propertyName: String, value: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            var objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, value)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun textViewObjectAnimator(layout: TextView, propertyName: String, value: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            var objectAnimator = ObjectAnimator.ofFloat(layout, propertyName, value)
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

}