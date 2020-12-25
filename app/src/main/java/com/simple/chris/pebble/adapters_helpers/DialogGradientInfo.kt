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
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_long_press_gradients.*
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

        UIElement.gradientDrawableNew(activity as Context, gradientPreview, arguments!!.getStringArrayList("array")!!, 20f)
        gradientDialogGradientName.text = arguments!!.getString("name")
        gradientDialogGradientDescription.text = arguments!!.getString("desc")

        holder.post {
            UIElements.viewObjectAnimator(holder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "alpha", 1f, 100, 100, LinearInterpolator())
            UIElements.viewObjectAnimator(drawCaller, "scaleY", 2f, 60000, 0, LinearInterpolator())
        }

        blurView.setOnClickListener {
            UIElements.viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                dialog?.dismiss()
            }, 450)
        }
    }

    companion object {
        fun newDialog(arrayList: ArrayList<String>, name: String, desc: String): DialogGradientInfo {
            val frag = DialogGradientInfo()
            val args = Bundle()
            args.putSerializable("array", arrayList as Serializable)
            args.putString("name", name)
            args.putString("desc", desc)
            frag.arguments = args

            return frag
        }
    }

}