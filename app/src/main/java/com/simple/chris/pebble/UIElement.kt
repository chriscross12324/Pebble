package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.jgabrielfreitas.core.BlurImageView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_popup.*

object UIElement {

    lateinit var popupDialog: Dialog

    fun setTheme(context: Context) {
        when (Values.settingThemes) {
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "black" -> context.setTheme(R.style.ThemeBlack)
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

    fun popupDialog(context: Context, popupName: String, icon: Int?, title: Int?, titleString: String?, description: Int,
                    buttonArrayList: ArrayList<HashMap<String, Int>>?, decorView: View?, listener: PopupDialogButtonRecyclerAdapter.OnButtonListener?) {
        /**
         * Checks if popUpDialog is visible; hides it if it does
         */
        try {
            if (popupDialog.isShowing) {
                popupDialog.dismiss()
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.ui_element.popup_dialog: ${e.localizedMessage}")
        }

        /** Creates popupDialog **/
        popupDialog = Dialog(context, R.style.dialogStyle)
        popupDialog.setCancelable(false)
        popupDialog.setContentView(R.layout.dialog_popup)

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
                val adapter = PopupDialogButtonRecyclerAdapter(context, popupName, buttonArrayList, listener)

                dialogRecycler.layoutManager = layoutManager
                dialogRecycler.adapter = adapter
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
        }

        popupDialog.show()

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
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
        } else {
            val backgroundDimmer = popupDialog.backgroundDimmer
            backgroundDimmer.alpha = 0.75f
        }
    }

    fun popupDialogHider() {
        /**
         * Checks if popupDialog is visible
         */
        try {
            if (popupDialog.isShowing) {
                val dialogMain = popupDialog.holder
                val dialogRecycler = popupDialog.popupButtonRecycler

                UIElements.viewObjectAnimator(dialogMain, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogMain, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogMain, "alpha", 0f, 150, 200, LinearInterpolator())

                UIElements.viewObjectAnimator(dialogRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

                Handler().postDelayed({
                      popupDialog.dismiss()
                }, 450)
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.ui_elements.popup_dialog_hider: ${e.localizedMessage}")
        }
    }

    fun hideSoftKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus as View
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}