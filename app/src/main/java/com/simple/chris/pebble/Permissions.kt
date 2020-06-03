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
import io.alterac.blurkit.BlurLayout

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

    fun readWritePermission(activity: Activity, context: Context, blur: BlurLayout) : Boolean {
        val writePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return if (writePermission != PackageManager.PERMISSION_GRANTED) {
            permissionDialog(activity, context, R.drawable.icon_storage, R.string.dialog_title_eng_permission_storage, R.string.dialog_body_eng_permission_storage, blur)
            false
        } else {
            true
        }
    }

    private fun permissionDialog(activity: Activity, context: Context, icon: Int, title: Int, description: Int, blur: BlurLayout) {
        blur.visibility = View.VISIBLE
        blur.startBlur()
        blur.invalidate()

        val dialog = Dialog(context, R.style.dialogStyle)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_permissions)

        val holder = dialog.findViewById<ConstraintLayout>(R.id.holder)
        val permissionIcon = dialog.findViewById<ImageView>(R.id.permissionIcon)
        val permissionTitle = dialog.findViewById<TextView>(R.id.permissionTitle)
        val permissionDescription = dialog.findViewById<TextView>(R.id.permissionDescription)
        val button = dialog.findViewById<ConstraintLayout>(R.id.button)

        permissionIcon.setImageResource(icon)
        permissionTitle.setText(title)
        permissionDescription.setText(description)

        val dialogWindow = dialog.window
        dialogWindow!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0f)
        dialogWindow.setGravity(Gravity.CENTER)
        dialog.show()

        holder.post {
            UIElements.viewObjectAnimator(holder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "alpha", 1f, 100, 100, LinearInterpolator())

            UIElements.viewObjectAnimator(button, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(button, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(button, "alpha", 1f, 100, 100, LinearInterpolator())
        }

        button.setOnClickListener {
            UIElements.viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

            UIElements.viewObjectAnimator(button, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(button, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(button, "alpha", 0f, 200, 200, LinearInterpolator())

            Handler().postDelayed({
                dialog.dismiss()
                blur.pauseBlur()
                blur.visibility = View.GONE

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }, 450)
        }
    }
}