package com.simple.chris.pebble.dialogs

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
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.databinding.DialogLongPressGradientsBinding
import com.simple.chris.pebble.functions.Calculations
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.RenderScriptBlur
import java.io.Serializable

class DialogGradientInfo : DialogFragment() {
    private var _binding: DialogLongPressGradientsBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLongPressGradientsBinding.inflate(inflater, container, false)
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
        val gradientPos = requireArguments().getIntArray("viewPos")!!

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

        UIElement.gradientDrawableNew(activity as Context, binding.gradientPreview, requireArguments().getStringArrayList("array")!!, 15f)
        binding.gradientDialogGradientName.text = requireArguments().getString("name")
        binding.gradientDialogGradientDescription.text = requireArguments().getString("desc")

        binding.holder.post {
            UIElements.viewObjectAnimator(binding.holder, "translationX", gradientPos[0].toFloat() - Calculations.convertToDP(this.requireContext(), 20f), 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(binding.holder, "translationY", gradientPos[1].toFloat() - Calculations.convertToDP(this.requireContext(), 20f), 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(binding.holder, "translationX", Calculations.screenMeasure(this.requireContext(), "width", dialog!!.window!!)/2.toFloat() - binding.holder.width/2, 400, 0, DecelerateInterpolator(2f))
            UIElements.viewObjectAnimator(binding.holder, "translationY", Calculations.screenMeasure(this.requireContext(), "height", dialog!!.window!!)/2.toFloat() - binding.holder.height/2, 400, 0, DecelerateInterpolator(2f))
            UIElements.viewObjectAnimator(binding.holder, "scaleX", 1f, 0, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.holder, "scaleY", 1f, 0, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.holder, "alpha", 1f, 200, 200, LinearInterpolator())
            UIElements.viewObjectAnimator(binding.drawCaller, "scaleY", 2f, 60000, 0, LinearInterpolator())
        }

        binding.blurView.setOnClickListener {
            UIElements.viewObjectAnimator(binding.holder, "scaleX", 1.15f, 200, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.holder, "scaleY", 1.15f, 200, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.holder, "alpha", 0f, 100, 100, LinearInterpolator())

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