package com.simple.chris.pebble.functions

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.jgabrielfreitas.core.BlurImageView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_one_button.*
import kotlinx.android.synthetic.main.dialog_popup.*
import java.util.concurrent.LinkedBlockingDeque
import com.sinaseyfi.advancedcardview.AdvancedCardView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

object UIElement {

    lateinit var popupDialog: Dialog
    var currentDecorView: View? = null
    var currentPopupCanBeOverwritten = true
    var dialogList = ArrayList<Dialog>()
    var dialogsToShow = LinkedBlockingDeque<Dialog>()

    fun setTheme(context: Context) {
        when (Values.settingThemes) {
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "darker" -> context.setTheme(R.style.ThemeDarker)
            "black" -> {
                context.setTheme(R.style.ThemeDarker)
                Values.settingThemes = "darker"
            }
        }
    }

    fun setWallpaper(context: Context, imageView: BlurImageView, alphaLayer: ImageView) {
        if (Permissions.readStoragePermissionGiven(context)) {
            if (Values.settingsSpecialEffects) {
                try {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    val wallpaperDrawable = wallpaperManager.drawable

                } catch (e: Exception) {

                }
            }
        }
    }

    @SuppressLint("NewApi")
    fun gradientDrawable(context: Context, view: View?, startColour: Int, endColour: Int, cornerRadius: Float): Drawable? {
        /** Create Gradient Drawable**/
        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColour, endColour)
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(context, cornerRadius)

        /** Set or return gradientDrawable **/
        if (view != null) {
            view.background = gradientDrawable
            if (Calculations.isAndroidPOrGreater()) {
                view.outlineSpotShadowColor = endColour
            }
        } else {
            return gradientDrawable
        }
        return null
    }

    @SuppressLint("NewApi")
    fun gradientDrawableNew(context: Context, view: View?, colourArray: ArrayList<String>, cornerRadius: Float): Drawable? {
        try {
            /** Create Gradient Drawable**/
            if (colourArray.size >= 2) {
                val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        Calculations.stringArraytoIntArray(colourArray)
                )
                gradientDrawable.cornerRadius = Calculations.convertToDP(context, cornerRadius)

                /** Set or return gradientDrawable **/
                if (view != null) {
                    view.background = gradientDrawable
                    if (Calculations.isAndroidPOrGreater()) {
                        view.outlineSpotShadowColor = Color.parseColor(colourArray[colourArray.size - 1])
                    }
                } else {
                    return gradientDrawable
                }
            } else {
                val colour = Color.parseColor(colourArray.toString().replace("[", "").replace("]", ""))
                val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        intArrayOf(colour, colour)
                )
                gradientDrawable.cornerRadius = Calculations.convertToDP(context, cornerRadius)
                /** Set or return gradientDrawable **/
                if (view != null) {
                    view.background = gradientDrawable
                    if (Calculations.isAndroidPOrGreater()) {
                        view.outlineSpotShadowColor = colour
                    }
                } else {
                    return gradientDrawable
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("ERR", "pebble.functions.ui_element.gradient_drawable_new: ${e.localizedMessage}")
        }


        return null
    }

    fun cardRadiusAnimator(layout: CardView, newRadius: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator = ValueAnimator.ofInt(layout.radius.roundToInt(), newRadius.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                layout.radius = value.toFloat()
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun animateViewWidth(axis: String, view: View, newValue: Int, delay: Long, duration: Long) {
        /**
         * Determines if animating height or width
         */
        Handler(Looper.getMainLooper()).postDelayed({
            val valueAnimator: ValueAnimator = if (axis == "height") {
                ValueAnimator.ofInt(view.height, newValue)
            } else {
                ValueAnimator.ofInt(view.width, newValue)
            }
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = view.layoutParams
                if (axis == "height") {
                    layoutParams.height = value
                } else if (axis == "width") {
                    layoutParams.width = value
                }
                view.layoutParams = layoutParams
            }
            valueAnimator.interpolator = DecelerateInterpolator(3f)
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun popupDialog(context: Context, popupName: String, icon: Int?, title: Int?, titleString: String?, description: Int,
                    buttonArrayList: ArrayList<HashMap<String, Int>>?, decorView: View?, listener: PopupDialogButtonRecycler.OnButtonListener?) {

        currentPopupCanBeOverwritten = true
        /** Creates popupDialog **/
        popupDialog = Dialog(context, R.style.dialogStyle)
        popupDialog.setCancelable(false)
        popupDialog.setContentView(R.layout.dialog_popup)

        val dialogWindow: Window = popupDialog.window!!
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0.1f)
        dialogWindow.setGravity(Gravity.CENTER)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        popupDialog.window?.setLayout(width, width)
        popupDialog.window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        /** Set popupDialog layout **/
        val dialogMain = popupDialog.holder
        val progressCircle = popupDialog.progressCircle
        val dialogIcon = popupDialog.permissionIcon
        val dialogTitle = popupDialog.permissionTitle
        val dialogDescription = popupDialog.permissionDescription
        val dialogRecycler = popupDialog.popupButtonRecycler
        val backgroundDimmer = popupDialog.backgroundDimmer

        if (popupName.contains("setting") && context.toString().contains("MainActivity")) {
            backgroundDimmer.setOnClickListener {
                popupDialogHider()
                Log.e("INFO", "$context")
            }
        }

        if (icon != null) {
            dialogIcon.setImageResource(icon)
        } else {
            dialogIcon.visibility = View.INVISIBLE
            progressCircle.visibility = View.VISIBLE
        }

        if (title != null) {
            dialogTitle.setText(title)
        } else {
            dialogTitle.text = titleString
        }
        dialogDescription.setText(description)

        /** Set popupLayout recycler **/
        if (buttonArrayList != null && listener != null) {
            try {
                dialogRecycler.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val adapter = PopupDialogButtonRecycler(context, popupName, buttonArrayList, listener)

                dialogRecycler.layoutManager = layoutManager
                dialogRecycler.adapter = adapter
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
        }

        dialogList.add(popupDialog)
        showPopupQueueManager(popupDialog)

        /** Animate popupLayout in **/
        dialogMain.post {
            UIElements.viewObjectAnimator(dialogMain, "scaleX", 1f, 350, 100, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "scaleY", 1f, 350, 100, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "alpha", 1f, 150, 100, LinearInterpolator())

            if (buttonArrayList != null) {
                UIElements.viewObjectAnimator(dialogRecycler, "scaleX", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "scaleY", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "alpha", 1f, 150, 100, LinearInterpolator())
            }
        }

        /** Create blurView **/
        if (decorView != null && Values.settingsSpecialEffects) {
            try {
                val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = decorView.background

                popupDialog.blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(context))
                        .setBlurRadius(20f)
                        .setHasFixedTransformationMatrix(true)
                        .setOverlayColor(Color.parseColor("#33000000"))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
            val backgroundDimmer = popupDialog.backgroundDimmer
            backgroundDimmer.alpha = 0.75f
        } else {
            val backgroundDimmer = popupDialog.backgroundDimmer
            backgroundDimmer.alpha = 0.75f
        }
    }

    fun popupDialogReformat(context: Context, popupName: String, icon: Int?, title: Int?, titleString: String?, description: Int,
                            buttonArrayList: ArrayList<HashMap<String, Int>>?, decorView: View?, listener: PopupDialogButtonRecycler.OnButtonListener?) {

        /** Creates popupDialog **/
        popupDialog = Dialog(context, R.style.dialogStyle)
        popupDialog.setCancelable(false)
        popupDialog.setContentView(R.layout.dialog_popup)

        currentDecorView = decorView

        val dialogWindow: Window = popupDialog.window!!
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0.1f)
        dialogWindow.setGravity(Gravity.CENTER)

        /** Set popupDialog layout **/
        val dialogMain = popupDialog.holder
        val progressCircle = popupDialog.progressCircle
        val dialogIcon = popupDialog.permissionIcon
        val dialogTitle = popupDialog.permissionTitle
        val dialogDescription = popupDialog.permissionDescription
        val dialogRecycler = popupDialog.popupButtonRecycler

        if (icon != null) {
            dialogIcon.setImageResource(icon)
        } else {
            dialogIcon.visibility = View.INVISIBLE
            progressCircle.visibility = View.VISIBLE
        }

        if (title != null) {
            dialogTitle.setText(title)
        } else {
            dialogTitle.text = titleString
        }
        dialogDescription.setText(description)

        /** Set popupLayout recycler **/
        if (buttonArrayList != null && listener != null) {
            try {
                dialogRecycler.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val adapter = PopupDialogButtonRecycler(context, popupName, buttonArrayList, listener)

                dialogRecycler.layoutManager = layoutManager
                dialogRecycler.adapter = adapter
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
        }

        dialogList.add(popupDialog)
        showPopupQueueManager(dialogList[0])
    }

    fun popupDialogHider() {
        /**
         * Checks if popupDialog is visible
         */
        try {
            if (dialogList[0].isShowing) {
                val dialogMain = popupDialog.holder
                val dialogRecycler = popupDialog.popupButtonRecycler

                UIElements.viewObjectAnimator(dialogMain, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogMain, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogMain, "alpha", 0f, 150, 200, LinearInterpolator())

                UIElements.viewObjectAnimator(dialogRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

                Handler(Looper.getMainLooper()).postDelayed({
                    if (dialogList.size > 0) {
                        hidePopupQueueManager(dialogList[0])
                    }

                }, 450)
            } else {
                hidePopupQueueManager(popupDialog)
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.ui_element.popup_dialog_hider: ${e.localizedMessage}")
        }
    }

    fun killDialogs() {
        if (dialogList.isNotEmpty()) {
            hidePopupQueueManager(dialogList[0])
            dialogList.clear()
        }
    }

    fun showPopupQueueManager(dialog: Dialog) {
        try {
            if (dialogsToShow.isEmpty()) {
                dialog.show()
                //showDialog(dialog)
            }
            dialogsToShow.offer(dialog)
        } catch (e: java.lang.Exception) {
            Log.e("ERR", "pebble.functions.ui_elements.show_popup_queue_manager: ${e.localizedMessage}")
        }

    }

    private fun hidePopupQueueManager(dialog: Dialog) {
        try {
            dialogsToShow.remove(dialog)
            dialogList.removeAt(0)
            dialog.dismiss()
            if (dialogsToShow.isNotEmpty()) {
                dialogsToShow.peek()!!.show()
            }
        } catch (e: java.lang.Exception) {
            Log.e("ERR", "pebble.ui_elements.hide_popup_queue_manager: ${e.localizedMessage}")
        }
    }

    fun showDialog(dialog: Dialog) {

        /** Animate popupLayout in **/
        /*dialogMain.post {
            UIElements.viewObjectAnimator(dialogMain, "scaleX", 1f, 350, 100, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "scaleY", 1f, 350, 100, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogRecycler, "alpha", 1f, 150, 100, LinearInterpolator())

            if (dialogRecycler.adapter != null) {
                UIElements.viewObjectAnimator(dialogRecycler, "scaleX", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "scaleY", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "alpha", 1f, 150, 100, LinearInterpolator())
            }
        }*/

        /** Create blurView **/
        if (currentDecorView != null && Values.settingsSpecialEffects && 1 == 2) {
            try {
                //val rootView = currentDecorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = currentDecorView!!.background

//                popupDialog.blurView.setupWith(rootView)
//                        .setFrameClearDrawable(windowBackground)
//                        .setBlurAlgorithm(RenderScriptBlur(context))
//                        .setBlurRadius(20f)
//                        .setHasFixedTransformationMatrix(true)
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
        } else {
            val backgroundDimmer = popupDialog.backgroundDimmer
            backgroundDimmer.alpha = 0.75f
        }
    }

    fun hideSoftKeyboard(activity: Activity) {
        try {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = activity.currentFocus as View
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.ui_element.hide_soft_keyboard: ${e.localizedMessage}")
        }

    }

    fun startActivityFade(context: Context, activity: Activity, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            context.startActivity(Intent(context, activity::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, delay)
    }

}