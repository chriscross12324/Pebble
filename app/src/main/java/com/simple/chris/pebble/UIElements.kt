package com.simple.chris.pebble

import android.content.Context

object UIElements {

    fun setTheme(context: Context){
        when (Values.theme){
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "black" -> context.setTheme(R.style.ThemeBlack)
        }
    }

}