package com.simple.chris.pebble.dialogs

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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.simple.chris.pebble.activities.GradientCreator
import com.simple.chris.pebble.activities.MainActivity
import com.simple.chris.pebble.databinding.DialogPopupBinding
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import com.simple.chris.pebble.recyclers.PopupDialogButtonRecycler
import eightbitlab.com.blurview.RenderScriptBlur

class DialogPopup : DialogFragment(), PopupDialogButtonRecycler.OnButtonListener {
    private var _binding: DialogPopupBinding? = null
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
        _binding = DialogPopupBinding.inflate(inflater, container, false)
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
        Values.dialogPopup = this
        Log.d("DEBUG", "Showing Dialog")
        if (requireArguments().getString("dialogName") == "connecting") {
            testConnection()
        }

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

        if (requireArguments().getInt("icon") != 0) {
            binding.permissionIcon.setImageResource(requireArguments().getInt("icon"))
        } else {
            binding.permissionIcon.visibility = View.INVISIBLE
            binding.progressCircle.visibility = View.VISIBLE
        }

        if (requireArguments().getInt("title") != 0) {
            binding.permissionTitle.setText(requireArguments().getInt("title"))
        } else {
            binding.permissionTitle.text = requireArguments().getString("titleString")
        }

        if (requireArguments().getInt("description") != 0) {
            binding.permissionDescription.setText(requireArguments().getInt("description"))
        } else {
            binding.permissionDescription.text = requireArguments().getString("descriptionString")
        }

        binding.holder.post {
            UIElements.viewObjectAnimator(binding.dialogHolder, "scaleX", 1f, 550, 150, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.dialogHolder, "scaleY", 1f, 550, 150, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.dialogHolder, "alpha", 1f, 100, 150, LinearInterpolator())
            UIElements.viewObjectAnimator(binding.drawCaller, "scaleY", 2f, 60000, 0, LinearInterpolator())
        }

        binding.blurView.setOnClickListener {
            if (requireArguments().getString("dialogName")!!.contains("setting") && (activity as Context).toString().contains("MainActivity")) {
                onDismiss(dialog!!)
            }
        }

        if (requireArguments().getSerializable("array") != null) {
            try {
                binding.popupButtonRecycler.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                val adapter = PopupDialogButtonRecycler((activity as Context), requireArguments().getString("dialogName")!!, requireArguments().getSerializable("array")!! as ArrayList<HashMap<String, Int>>, this)

                binding.popupButtonRecycler.layoutManager = layoutManager
                binding.popupButtonRecycler.adapter = adapter
            } catch (e: Exception) {
                Log.e("ERR", "pebble.dialog_popup.on_view_created: ${e.localizedMessage}")
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commit()
        } catch (e: Exception) {
            Log.e("ERR", "pebble.dialog_popup.show: ${e.localizedMessage}")
        }
    }

    private fun testConnection() {
        Handler(Looper.getMainLooper()).postDelayed({
            Handler(Looper.getMainLooper()).postDelayed({
                if (!Values.downloadingGradients) {
                    onDismiss(Values.dialogPopup.dialog!!)
                } else {
                    testConnection()
                }
            }, 200)
        }, 200)
    }

    override fun onDismiss(dialog: DialogInterface) {
        try {
            UIElements.viewObjectAnimator(binding.dialogHolder, "scaleX", 1.15f, 200, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.dialogHolder, "scaleY", 1.15f, 200, 0, AccelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.dialogHolder, "alpha", 0f, 100, 100, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                super.onDismiss(dialog)
            }, 250)
        } catch (e: Exception) {
            onDismiss(Values.dialogPopup.dialog!!)
        }
    }

    companion object {
        fun newDialog(buttonArray: ArrayList<HashMap<String, Int>>?, dialogName: String, icon: Int?, title: Int?, titleString: String?, description: Int?, descriptionString: String?): DialogPopup {
            val frag = DialogPopup()
            val args = Bundle()
            args.putString("dialogName", dialogName)
            if (buttonArray != null) {
                args.putSerializable("array", buttonArray)
            }
            if (icon != null) {
                args.putInt("icon", icon)
            }
            if (title != null) {
                args.putInt("title", title)
            }
            if (titleString != null) {
                args.putString("titleString", titleString)
            }
            if (description != null) {
                args.putInt("description", description)
            }
            if (descriptionString != null) {
                args.putString("descriptionString", descriptionString)
            }
            frag.arguments = args

            return frag
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        Values.dialogPopup = this
        onDismiss(dialog!!)
        when {
            popupName.contains("submit") -> {
                (activity as GradientCreator).popupDialogHandler(popupName, position)
            }
            else -> {
                (activity as MainActivity).popupDialogHandler(popupName, position)
            }
        }
    }

}