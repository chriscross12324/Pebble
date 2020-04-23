package com.simple.chris.pebble

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.alterac.blurkit.BlurLayout
import kotlinx.android.synthetic.main.activity_browse.*
import java.lang.Exception

object RecyclerGrid {

    fun gradientGrid(context: Context, view: RecyclerView, gradientJSON: ArrayList<HashMap<String, String>>, onGradientListener: GradientRecyclerViewAdapter.OnGradientListener, onGradientLongClickListener: GradientRecyclerViewAdapter.OnGradientLongClickListener) {
        try {
            val gridLayoutManager = GridLayoutManager(context, 2)
            val gridLayoutAdapter = GradientRecyclerViewAdapter(context, gradientJSON, onGradientListener, onGradientLongClickListener)
            view.setHasFixedSize(true)
            view.layoutManager = gridLayoutManager
            view.adapter = gridLayoutAdapter
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid: ${e.localizedMessage}")

        }
    }

    fun gradientGridOnClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, view: View, position: Int) {
        try {
            val details = Intent(context, ActivityGradientDetails::class.java)
            val gradientInfo = gradientJSON[position]

            details.putExtra("gradientName", gradientInfo["backgroundName"])
            details.putExtra("startColour", gradientInfo["startColour"])
            details.putExtra("endColour", gradientInfo["endColour"])
            details.putExtra("description", gradientInfo["description"])

            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view.findViewById(R.id.gradient), gradientInfo["backgroundName"] as String)
            context.startActivity(details, activityOptions.toBundle())
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_click_listener: ${e.localizedMessage}")

        }
    }

    fun gradientGridOnLongClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, position: Int, blur: BlurLayout) {
        try {
            blur.visibility = View.VISIBLE
            blur.startBlur()
            blur.invalidate()

            val dialog = Dialog(context, R.style.dialogStyle)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_long_press_gradients)

            val dismissDialog = dialog.findViewById<ImageView>(R.id.dismissDialog)
            val holder = dialog.findViewById<ConstraintLayout>(R.id.holder)
            val gradientView = dialog.findViewById<ImageView>(R.id.gradientPreview)
            val gradientName = dialog.findViewById<TextView>(R.id.gradientDialogGradientName)
            val gradientDescription = dialog.findViewById<TextView>(R.id.gradientDialogGradientDescription)

            val gradientInfo = gradientJSON[position]
            UIElements.gradientDrawable(context, true, gradientView, Color.parseColor(gradientInfo["startColour"]), Color.parseColor(gradientInfo["endColour"]), 15f)
            gradientName.text = gradientInfo["backgroundName"]
            gradientDescription.text = gradientInfo["description"]

            val dialogWindow = dialog.window
            dialogWindow!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            dialogWindow.setDimAmount(0f)
            dialogWindow.setGravity(Gravity.CENTER)
            dialog.show()

            holder.post {
                UIElements.viewObjectAnimator(holder, "scaleX", 1f, 350, 100, OvershootInterpolator())
                UIElements.viewObjectAnimator(holder, "scaleY", 1f, 350, 100, OvershootInterpolator())
                UIElements.viewObjectAnimator(holder, "alpha", 1f, 100, 100, LinearInterpolator())
            }

            dismissDialog.setOnClickListener {
                UIElements.viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

                Handler().postDelayed({
                    dialog.dismiss()
                    blur.pauseBlur()
                    blur.visibility = View.GONE
                }, 450)
            }

        } catch (e: Exception){
            Log.e("ERR", "pebble.recycler_grid.gradient.grid.long.click.listener: ${e.localizedMessage}")
        }
    }

}