package com.simple.chris.pebble.adapters_helpers

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.databinding.DialogColourInfoBinding
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.RenderScriptBlur
import java.io.Serializable

class DialogColourInfo : DialogFragment() {
    private var _binding: DialogColourInfoBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_colour_info, container)
    }*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogColourInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.window!!.setDimAmount(0f)
        val hexString = requireArguments().getString("hexString")!!

        if (Values.settingSpecialEffects) {
            try {
                val rootView = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = requireActivity().window.decorView.background

                binding.blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(activity))
                        .setBlurRadius(20f)
                        .setOverlayColor(Color.parseColor(Values.dialogBackgroundTint))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.gradient_long_click_dialog: ${e.localizedMessage}")
            }
        } else {
            binding.backgroundDimmer.alpha = Values.dialogBackgroundDim
        }

        /** Calculate RGB & HSV values **/
        val r = Integer.valueOf(hexString.substring(1, 3), 16)
        val g = Integer.valueOf(hexString.substring(3, 5), 16)
        val b = Integer.valueOf(hexString.substring(5, 7), 16)
        val hsv = floatArrayOf(0f, 1f, 1f)
        Color.colorToHSV(Color.parseColor(hexString), hsv)
        val h = hsv[0].toInt()
        val s = (hsv[1] * 100).toInt()
        val v = (hsv[2] * 100).toInt()

        /** Set dialog layout **/
        binding.colourPreview.setImageDrawable(UIElements.colourDrawable(activity as Context, hexString, 20f))
        binding.hexText.text = hexString
        binding.rText.text = r.toString()
        binding.gText.text = g.toString()
        binding.bText.text = b.toString()
        binding.hText.text = h.toString()
        binding.sText.text = s.toString()
        binding.vText.text = v.toString()

        /** Animate popupLayout in **/
        binding.popupHolder.post {
            UIElements.viewObjectAnimator(binding.popupHolder, "scaleX", 1f, 550, 150, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.popupHolder, "scaleY", 1f, 550, 150, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.popupHolder, "alpha", 1f, 100, 150, LinearInterpolator())
            UIElements.viewObjectAnimator(binding.drawCaller, "scaleY", 2f, 2000, 0, LinearInterpolator())
        }

        binding.blurView.setOnClickListener {
            onDismiss(dialog!!)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        UIElements.viewObjectAnimator(binding.popupHolder, "scaleX", 1.15f, 200, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.popupHolder, "scaleY", 1.15f, 200, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.popupHolder, "alpha", 0f, 100, 100, LinearInterpolator())

        Handler(Looper.getMainLooper()).postDelayed({
            Log.e("INFO", "Here")
            super.onDismiss(dialog)
        }, 250)
    }

    companion object {
        fun newDialog(hexString: String): DialogColourInfo {
            val frag = DialogColourInfo()
            val args = Bundle()
            args.putSerializable("hexString", hexString as Serializable)
            frag.arguments = args

            return frag
        }
    }

}