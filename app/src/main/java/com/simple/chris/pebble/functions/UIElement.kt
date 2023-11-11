package com.simple.chris.pebble.functions

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

@SuppressLint("StaticFieldLeak")
object UIElement {

    fun hideSoftKeyboard(activity: Activity) {
        try {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = activity.currentFocus as View
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.ui_element.hide_soft_keyboard: ${e.localizedMessage}")
        }

    }
}