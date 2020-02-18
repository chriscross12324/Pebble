package com.simple.chris.pebble

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.logging.Handler

object UIElements {

    fun setTheme(context: Context) {
        when (Values.theme) {
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "black" -> context.setTheme(R.style.ThemeBlack)
        }
    }

    fun constraintLayoutAlpha(layout: ConstraintLayout, value: Int, delay: Int) {
        val handler: Handler
    }

}