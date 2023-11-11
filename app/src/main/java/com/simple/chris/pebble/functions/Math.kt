package com.simple.chris.pebble.functions

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kotlin.random.Random


fun canSplitScreen(context: Context, window: Window): Boolean {
    return Values.settingSplitScreen && convertPxToInch(context, window) >= 4
}

fun convertPxToInch(context: Context, window: Window): Float {
    val displayMetrics = DisplayMetrics()
    window.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val dpi = displayMetrics.xdpi
    return getScreenMetrics(context, window).width / dpi
}

fun convertFloatToDP(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

fun convertStringArrToIntArr(stringArr: ArrayList<String>): IntArray {
    return stringArr.mapNotNull {
        try {
            Color.parseColor(it)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.functions.Math.convertStringArrToIntArr: ${e.localizedMessage}")
            null
        }
    }.toIntArray()
}

fun getScreenMetrics(context: Context, window: Window): ScreenMetrics {
    val isPortrait =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val screenHeight =
        context.resources.displayMetrics.heightPixels + if (isPortrait) getCutoutHeight(window) else 0
    val screenWidth =
        context.resources.displayMetrics.widthPixels + if (!isPortrait) getCutoutHeight(window) else 0

    return ScreenMetrics(
        screenHeight,
        screenWidth,
        minOf(screenHeight, screenWidth),
        maxOf(screenHeight, screenWidth),
    )
}

fun getViewMetrics(view: View): ScreenMetrics {
    view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    return ScreenMetrics(
        view.measuredHeight,
        view.measuredWidth,
        minOf(view.measuredHeight, view.measuredWidth),
        maxOf(view.measuredHeight, view.measuredWidth),
    )
}

fun getCutoutHeight(window: Window): Int {
    val windowInsets = window.decorView.rootWindowInsets
    if (windowInsets != null) {
        val displayCutout = windowInsets.displayCutout
        if (displayCutout != null) {
            return displayCutout.safeInsetTop + displayCutout.safeInsetBottom + displayCutout.safeInsetLeft + displayCutout.safeInsetRight
        }
    }
    return 0
}

fun generateBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
    val mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(mutableBitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)

    return mutableBitmap
}

fun generateRandomColour(): String {
    return "#${
        Integer.toHexString(
            Color.rgb(
                Random.nextInt(256),
                Random.nextInt(256),
                Random.nextInt(256)
            )
        ).substring(2)
    }"
}

class ScreenMetrics constructor(
    var height: Int,
    var width: Int,
    var smallest: Int,
    var largest: Int
)

enum class Property {
    HEIGHT, WIDTH, SMALLEST, LARGEST, VISIBILITY, RADIUS, TRANSLATION_Y, TRANSLATION_X, SCALE_Y, SCALE_X, ALPHA
}

fun Property.toReadableString(): String {
    return when (this) {
        Property.HEIGHT -> "height"
        Property.WIDTH -> "width"
        Property.SMALLEST -> "smallest"
        Property.LARGEST -> "largest"
        Property.VISIBILITY -> "visibility"
        Property.RADIUS -> "radius"
        Property.TRANSLATION_Y -> "translationY"
        Property.TRANSLATION_X -> "translationX"
        Property.SCALE_Y -> "scaleY"
        Property.SCALE_X -> "scaleX"
        Property.ALPHA -> "alpha"
    }
}