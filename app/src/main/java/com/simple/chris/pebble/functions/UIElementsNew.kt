package com.simple.chris.pebble.functions

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.graphics.Color
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