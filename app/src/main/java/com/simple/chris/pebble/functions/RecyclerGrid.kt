package com.simple.chris.pebble.functions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.activities.GradientDetails
import com.simple.chris.pebble.adapters_helpers.GradientRecyclerView
import com.simple.chris.pebble.adapters_helpers.LockableBottomSheet
import com.simple.chris.pebble.functions.RecyclerGrid.gradientGrid
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_long_press_gradients.*
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.module_browse_normal.view.*
import kotlin.Exception

object RecyclerGrid {

    lateinit var gradientPopup: Dialog

    fun gradientGrid(context: Context, view: RecyclerView, gradientJSON: ArrayList<HashMap<String, String>>, onGradientListener: GradientRecyclerView.OnGradientListener, onGradientLongClickListener: GradientRecyclerView.OnGradientLongClickListener) {
        try {
            val gridLayoutManager = GridLayoutManager(context, 2)
            val gridLayoutAdapter = GradientRecyclerView(context, gradientJSON, onGradientListener, onGradientLongClickListener)
            //gridLayoutAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            view.setHasFixedSize(true)
            view.layoutManager = gridLayoutManager
            view.adapter = gridLayoutAdapter

            var x: Int
            var y = 0
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Values.browseRecyclerScrollPos = dy
                    x = y
                    y = gridLayoutManager.findFirstCompletelyVisibleItemPosition()

                    Log.e("INFO", "$x : $y")
                    //Log.e("INFO", "${gridLayoutManager.findFirstCompletelyVisibleItemPosition()}")
                    //Values.recyclerBrowseAtTop = gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
                    //Toast.makeText(context, "$dy", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid: ${e.localizedMessage}")
        }
    }

    fun gradientGridOnClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, view: View, position: Int) {
        try {
            val details = Intent(context, GradientDetails::class.java)
            val gradientInfo = gradientJSON[position]

            details.putExtra("gradientName", gradientInfo["gradientName"])
            details.putExtra("gradientColours", gradientInfo["gradientColours"])
            details.putExtra("gradientDescription", gradientInfo["gradientDescription"])

            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view.findViewById(R.id.gradient), gradientInfo["gradientName"] as String)
            context.startActivity(details, activityOptions.toBundle())
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_click_listener: ${e.localizedMessage}")

        }
    }

    fun gradientGridOnLongClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, position: Int, window: Window?, view: View) {
        val gradientPos = IntArray(2)
        view.gradient.getLocationOnScreen(gradientPos)
        /**
         * Checks if gradientPopup is visible; hides if it is
         */
        try {
            if (gradientPopup.isShowing) {
                gradientPopup.dismiss()
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_long_click_listener: ${e.localizedMessage}")
        }

        /** Creates gradientPopup **/
        gradientPopup = Dialog(context, R.style.dialogStyle)
        gradientPopup.setCancelable(false)
        gradientPopup.setContentView(R.layout.dialog_long_press_gradients)
        if (window != null) {
            gradientPopup.window?.setLayout(Calculations.screenMeasure(context, "width", window), Calculations.screenMeasure(context, "height", window))
            gradientPopup.window?.decorView?.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        val dialogWindow: Window = gradientPopup.window!!
        //dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0.1f)
        dialogWindow.setGravity(Gravity.CENTER)

        /** Set gradientPopup layout **/
        val dialogMain = gradientPopup.holder as ConstraintLayout
        val gradientView = gradientPopup.gradientPreview as ImageView
        val gradientName = gradientPopup.gradientDialogGradientName as TextView
        val gradientDescription = gradientPopup.gradientDialogGradientDescription as TextView
        val drawCaller = gradientPopup.drawCaller as ImageView

        Toast.makeText(context, "${gradientPos[0]} : ${gradientPos[1]}", Toast.LENGTH_SHORT).show()
        UIElements.viewObjectAnimator(dialogMain, "translationX", gradientPos[0].toFloat(), 0, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(dialogMain, "translationY", gradientPos[1].toFloat(), 0, 0, LinearInterpolator())
        val gradientInfo = gradientJSON[position]
        try {
            val gradientColours = gradientInfo["gradientColours"]!!.replace("[", "").replace("]", "").split(",").map { it.trim() }
            val nl = ArrayList<String>(gradientColours)
            UIElement.gradientDrawableNew(context, gradientView, nl, Values.gradientCornerRadius - 5f)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.adapter_helpers.recycler_grid.gradient_grid_on_long_click_listener: ${e.localizedMessage}")
            gradientPopup.cancel()
        }

        gradientName.text = gradientInfo["gradientName"]
        gradientDescription.text = gradientInfo["gradientDescription"]

        gradientPopup.show()

        /** Animate gradientPopup in **/
        dialogMain.post {
            UIElements.viewObjectAnimator(dialogMain, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogMain, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogMain, "alpha", 1f, 100, 100, LinearInterpolator())
            UIElements.viewObjectAnimator(drawCaller, "scaleY", 2f, 60000, 0, LinearInterpolator())
        }

        /** Create blurView **/
        if (window?.decorView != null && Values.settingsSpecialEffects) {
            try {
                val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = window.decorView.background

                gradientPopup.blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(context))
                        .setBlurRadius(20f)
                        .setHasFixedTransformationMatrix(false)
                        .setBlurAutoUpdate(true)
                        .setOverlayColor(Color.parseColor(Values.dialogBackgroundTint))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_long_click_listener: ${e.localizedMessage}")
            }
        } else {
            val backgroundDimmer = gradientPopup.backgroundDimmer
            backgroundDimmer.alpha = Values.dialogBackgroundDimmer
        }

        gradientPopup.blurView.setOnClickListener {
            UIElements.viewObjectAnimator(dialogMain, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "alpha", 0f, 200, 200, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                gradientPopup.dismiss()
            }, 450)
        }
    }
}