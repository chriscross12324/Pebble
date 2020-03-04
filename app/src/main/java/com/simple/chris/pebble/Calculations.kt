package com.simple.chris.pebble

import android.content.Context
import android.util.TypedValue

object Calculations {

    fun screenMeasure(context: Context, value: String): Int {
        when(value) {
            "height" -> return context.resources.displayMetrics.heightPixels
            "width" -> return context.resources.displayMetrics.widthPixels
        }
        return 0
    }

    fun convertToDP(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

}