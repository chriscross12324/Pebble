package com.simple.chris.pebble

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.Calculations.convertToDP
import io.alterac.blurkit.BlurLayout
import java.io.File
import kotlin.math.roundToInt

object UIElements {

    lateinit var dialogOneButton: Dialog

    fun setTheme(context: Context) {
        when (Values.theme) {
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "black" -> context.setTheme(R.style.ThemeBlack)
        }
    }

    @SuppressLint("NewApi")
    fun gradientDrawable(context: Context, setDrawable: Boolean, view: View?, startColour: Int, endColour: Int, cornerRadius: Float): Drawable? {
        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColour, endColour)
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(context, cornerRadius)

        when (setDrawable) {
            true -> {
                view!!.background = gradientDrawable
                if (Calculations.isAndroidPOrGreater()) {
                    view!!.outlineSpotShadowColor = endColour
                }
            }
            false -> {
                return gradientDrawable
            }
        }
        return null
    }

    fun viewVisibility(view: View, visibility: Int, delay: Long) {
        Handler().postDelayed({
            view.visibility = visibility
        }, delay)
    }

    fun viewObjectAnimator(view: View, propertyName: String, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(view, propertyName, endValue)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun constraintLayoutHeightAnimator(layout: ConstraintLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
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

    fun constraintLayoutElevationAnimator(layout: ConstraintLayout, startElevation: Float, endElevation: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
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

    fun linearLayoutHeightAnimator(layout: LinearLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
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
        Handler().postDelayed({
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
        Handler().postDelayed({
            layout.text = text
        }, delay)
    }

    fun constraintElevationColour(layout: ConstraintLayout, colour: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layout.outlineSpotShadowColor = colour
        }
    }

    fun bottomSheetPeekHeightAnim(bottomSheet: BottomSheetBehavior<CardView>, peekHeight: Int, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
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

        Handler().postDelayed({
            dialogOneButton.dismiss()
        }, 800)
    }

    fun popupDialog(context: Context, icon: Int, title: Int, description: Int, buttonText: Int?, blur: BlurLayout, listener: View.OnClickListener) {
        blur.visibility = View.VISIBLE
        blur.startBlur()
        blur.invalidate()

        val dialog = Dialog(context, R.style.dialogStyle)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_popup)

        val dismiss = dialog.findViewById<ImageView>(R.id.dismiss)
        val holder = dialog.findViewById<ConstraintLayout>(R.id.holder)
        val permissionIcon = dialog.findViewById<ImageView>(R.id.permissionIcon)
        val permissionTitle = dialog.findViewById<TextView>(R.id.permissionTitle)
        val permissionDescription = dialog.findViewById<TextView>(R.id.permissionDescription)
        val button = dialog.findViewById<ConstraintLayout>(R.id.button)
        val buttonTextView = dialog.findViewById<TextView>(R.id.text)

        permissionIcon.setImageResource(icon)
        permissionTitle.setText(title)
        permissionDescription.setText(description)

        val dialogWindow = dialog.window
        dialogWindow!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0f)
        dialogWindow.setGravity(Gravity.CENTER)
        dialog.show()

        holder.post {
            viewObjectAnimator(holder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            viewObjectAnimator(holder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            viewObjectAnimator(holder, "alpha", 1f, 100, 100, LinearInterpolator())

            if (buttonText != null) {
                viewObjectAnimator(button, "scaleX", 1f, 350, 100, OvershootInterpolator())
                viewObjectAnimator(button, "scaleY", 1f, 350, 100, OvershootInterpolator())
                viewObjectAnimator(button, "alpha", 1f, 100, 100, LinearInterpolator())
                buttonTextView.setText(buttonText)
            }

        }

        dismiss.setOnClickListener {
            viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

            viewObjectAnimator(button, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            viewObjectAnimator(button, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            viewObjectAnimator(button, "alpha", 0f, 200, 200, LinearInterpolator())

            Handler().postDelayed({
                dialog.dismiss()
                blur.pauseBlur()
                blur.visibility = View.GONE
            }, 450)
        }

        /*button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(dir), "folder")

            if (intent.resolveActivityInfo(context.packageManager, 0) != null) {
                context.startActivity(intent)
            } else {
                Log.e("ERR", "No file explorer")
                context.startActivity(intent)
            }
        }*/
        button.setOnClickListener(listener)
    }

}