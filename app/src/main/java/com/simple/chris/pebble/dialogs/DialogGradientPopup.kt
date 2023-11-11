package com.simple.chris.pebble.dialogs

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
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.databinding.DialogLongPressGradientsBinding
import com.simple.chris.pebble.functions.Property
import com.simple.chris.pebble.functions.Values
import com.simple.chris.pebble.functions.animateView
import com.simple.chris.pebble.functions.convertFloatToDP
import com.simple.chris.pebble.functions.generateGradientDrawable
import com.simple.chris.pebble.functions.getScreenMetrics

class DialogGradientPopup : DialogFragment() {
    private var _binding: DialogLongPressGradientsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLongPressGradientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureDialogWindow()
        applyBlurEffect()
        setupUIElements()
    }

    private fun configureDialogWindow() {
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0f)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    private fun applyBlurEffect() {
        if (Values.settingSpecialEffects) {
            try {
                binding.blurView.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content))
                    .setFrameClearDrawable(requireActivity().window.decorView.background)
                    //.setBlurAlgorithm(RenderScriptBlur(activity))
                    .setBlurRadius(30f)
                    .setOverlayColor(Color.parseColor(Values.dialogBackgroundTint))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.DialogGradientPopup: ${e.localizedMessage}")
            }
        } else {
            dialog?.window?.apply { setDimAmount(Values.dialogBackgroundDim) }
        }
    }

    private fun setupUIElements() {
        binding.holder.post {
            //Get Passed Argument
            val gradientName = requireArguments().getString("name") ?: "[ERROR]"
            val gradientDescription = requireArguments().getString("description") ?: "[ERROR]"
            val gradientColours = requireArguments().getStringArrayList("array") ?: arrayListOf("#000000", "#000000")
            val gradientPos = requireArguments().getIntArray("viewPos") ?: intArrayOf(0, 0)

            //Set Text
            binding.gradientDialogGradientName.text = gradientName
            binding.gradientDialogGradientDescription.apply {
                text = gradientDescription
                visibility = if (gradientDescription.trim().isEmpty()) View.GONE else View.VISIBLE
            }
            //binding.gradientDialogGradientDescription.text = gradientDescription
            generateGradientDrawable(this@DialogGradientPopup.requireContext(), binding.gradientPreview, gradientColours, 15f)

            //Complete Setup
            animateDialogEntrance(gradientPos)
            animateDrawCaller()
        }

        binding.blurView.setOnClickListener {
            animateDialogExit()
        }
    }

    private fun animateDialogEntrance(gradientPos: IntArray) {
        with(binding) {
            val screenMetrics = getScreenMetrics(requireContext(), dialog!!.window!!)
            val initialTranslationX = gradientPos[0].toFloat() - convertFloatToDP(requireContext(), 20f)
            val initialTranslationY = gradientPos[1].toFloat() - convertFloatToDP(requireContext(), 20f)

            //Set Starting Position/Scale
            animateView(holder, Property.TRANSLATION_X, initialTranslationX, 0)
            animateView(holder, Property.TRANSLATION_Y, initialTranslationY, 0)
            animateView(holder, Property.SCALE_X, 1f, 0)
            animateView(holder, Property.SCALE_Y, 1f, 0)

            //Animate to Final Position/Opacity
            animateView(holder, Property.TRANSLATION_X, (screenMetrics.width / 2f) - (holder.width / 2f), 400)
            animateView(holder, Property.TRANSLATION_Y, (screenMetrics.height / 2f) - (holder.height / 2f), 400)
            animateView(holder, Property.ALPHA, 1f, 200, 200, LinearInterpolator())
        }
    }

    private fun animateDrawCaller() {
        with(binding) {
            animateView(drawCaller, Property.SCALE_Y, 2f, 60000)
        }
    }

    private fun animateDialogExit() {
        with(binding) {
            //Animate out dialog
            animateView(holder, Property.SCALE_X, 1.15f, 200, interpolator = AccelerateInterpolator(3f))
            animateView(holder, Property.SCALE_Y, 1.15f, 200, interpolator = AccelerateInterpolator(3f))
            animateView(holder, Property.ALPHA, 0f, 100, 100, LinearInterpolator())

            //Dismiss Dialog
            Handler(Looper.getMainLooper()).postDelayed({
                dialog?.dismiss()
            }, 200)
        }
    }

    companion object {
        fun newDialog(arrayList: ArrayList<String>, name: String, description: String, viewPos: IntArray): DialogGradientPopup {
            return DialogGradientPopup().apply {
                arguments = Bundle().apply {
                    putSerializable("array", arrayList)
                    putString("name", name)
                    putString("description", description)
                    putIntArray("viewPos", viewPos)
                }
            }
        }
    }
}