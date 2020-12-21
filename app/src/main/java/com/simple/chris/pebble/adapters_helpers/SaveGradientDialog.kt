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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_gradient_screen_save.*

class SaveGradientDialog : DialogFragment() {

    private lateinit var blurView: BlurView
    private lateinit var drawCaller: ImageView
    private lateinit var dialogHolder: ConstraintLayout
    private lateinit var gradientPreview: ImageView
    private lateinit var heightText: EditText
    private lateinit var widthText: EditText
    private lateinit var presetButton: LinearLayout
    private lateinit var saveButton: ConstraintLayout

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.dialog_gradient_screen_save, container)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.window!!.setDimAmount(0f)

        blurView = view.findViewById(R.id.blurView)

        gradientPreview = view.findViewById(R.id.gradientPreview)
        drawCaller = view.findViewById(R.id.drawCaller)
        dialogHolder = view.findViewById(R.id.holder)
        heightText = view.findViewById(R.id.heightText)
        widthText = view.findViewById(R.id.widthText)
        presetButton = view.findViewById(R.id.presetButton)
        saveButton = view.findViewById(R.id.saveGradientButton)

        UIElement.gradientDrawableNew(activity as Context, gradientPreview, arguments!!.getStringArrayList("array")!!, 20f)

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
                Log.e("ERR", "pebble.save_gradient_dialog: ${e.localizedMessage}")
            }
        }
        dialogHolder.post {
            UIElements.viewObjectAnimator(dialogHolder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogHolder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(dialogHolder, "alpha", 1f, 100, 100, LinearInterpolator())
            UIElements.viewObjectAnimator(drawCaller, "scaleY", 2f, 2000, 0, LinearInterpolator())
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
        fun newInstance(arrayList: ArrayList<String>): SaveGradientDialog {
            val frag = SaveGradientDialog()
            val args = Bundle()
            args.putStringArrayList("array", arrayList)
            frag.arguments = args

            return frag
        }
    }

}