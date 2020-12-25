package com.simple.chris.pebble.adapters_helpers

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.Calculations
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_save_gradient.*

class DialogSaveGradient : DialogFragment() {

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_save_gradient, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.window!!.setDimAmount(0f)

        UIElement.gradientDrawableNew(activity as Context, gradientPreview, arguments!!.getStringArrayList("array")!!, 20f)
        heightText.setText(Calculations.screenMeasure(activity as Context, "height", activity!!.window).toString())
        widthText.setText(Calculations.screenMeasure(activity as Context, "width", activity!!.window).toString())

        if (Values.settingsSpecialEffects) {
            try {
                val rootView = activity!!.window.decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = activity!!.window.decorView.background

                blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(activity))
                        .setBlurRadius(20f)
                        .setOverlayColor(Color.parseColor(Values.dialogBackgroundTint))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.save_gradient_dialog: ${e.localizedMessage}")
            }
        } else {
            backgroundDimmer.alpha = Values.dialogBackgroundDimmer
        }

        holder.post {
            UIElements.viewObjectAnimator(holder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "alpha", 1f, 100, 100, LinearInterpolator())
            UIElements.viewObjectAnimator(drawCaller, "scaleY", 2f, 2000, 0, LinearInterpolator())
        }

        blurView.setOnClickListener {
            UIElements.viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                dialog?.dismiss()
            }, 450)
        }

        presetButton.setOnClickListener {
            val height = Calculations.screenMeasure(activity as Context, "height", activity!!.window)
            val width = Calculations.screenMeasure(activity as Context, "width", activity!!.window)

            heightText.setText(height.toString())
            widthText.setText(width.toString())
        }

    }

    companion object {
        fun newInstance(arrayList: ArrayList<String>): DialogSaveGradient {
            val frag = DialogSaveGradient()
            val args = Bundle()
            args.putStringArrayList("array", arrayList)
            frag.arguments = args

            return frag
        }
    }

}