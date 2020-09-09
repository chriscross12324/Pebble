package com.simple.chris.pebble.functions

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.roundToInt

object Calculations {

    fun screenMeasure(context: Context, value: String, window: Window): Int {
        when (value) {
            "height" -> {
                Log.e("INFO", "${context.resources.displayMetrics.heightPixels + cutoutHeight(window)}")
                return context.resources.displayMetrics.heightPixels + cutoutHeight(window)
            }
            "width" -> return context.resources.displayMetrics.widthPixels
            "largest" -> {
                return if (screenMeasure(context, "height", window) > screenMeasure(context, "width", window)) {
                    screenMeasure(context, "height", window)
                } else {
                    screenMeasure(context, "width", window)
                }
            }
            "smallest" -> {
                return if (screenMeasure(context, "height", window) < screenMeasure(context, "width", window)) {
                    screenMeasure(context, "height", window)
                } else {
                    screenMeasure(context, "width", window)
                }
            }
        }
        return 0
    }

    fun cutoutHeight(window: Window): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val windowInsets = window.decorView.rootWindowInsets
            if (windowInsets != null) {
                val displayCutout = windowInsets.displayCutout
                if (displayCutout != null) {
                    Log.e("ERR", "${displayCutout.safeInsetTop}")
                    return displayCutout.safeInsetTop
                }
            }
        }
        return 0
    }

    /*fun cutoutHeight(decorView: View) : Int {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            decorView.rootWindowInsets.displayCutout!!.boundingRects.
        } else {
            0
        }
    }*/

    fun stringArraytoIntArray(stringArray: ArrayList<String>): IntArray {
        val intArray: IntArray
        val arrayList: ArrayList<Int> = ArrayList()
        for (i in 0 until stringArray.size) { //i in stringArray.size - 1 downTo 0
            try {
                arrayList.add(Color.parseColor(stringArray[i]))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.functions.calculations.string_array_to_int_array: ${e.localizedMessage}")
            }
        }
        intArray = arrayList.toIntArray()
        return intArray
    }

    fun viewWrapContent(view: View, value: String): Int {
        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        when (value) {
            "height" -> return view.measuredHeight
            "width" -> return view.measuredWidth
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

    fun approximatelyEqual(desiredValue: Float, actualValue: Float, tolerancePercentage: Float): Boolean {
        val diff = abs(desiredValue - actualValue)
        val tolerance = tolerancePercentage / 100 * desiredValue
        return diff < tolerance
    }

    fun averageColour(startColour: String, endColour: String): String? {
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