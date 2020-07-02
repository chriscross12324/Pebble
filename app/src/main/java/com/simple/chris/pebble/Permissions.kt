package com.simple.chris.pebble

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat

object Permissions {
    /*
    This checks for and asks for permissions
     */

    fun readStoragePermissionGiven(context: Context): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        return readPermission == PackageManager.PERMISSION_GRANTED
    }

    fun writeStoragePermissionGiven(context: Context): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return writePermission == PackageManager.PERMISSION_GRANTED
    }

}