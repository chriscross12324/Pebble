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
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import java.io.Serializable

class GradientLongClickDialog : DialogFragment() {

    private lateinit var blurView: BlurView
    private lateinit var backgroundDim: ImageView
    private lateinit var drawCaller: ImageView
    private lateinit var dialogHolder: ConstraintLayout
    private lateinit var gradientPreview: ImageView
    private lateinit var gradientName: TextView
    private lateinit var gradientDescription: TextView

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

        blurView = view.findViewById(R.id.blurView)
        backgroundDim = view.findViewById(R.id.backgroundDimmer)
        dialogHolder = view.findViewById(R.id.holder)
        gradientPreview = view.findViewById(R.id.gradientPreview)
        gradientName = view.findViewById(R.id.gradientDialogGradientName)
        gradientDescription = view.findViewById(R.id.gradientDialogGradientDescription)
        drawCaller = view.findViewById(R.id.drawCaller)

        if (Values.settingsSpecialEffects) {
            try {
                val rootView = activity!!.window.decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = activity!!.window.decorView.background

                blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(activity))
                        .setBlurRadius(20f)
                        .setOverlayColor(Color.parseColor("#33000000"))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.gradient_long_click_dialog: ${e.localizedMessage}")
            }
        } else {
            backgroundDim.alpha = 0.75f
        }

        UIElement.gradientDrawableNew(activity as Context, gradientPreview, arguments!!.getStringArrayList("array")!!, 20f)
        gradientName.text = arguments!!.getString("name")
        gradientDescription.text = arguments!!.getString("desc")

        dialogHolder.post {
            UIElements.viewObjectAnimator(dialogHolder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogHolder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogHolder, "alpha", 1f, 100, 100, LinearInterpolator())
            UIElements.viewObjectAnimator(drawCaller, "scaleY", 2f, 60000, 0, LinearInterpolator())
        }

        blurView.setOnClickListener {
            UIElements.viewObjectAnimator(dialogHolder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogHolder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogHolder, "alpha", 0f, 200, 200, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                dialog?.dismiss()
            }, 450)
        }
    }

    companion object {
        fun newDialog(arrayList: ArrayList<String>, name: String, desc: String): GradientLongClickDialog {
            val frag = GradientLongClickDialog()
            val args = Bundle()
            args.putSerializable("array", arrayList as Serializable)
            args.putString("name", name)
            args.putString("desc", desc)
            frag.arguments = args

            return frag
        }
    }

}