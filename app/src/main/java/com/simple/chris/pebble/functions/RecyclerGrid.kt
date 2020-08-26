package com.simple.chris.pebble.functions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.activities.GradientDetailsActivity
import com.simple.chris.pebble.adapters.GradientRecyclerView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_long_press_gradients.*
import kotlin.Exception

object RecyclerGrid {

    lateinit var gradientPopup: Dialog

    fun gradientGrid(context: Context, view: RecyclerView, gradientJSON: ArrayList<HashMap<String, String>>, onGradientListener: GradientRecyclerView.OnGradientListener, onGradientLongClickListener: GradientRecyclerView.OnGradientLongClickListener) {
        try {
            val gridLayoutManager = GridLayoutManager(context, 2)
            val gridLayoutAdapter = GradientRecyclerView(context, gradientJSON, onGradientListener, onGradientLongClickListener)
            view.setHasFixedSize(true)
            view.layoutManager = gridLayoutManager
            view.adapter = gridLayoutAdapter
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid: ${e.localizedMessage}")

        }
    }

    fun gradientGridOnClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, view: View, position: Int) {
        try {
            val details = Intent(context, GradientDetailsActivity::class.java)
            val gradientInfo = gradientJSON[position]

            details.putExtra("gradientName", gradientInfo["gradientName"])
            details.putExtra("startColour", gradientInfo["startColour"])
            details.putExtra("endColour", gradientInfo["endColour"])
            details.putExtra("description", gradientInfo["description"])

            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view.findViewById(R.id.gradient), gradientInfo["gradientName"] as String)
            context.startActivity(details, activityOptions.toBundle())
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_click_listener: ${e.localizedMessage}")

        }
    }

    fun gradientGridOnLongClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, position: Int, decorView: View?) {
        /**
         * Checks if gradientPopup is visible; hides if it is
         */
        /*try {
            if (gradientPopup.isShowing) {
                gradientPopup.dismiss()
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_long_click_listener: ${e.localizedMessage}")
        }*/

        /** Creates gradientPopup **/
        gradientPopup = Dialog(context, R.style.dialogStyle)
        gradientPopup.setCancelable(false)
        gradientPopup.setContentView(R.layout.dialog_long_press_gradients)

        val dialogWindow: Window = gradientPopup.window!!
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0.1f)
        dialogWindow.setGravity(Gravity.CENTER)

        /** Set gradientPopup layout **/
        val dialogMain = gradientPopup.holder as ConstraintLayout
        val gradientView = gradientPopup.gradientPreview as ImageView
        val gradientName = gradientPopup.gradientDialogGradientName as TextView
        val gradientDescription = gradientPopup.gradientDialogGradientDescription as TextView

        val gradientInfo = gradientJSON[position]
        UIElement.gradientDrawable(context, gradientView, Color.parseColor(gradientInfo["startColour"]), Color.parseColor(gradientInfo["endColour"]), Values.gradientCornerRadius - 5f)
        gradientName.text = gradientInfo["gradientName"]
        gradientDescription.text = gradientInfo["description"]

        gradientPopup.show()

        /** Animate gradientPopup in **/
        dialogMain.post {
            UIElements.viewObjectAnimator(dialogMain, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogMain, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogMain, "alpha", 1f, 100, 100, LinearInterpolator())
        }

        /** Create blurView **/
        if (decorView != null && Values.settingsSpecialEffects) {
            try {
                val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = decorView.background

                gradientPopup.blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(context))
                        .setBlurRadius(20f)
                        .setHasFixedTransformationMatrix(true)
            } catch (e: Exception) {
                Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_long_click_listener: ${e.localizedMessage}")
            }
        } else {
            val backgroundDimmer = gradientPopup.backgroundDimmer
            backgroundDimmer.alpha = 0.75f
        }

        gradientPopup.blurView.setOnClickListener {
            UIElements.viewObjectAnimator(dialogMain, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "alpha", 0f, 200, 200, LinearInterpolator())

            Handler().postDelayed({
                gradientPopup.dismiss()
            }, 450)
        }
    }
}