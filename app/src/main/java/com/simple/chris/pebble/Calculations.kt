package com.simple.chris.pebble

import android.content.Context

object Calculations {

    fun screenMeasure(context: Context, value: String): Int {
        when(value) {
            "height" -> return context.resources.displayMetrics.heightPixels
            "width" -> return context.resources.displayMetrics.widthPixels
        }
        return 0
    }

}