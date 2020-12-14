package com.simple.chris.pebble.functions

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.renderscript.*
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jgabrielfreitas.core.BlurImageView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.Calculations.convertToDP
import kotlin.math.roundToInt

object UIElements {

    lateinit var dialogOneButton: Dialog
    lateinit var popupDialog: Dialog

    fun viewVisibility(view: View, visibility: Int, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            view.visibility = visibility
        }, delay)
    }

    fun viewObjectAnimator(view: View, propertyName: String, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(view, propertyName, endValue)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun constraintLayoutHeightAnimator(layout: ConstraintLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun viewWidthAnimator(layout: View, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.width = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun viewHeightAnimator(layout: View, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun constraintLayoutElevationAnimator(layout: ConstraintLayout, endElevation: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofFloat(layout.elevation, endElevation)
            valueAnimator.addUpdateListener {
                val current = it.animatedValue as Float
                layout.elevation = current
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun linearLayoutHeightAnimator(layout: LinearLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun linearLayoutElevationAnimator(layout: LinearLayout, startElevation: Float, endElevation: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofFloat(startElevation, endElevation)
            valueAnimator.addUpdateListener {
                val current = it.animatedValue as Float
                layout.elevation = current
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun textViewTextChanger(layout: TextView, text: String, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            layout.text = text
        }, delay)
    }

    fun constraintElevationColour(layout: ConstraintLayout, colour: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layout.outlineSpotShadowColor = colour
        }
    }

    fun bottomSheetPeekHeightAnim(bottomSheet: BottomSheetBehavior<CardView>, peekHeight: Int, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            var valueAnimator = ValueAnimator()
            valueAnimator.setIntValues(bottomSheet.peekHeight, peekHeight)
            valueAnimator.duration = duration
            valueAnimator.interpolator = interpolator
            valueAnimator.addUpdateListener {
                val current = it.animatedValue as Int
                bottomSheet.peekHeight = current
            }
            valueAnimator.start()
        }, delay)
    }

    fun setWallpaper(context: Context, imageView: BlurImageView, alphaLayer: ImageView, window: Window) {
        if (Permissions.readStoragePermissionGiven(context)) {
            if (Values.settingsSpecialEffects) {
                try {
                    val wallpaper: WallpaperManager = WallpaperManager.getInstance(context)
                    val wallpaperDrawable: Drawable = wallpaper.drawable
                    val bmpDraw = wallpaperDrawable as BitmapDrawable
                    val bmp = bmpDraw.bitmap
                    val wallpaperBMP = Bitmap.createScaledBitmap(bmp, Calculations.screenMeasure(context, "width", window), Calculations.screenMeasure(context, "height", window), true)
                    imageView.setImageBitmap(wallpaperBMP)
                    imageView.setBlur(15)
                    viewObjectAnimator(alphaLayer, "alpha", 0.3f, 150, 0, LinearInterpolator())
                } catch (e: Exception) {
                    Log.e("ERR", "pebble.ui_elements.set_wallpaper.from.$context.${e.localizedMessage}")
                }
            } else {
                viewObjectAnimator(alphaLayer, "alpha", 1f, 150, 0, LinearInterpolator())
            }
        }
    }

    private fun blurRenderScript(context: Context, bitmap: Bitmap, radius: Float): Bitmap {

        val renderScript = RenderScript.create(context)

        val normalBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val blurInput = Allocation.createFromBitmap(renderScript, bitmap)
        val blurOutput = Allocation.createFromBitmap(renderScript, normalBitmap)

        val blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        blur.setInput(blurInput)
        blur.setRadius(radius)
        blur.forEach(blurOutput)

        blurOutput.copyTo(normalBitmap)
        renderScript.destroy()

        return normalBitmap
    }


    fun oneButtonDialog(context: Context, icon: Int, title: Int, body: Int, buttonText: Int, listener: View.OnClickListener) {
        dialogOneButton = Dialog(context, R.style.dialogStyle)
        dialogOneButton.setCancelable(false)
        dialogOneButton.setContentView(R.layout.dialog_one_button)

        val dialogMainHolder = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogMainHolder)
        val dialogIcon = dialogOneButton.findViewById<ImageView>(R.id.dialogIcon)
        val dialogTitle = dialogOneButton.findViewById<TextView>(R.id.dialogTitle)
        val dialogBody = dialogOneButton.findViewById<TextView>(R.id.dialogBody)
        val dialogButton = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogButton1)
        val dialogButtonText = dialogOneButton.findViewById<TextView>(R.id.dialogButton1Text)

        dialogIcon.setImageResource(icon)
        dialogTitle.setText(title)
        dialogBody.setText(body)
        dialogButtonText.setText(buttonText)

        dialogButton.setOnClickListener(listener)

        val dialogWindow = dialogOneButton.window
        dialogWindow!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogWindow.setDimAmount(0.5f)
        dialogWindow.setGravity(Gravity.BOTTOM)
        dialogOneButton.show()

        dialogMainHolder.post {
            dialogButton.translationY = dialogButton.height + convertToDP(context, 24f)
            dialogMainHolder.translationY = dialogMainHolder.height + dialogButton.height + convertToDP(context, 40f)
            dialogMainHolder.visibility = View.VISIBLE

            viewObjectAnimator(dialogMainHolder, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
            viewObjectAnimator(dialogButton, "translationY", 0f, 700, 200, DecelerateInterpolator(3f))
        }
    }

    fun oneButtonHider(context: Context) {
        val dialogMainHolder = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogMainHolder)
        val dialogButton = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogButton1)

        viewObjectAnimator(dialogButton, "translationY", dialogButton.height + convertToDP(context, 24f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(dialogMainHolder, "translationY", dialogMainHolder.height + dialogButton.height + convertToDP(context, 40f), 700, 100, DecelerateInterpolator(3f))

        Handler(Looper.getMainLooper()).postDelayed({
            dialogOneButton.dismiss()
        }, 800)
    }
}