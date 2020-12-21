package com.simple.chris.pebble.functions

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.palette.graphics.Palette
import kotlin.math.abs
import kotlin.math.roundToInt

object Calculations {

    fun screenMeasure(context: Context, value: String, window: Window): Int {
        when (value) {
            "height" -> {
                return if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    context.resources.displayMetrics.heightPixels + cutoutHeight(window)
                } else {
                    context.resources.displayMetrics.heightPixels
                }
            }
            "width" -> {
                //Toast.makeText(context, "${context.resources.displayMetrics.widthPixels + cutoutHeight(window)}", Toast.LENGTH_SHORT).show()
                return if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    context.resources.displayMetrics.widthPixels + cutoutHeight(window)

                } else {
                    context.resources.displayMetrics.widthPixels
                }

            }
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

    fun splitScreenPossible(context: Context, window: Window): Boolean {
        return Values.settingSplitScreen && pxToIn(context, window) >= 4
    }

    fun pxToIn(context: Context, window: Window): Float {
        val displayMetrics = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dpi = displayMetrics.xdpi
        return screenMeasure(context, "width", window) / dpi
    }

    fun cutoutHeight(window: Window): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val windowInsets = window.decorView.rootWindowInsets
            if (windowInsets != null) {
                val displayCutout = windowInsets.displayCutout
                if (displayCutout != null) {
                    return displayCutout.safeInsetTop + displayCutout.safeInsetBottom + displayCutout.safeInsetLeft + displayCutout.safeInsetRight
                }
            }
        }
        return 0
    }

    fun dominantColour(context: Context, colourArray: ArrayList<String>): String {
        var colourHEX = "#ffffff"
        val bitmap = createBitmap(UIElement.gradientDrawableNew(context, null, colourArray, 0f) as Drawable,
            10, 10)
        Palette.Builder(bitmap).generate { it?.let { palette ->
            val colour = palette.getDominantColor(Color.parseColor("#ffffff"))
            colourHEX = Integer.toHexString(colour)
        } }
        return ""
    }

    fun createBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        return mutableBitmap
    }

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