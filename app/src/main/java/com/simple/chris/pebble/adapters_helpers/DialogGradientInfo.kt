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
import android.view.animation.*
import android.widget.Toast
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.Calculations
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_long_press_gradients.*
import kotlinx.android.synthetic.main.dialog_long_press_gradients.backgroundDimmer
import kotlinx.android.synthetic.main.dialog_long_press_gradients.blurView
import kotlinx.android.synthetic.main.dialog_long_press_gradients.drawCaller
import kotlinx.android.synthetic.main.dialog_long_press_gradients.holder
import kotlinx.android.synthetic.main.dialog_popup.*
import kotlinx.android.synthetic.main.module_browse_normal.view.*
import java.io.Serializable

class DialogGradientInfo : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_long_press_gradients, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.window!!.setDimAmount(0f)
        val gradientPos = arguments!!.getIntArray("viewPos")!!

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
                Log.e("ERR", "pebble.gradient_long_click_dialog: ${e.localizedMessage}")
            }
        } else {
            backgroundDimmer.alpha = Values.dialogBackgroundDimmer
        }

        UIElement.gradientDrawableNew(activity as Context, gradientPreview, arguments!!.getStringArrayList("array")!!, Values.gradientCornerRadius)
        gradientDialogGradientName.text = arguments!!.getString("name")
        gradientDialogGradientDescription.text = arguments!!.getString("desc")

        holder.post {
            UIElements.viewObjectAnimator(holder, "translationX", gradientPos[0].toFloat() - Calculations.convertToDP(this.context!!, 20f), 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(holder, "translationY", gradientPos[1].toFloat() - Calculations.convertToDP(this.context!!, 20f), 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(holder, "translationX", Calculations.screenMeasure(this.context!!, "width", dialog!!.window!!)/2.toFloat() - holder.width/2, 400, 0, DecelerateInterpolator(2f))
            UIElements.viewObjectAnimator(holder, "translationY", Calculations.screenMeasure(this.context!!, "height", dialog!!.window!!)/2.toFloat() - holder.height/2, 400, 0, DecelerateInterpolator(2f))
            UIElements.viewObjectAnimator(holder, "scaleX", 1f, 0, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "scaleY", 1f, 0, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "alpha", 1f, 200, 200, LinearInterpolator())
            UIElements.viewObjectAnimator(drawCaller, "scaleY", 2f, 60000, 0, LinearInterpolator())
        }

        blurView.setOnClickListener {
            /*UIElements.viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())*/
            UIElements.viewObjectAnimator(holder, "scaleX", 1.15f, 200, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "scaleY", 1.15f, 200, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "alpha", 0f, 100, 100, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                dialog?.dismiss()
            }, 250)
        }
    }

    companion object {
        fun newDialog(arrayList: ArrayList<String>, name: String, desc: String, viewPos: IntArray): DialogGradientInfo {
            val frag = DialogGradientInfo()
            val args = Bundle()
            args.putSerializable("array", arrayList as Serializable)
            args.putString("name", name)
            args.putString("desc", desc)
            args.putIntArray("viewPos", viewPos)
            frag.arguments = args

            return frag
        }
    }

}