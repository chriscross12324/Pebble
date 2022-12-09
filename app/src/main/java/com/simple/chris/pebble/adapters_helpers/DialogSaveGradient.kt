package com.simple.chris.pebble.adapters_helpers

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.R
import com.simple.chris.pebble.databinding.DialogSaveGradientBinding
import com.simple.chris.pebble.functions.Calculations
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.RenderScriptBlur
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class DialogSaveGradient : DialogFragment() {
    private var _binding: DialogSaveGradientBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_save_gradient, container)
    }*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogSaveGradientBinding.inflate(inflater, container, false)
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

        UIElement.gradientDrawableNew(activity as Context, binding.gradientPreview, requireArguments().getStringArrayList("array")!!, 20f)
        binding.heightText.setText(Calculations.screenMeasure(activity as Context, "height", requireActivity().window).toString())
        binding.widthText.setText(Calculations.screenMeasure(activity as Context, "width", requireActivity().window).toString())

        if (Values.settingsSpecialEffects) {
            try {
                val rootView = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = requireActivity().window.decorView.background

                binding.blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(activity))
                        .setBlurRadius(20f)
                        .setOverlayColor(Color.parseColor(Values.dialogBackgroundTint))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.save_gradient_dialog: ${e.localizedMessage}")
            }
        } else {
            binding.backgroundDimmer.alpha = Values.dialogBackgroundDimmer
        }

        binding.holder.post {
            UIElements.viewObjectAnimator(binding.holder, "scaleX", 1f, 550, 150, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.holder, "scaleY", 1f, 550, 150, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(binding.holder, "alpha", 1f, 100, 150, LinearInterpolator())
            UIElements.viewObjectAnimator(binding.drawCaller, "scaleY", 2f, 2000, 0, LinearInterpolator())
        }

        binding.blurView.setOnClickListener {
            onDismiss(dialog!!)
        }

        binding.presetButton.setOnClickListener {
            val height = Calculations.screenMeasure(activity as Context, "height", requireActivity().window)
            val width = Calculations.screenMeasure(activity as Context, "width", requireActivity().window)

            binding.heightText.setText(height.toString())
            binding.widthText.setText(width.toString())
        }

        binding.saveGradientButton.setOnClickListener {
            if (context != null) {
                try {
                    var outputStream: OutputStream
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        /** Android 10+ **/
                        val resolver = requireContext().contentResolver
                        val contentValues = ContentValues()
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, (Values.gradientScreenName + ".png").replace(" ", "_").toLowerCase(Locale.getDefault()))
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Pebble")
                        val imageURI = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        outputStream = resolver.openOutputStream(Objects.requireNonNull(imageURI) as Uri) as OutputStream
                    } else {
                        /** Android 9- **/
                        val imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                        val imageDir = File(imagePath, "/Pebble")
                        val dirFlag = imageDir.mkdirs()
                        val fileImage = File(imageDir, (Values.gradientScreenName + ".png").replace(" ", "_").toLowerCase(Locale.getDefault()))
                        outputStream = FileOutputStream(fileImage)
                    }
                    Calculations.createBitmap(UIElement.gradientDrawableNew(activity as Context, null, Values.gradientScreenColours, 0f) as Drawable,
                        binding.widthText.text.toString().toInt(), binding.heightText.text.toString().toInt()).compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Objects.requireNonNull(outputStream).close()
                    onDismiss(dialog!!)
                } catch (e: Exception) {
                    Toast.makeText(this.context, "Error: Have you granted storage permissions?", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("ERR", "pebble.dialog_save_gradient.save_gradient: Context is null")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        try {
            if (binding.holder != null) {
                UIElements.viewObjectAnimator(binding.holder, "scaleX", 1.15f, 200, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(binding.holder, "scaleY", 1.15f, 200, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(binding.holder, "alpha", 0f, 100, 100, LinearInterpolator())

                Handler(Looper.getMainLooper()).postDelayed({
                    super.onDismiss(dialog)
                }, 250)
            }
        } catch (e: Exception) {
            onDismiss(dialog)
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