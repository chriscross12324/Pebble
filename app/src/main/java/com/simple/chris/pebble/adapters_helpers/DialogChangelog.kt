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
import kotlinx.android.synthetic.main.dialog_changelog.*
import kotlinx.android.synthetic.main.dialog_long_press_gradients.*
import kotlinx.android.synthetic.main.dialog_long_press_gradients.backgroundDimmer
import kotlinx.android.synthetic.main.dialog_long_press_gradients.blurView
import kotlinx.android.synthetic.main.dialog_long_press_gradients.drawCaller
import kotlinx.android.synthetic.main.dialog_long_press_gradients.holder
import kotlinx.android.synthetic.main.dialog_popup.*
import kotlinx.android.synthetic.main.module_browse_normal.view.*
import java.io.Serializable

class DialogChangelog : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_changelog, container)
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

        /*UIElements.viewWidthAnimator(notchEmptyLeft, 0f, Calculations.cutoutWidthLeft(activity!!.window).toFloat(), 0, 100, LinearInterpolator())
        UIElements.viewWidthAnimator(notchEmptyRight, 0f, Calculations.cutoutWidthRight(activity!!.window).toFloat(), 0, 100, LinearInterpolator())
        UIElements.viewHeightAnimator(notchEmptyTop, 0f, Calculations.cutoutHeight(activity!!.window).toFloat(), 0, 100, LinearInterpolator())*/
    }

    companion object {
        fun newDialog(arrayList: ArrayList<String>?): DialogChangelog {
            val frag = DialogChangelog()
            /*val args = Bundle()
            args.putSerializable("array", arrayList as Serializable)
            frag.arguments = args*/

            return frag
        }
    }

}