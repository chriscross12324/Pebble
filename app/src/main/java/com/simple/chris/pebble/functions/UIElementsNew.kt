package com.simple.chris.pebble.functions

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import com.simple.chris.pebble.R

fun setAppTheme(context: Context) {
    when (Values.settingTheme) {
        "light" -> context.setTheme(R.style.ThemeLight)
        "dark" -> context.setTheme(R.style.ThemeDark)
        "darker" -> context.setTheme(R.style.ThemeDarker)
        else -> {
            context.setTheme(R.style.ThemeDarker)
            Values.settingTheme = "darker"
        }
    }
}

fun generateGradientDrawable(
    context: Context,
    view: View?,
    colourArray: ArrayList<String>,
    cornerRadius: Float
): Drawable? {
    try {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            if (colourArray.size >= 2) convertStringArrToIntArr(colourArray)
            else intArrayOf(
                Color.parseColor(
                    colourArray.toString().replace("[", "").replace("]", "")
                ),
                Color.parseColor(
                    colourArray.toString().replace("[", "").replace("]", "")
                ),
            )
        ).apply {
            this.cornerRadius = convertFloatToDP(context, cornerRadius)
        }

        view?.run {
            background = gradientDrawable
            outlineSpotShadowColor = Color.parseColor(colourArray.last())
        }

        return if (view == null) gradientDrawable else null
    } catch (e: Exception) {
        Log.e("ERR", "pebble.functions.UIElementsNew: ${e.localizedMessage}")
    }
    return null
}

fun animateView(
    view: View,
    property: Property,
    finalValue: Float,
    duration: Long = 500L,
    startDelay: Long = 0L,
    interpolator: TimeInterpolator = DecelerateInterpolator(3f)
) {
    when (val translatedEnum = property.toReadableString()) {
        "height", "width" -> animateViewHeightWidth(view, translatedEnum, finalValue, duration, startDelay, interpolator)
        "visibility" -> setViewVisibility(view, finalValue.toInt(), startDelay)
        else -> animateViewGeneric(view, translatedEnum, finalValue, duration, startDelay, interpolator)
    }
}

private fun animateViewGeneric(
    view: View,
    property: String,
    finalValue: Float,
    duration: Long,
    startDelay: Long,
    interpolator: TimeInterpolator
) {
    ObjectAnimator.ofFloat(view, property, finalValue).apply {
        this.duration = duration
        this.startDelay = startDelay
        this.interpolator = interpolator
        start()
    }
}

private fun animateViewHeightWidth(
    view: View,
    property: String,
    finalValue: Float,
    duration: Long,
    startDelay: Long,
    interpolator: TimeInterpolator
) {
    val initialSize = when (property) {
        "height" -> view.height
        "width" -> view.width
        else -> 0
    }
    ValueAnimator.ofInt(initialSize, finalValue.toInt()).apply {
        this.duration = duration
        this.startDelay = startDelay
        this.interpolator = interpolator
        addUpdateListener {
            val animatedValue = it.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams

            when (property) {
                "height" -> layoutParams.height = animatedValue
                "width" -> layoutParams.width = animatedValue
            }

            view.layoutParams = layoutParams
        }
        start()
    }
}

private fun setViewVisibility(
    view: View,
    finalValue: Int,
    startDelay: Long
) {
    Handler(Looper.getMainLooper()).postDelayed({
        view.visibility = finalValue
    }, startDelay)
}