package com.simple.chris.pebble.functions

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.*
import android.view.animation.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jgabrielfreitas.core.BlurImageView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.Calculations.convertToDP
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
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
            if (view != null) {
                try {
                    val objectAnimator = ObjectAnimator.ofFloat(view, propertyName, endValue)
                    objectAnimator.duration = duration
                    objectAnimator.interpolator = interpolator
                    objectAnimator.start()
                } catch (e: Exception) {
                    Log.e("ERR", "pebble.ui_elements.view_object_animator: ${e.localizedMessage}")
                }
            } else {
                Log.e("ERR", "Critical Error Caught: viewObjectAnimator - @view == null")
            }
        }, delay)
    }

    fun viewWidthAnimator(layout: View, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
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
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.view_width_animator: ${e.localizedMessage}")
            }
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

    fun cardViewCornerRadiusAnimator(layout: CardView, endRadius: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofFloat(layout.radius, endRadius)
            valueAnimator.addUpdateListener {
                val current = it.animatedValue as Float
                layout.radius = current
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
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

    @SuppressLint("MissingPermission") //[Dec 9, 2022] - Suppressing as I no longer wish to make large changes, and the try/catch will lazily handle any issues
    fun setWallpaper(context: Context, imageView: BlurImageView, alphaLayer: ImageView, window: Window) {
        if (Permissions.readStoragePermissionGiven(context)) {
            if (Values.settingSpecialEffects) {
                try {
                    val wallpaper: WallpaperManager = WallpaperManager.getInstance(context)
                    val wallpaperDrawable: Drawable = wallpaper.drawable!!
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
        } else {
            Log.d("MSG", "android.permission.READ_EXTERNAL_STORAGE not granted")
        }
    }

    fun setImageViewSRC(view: ImageView, src: Int, duration: Long, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            val animOut = AlphaAnimation(1.0f, 0.0f)
            animOut.duration = duration
            val animIn = AlphaAnimation(0.0f, 1.0f)
            animIn.duration = duration

            view.startAnimation(animOut)
            animOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    view.setImageResource(src)
                    view.startAnimation(animIn)
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            })
        }, delay)
    }

    fun setTextViewText(view: TextView, text: Int, duration: Long, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            val animOut = AlphaAnimation(1.0f, 0.0f)
            animOut.duration = duration
            val animIn = AlphaAnimation(0.0f, 1.0f)
            animIn.duration = duration

            view.startAnimation(animOut)
            animOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    view.setText(text)
                    view.startAnimation(animIn)
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            })
        }, delay)
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

    fun saveGradientDialog(context: Context, colourArray: ArrayList<String>, window: Window) {

        /** Creates popupDialog **/
        val saveGradientDialog = Dialog(context, R.style.dialogStyle)
        saveGradientDialog.setCancelable(false)
        saveGradientDialog.setContentView(R.layout.dialog_save_gradient)

        val dialogWindow: Window = saveGradientDialog.window!!
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0.1f)
        dialogWindow.setGravity(Gravity.CENTER)

        /** Set popupDialog layout **/
        val dialogMain = saveGradientDialog.findViewById<CardView>(R.id.popupHolder)
        val gradientPreview = saveGradientDialog.findViewById<ImageView>(R.id.gradientPreview)
        val heightText = saveGradientDialog.findViewById<EditText>(R.id.heightText)
        val widthText = saveGradientDialog.findViewById<EditText>(R.id.widthText)
        val presetButton = saveGradientDialog.findViewById<LinearLayout>(R.id.presetButton)
        val saveButton = saveGradientDialog.findViewById<ConstraintLayout>(R.id.saveGradientButton)
        val backgroundDimmer = saveGradientDialog.findViewById<ImageView>(R.id.backgroundDimmer)

        /** Set UI Elements **/
        UIElement.gradientDrawableNew(context, gradientPreview, colourArray, 20f)
        //heightText.setText(Calculations.screenMeasure(context, "height", window))
        //widthText.setText(Calculations.screenMeasure(context, "width", window))

        saveButton.setOnClickListener {

        }

        saveGradientDialog.show()

        presetButton.setOnClickListener {
            heightText.setText(Calculations.screenMeasure(context, "height", window))
            widthText.setText(Calculations.screenMeasure(context, "width", window))
        }

        /** Animate popupLayout in **/
        dialogMain.post {
            viewObjectAnimator(dialogMain, "scaleX", 1f, 350, 100, OvershootInterpolator())
            viewObjectAnimator(dialogMain, "scaleY", 1f, 350, 100, OvershootInterpolator())
            viewObjectAnimator(dialogMain, "alpha", 1f, 150, 100, LinearInterpolator())
        }

        /** Dismisses Dialog **/
        backgroundDimmer.setOnClickListener {
            try {
                viewObjectAnimator(dialogMain, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                viewObjectAnimator(dialogMain, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                viewObjectAnimator(dialogMain, "alpha", 0f, 150, 200, LinearInterpolator())
                Handler(Looper.getMainLooper()).postDelayed({
                    saveGradientDialog.dismiss()
                }, 450)
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.colour_dialog: ${e.localizedMessage}")
            }
        }

        /** Create blurView **/
        if (Values.settingSpecialEffects) {
            try {
                val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = window.decorView.background

                saveGradientDialog.findViewById<BlurView>(R.id.blurView).setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(context))
                        .setBlurRadius(20f)
                        .setHasFixedTransformationMatrix(true)
                        .setOverlayColor(Color.parseColor(Values.dialogBackgroundTint))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
            val backgroundDimmer = saveGradientDialog.findViewById<ImageView>(R.id.backgroundDimmer)
            backgroundDimmer.alpha = Values.dialogBackgroundDim
        } else {
            val backgroundDimmer = saveGradientDialog.findViewById<ImageView>(R.id.backgroundDimmer)
            backgroundDimmer.alpha = Values.dialogBackgroundDim
        }
    }

    fun colourDrawable(context: Context, hexString: String, cornerRadius: Float): Drawable {
        val colourDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.parseColor(hexString), Color.parseColor(hexString))
        )
        colourDrawable.cornerRadius = convertToDP(context, cornerRadius)

        return colourDrawable
    }
}