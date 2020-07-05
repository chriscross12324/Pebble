package com.simple.chris.pebble

import android.content.Context
import android.os.Build
import android.util.Log
import android.util.TypedValue
import kotlin.math.abs
import kotlin.math.roundToInt

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

    fun isAndroidPOrGreater(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return true
        }
        return false
    }

    fun approximatelyEqual(desiredValue: Float, actualValue: Float, tolerancePercentage: Float) : Boolean {
        val diff = abs(desiredValue - actualValue)
        val tolerance = tolerancePercentage / 100 * desiredValue
        return  diff < tolerance
    }

    fun averageColour(startColour: String, endColour: String) : String? {
        try {
            /** Remove # from hex **/
            val startRem = startColour.replace("#", "")
            val endRem = endColour.replace("#", "")

            /** Get RGB integers of startRem **/
            val startR = Integer.valueOf(startRem.substring(0, 2), 16)
            val startG = Integer.valueOf(startRem.substring(2, 4), 16)
            val startB = Integer.valueOf(startRem.substring(4, 6), 16)

            /** Get RGB integers of endRem **/
            val endR = Integer.valueOf(endRem.substring(0, 2), 16)
            val endG = Integer.valueOf(endRem.substring(2, 4), 16)
            val endB = Integer.valueOf(endRem.substring(4, 6), 16)

            /** Get average of start & end colours **/
            val avgR = (startR + (endR - startR) * 0.5).roundToInt().toString(16).padStart(2, '0')
            val avgG = (startG + (endG - startG) * 0.5).roundToInt().toString(16).padStart(2, '0')
            val avgB = (startB + (endB - startB) * 0.5).roundToInt().toString(16).padStart(2, '0')

            return (avgR + avgG + avgB)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.calculations.average_colour: ${e.localizedMessage}")
        }
        return null
    }

}