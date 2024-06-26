package com.simple.chris.pebble.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.simple.chris.pebble.R
import com.simple.chris.pebble.databinding.FragmentGradientScreenBinding
import com.simple.chris.pebble.dialogs.DialogPopup
import com.simple.chris.pebble.dialogs.DialogSaveGradient
import com.simple.chris.pebble.functions.*
import kotlin.Exception

class FragExpandedGradient : Fragment(R.layout.fragment_gradient_screen) {
    private var _binding: FragmentGradientScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Activity
    private lateinit var gradientName: String
    private lateinit var gradientDescription: String
    private lateinit var gradientColourArray: ArrayList<String>

    private var savedFileName = ""
    private var optionsExpanded = false
    private var optionsAnimating = false
    private var notificationShowing = false
    private var animatingFullscreen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGradientScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = (activity as ActivityMain)
    }

    fun startSplitScreen() {
        /** Called if SplitScreen started from fresh, or if fullscreen **/
        try {
            binding.gradientViewer.background = null
            binding.backgroundDimmer.alpha = 0f
        } catch (e: Exception) {
        }

        /** Set local values from Values.kt **/
        gradientName = Values.gradientScreenName
        gradientDescription = Values.gradientScreenDesc
        gradientColourArray = Values.gradientScreenColours

        /** Wait for UI to populate **/
        Handler(Looper.getMainLooper()).postDelayed({
            if (binding.detailsHolder != null) {
                /** Set UI Elements **/
                optionsExpanded = false
                binding.optionsHolder.visibility = View.INVISIBLE
                binding.gradientNameText.text = gradientName
                binding.gradientDescriptionText.text = gradientDescription
                binding.gradientDescriptionText.visibility = if (gradientDescription == "") View.GONE else View.VISIBLE
                UIElements.viewObjectAnimator(binding.backgroundDimmer, "alpha", 0f, 0, 0, LinearInterpolator())

                /** Set position of UI Elements **/
                UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                        (90 * resources.displayMetrics.density) + binding.detailsHolder.height,
                        0, 0, LinearInterpolator())
                UIElements.viewObjectAnimator(binding.actionsHolder, "translationY",
                        (74 * resources.displayMetrics.density) + binding.detailsHolder.height,
                        0, 0, LinearInterpolator())

                /** Tell MainActivity everything is ready **/
                tellMainReady()
            } else {
                startSplitScreen()
            }
        }, 50)
    }

    fun continueSplitScreen() {
        /** Called if SplitScreen is already showing Gradient Screen**/

        /** Set local values from Values.kt **/
        gradientName = Values.gradientScreenName
        gradientDescription = Values.gradientScreenDesc
        gradientColourArray = Values.gradientScreenColours

        /** Wait for UI to populate **/
        Handler(Looper.getMainLooper()).postDelayed({
            if (binding.detailsHolder != null) {
                Log.d("DEBUG", "Continuing")
                /** Animates away UI Elements **/
                UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                        (90 * resources.displayMetrics.density) + binding.detailsHolder.height,
                        500, 30, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(binding.actionsHolder, "translationY",
                        (74 * resources.displayMetrics.density) + binding.detailsHolder.height,
                        500, 0, DecelerateInterpolator(3f))

                Handler(Looper.getMainLooper()).postDelayed({
                    /** Set UI Elements **/
                    optionsExpanded = false
                    binding.detailsHolder.visibility = View.INVISIBLE
                    binding.actionsHolder.visibility = View.INVISIBLE
                    binding.optionsHolder.visibility = View.INVISIBLE
                    binding.gradientNameText.text = gradientName
                    binding.gradientDescriptionText.text = gradientDescription
                    binding.gradientDescriptionText.visibility = if (gradientDescription == "") View.GONE else View.VISIBLE

                    /** Set position of UI Elements **/
                    UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                            (90 * resources.displayMetrics.density) + binding.detailsHolder.height,
                            0, 0, LinearInterpolator())
                    UIElements.viewObjectAnimator(binding.actionsHolder, "translationY",
                            (74 * resources.displayMetrics.density) + binding.detailsHolder.height,
                            0, 0, LinearInterpolator())

                    /** Tell MainActivity everything is ready **/
                    tellMainReady()
                }, 700)
            } else {
                continueSplitScreen()
            }
        }, 50)


    }

    private fun tellMainReady() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.canDismissSharedElement) {
                (activity as ActivityMain).endSharedElement()
                Values.currentActivity = "GradientScreen"
                buttonFunctionality()
            } else {
                tellMainReady()
            }
        }, 50)
    }

    fun showUI() {
        /** Set visibility for UI Elements **/
        generateGradientDrawable(context, binding.gradientViewer, gradientColourArray, 0f)
        UIElements.viewVisibility(binding.gradientViewer, View.VISIBLE, 0)
        UIElements.viewVisibility(binding.detailsHolder, View.VISIBLE, 0)
        UIElements.viewVisibility(binding.actionsHolder, View.VISIBLE, 0)

        UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                0f, 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.actionsHolder, "translationY",
                0f, 700, 50, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.backgroundDimmer, "alpha", 1f, 500, 0, LinearInterpolator())

        /** Tell app ready for another gradient **/
        Handler(Looper.getMainLooper()).postDelayed({
            Values.animatingSharedElement = false
        }, 750)
    }

    private fun hideUI() {
        /** Animate UI Elements out **/
        UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                (90 * resources.displayMetrics.density) + binding.detailsHolder.height,
                500, 30, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.actionsHolder, "translationY",
                (74 * resources.displayMetrics.density) + binding.detailsHolder.height,
                500, 0, DecelerateInterpolator(3f))

        Handler(Looper.getMainLooper()).postDelayed({
            binding.optionsHolder.visibility = View.INVISIBLE
            optionsExpanded = false
        }, 100)

        /** Tell MainActivity to hide secondary **/
        Handler(Looper.getMainLooper()).postDelayed({
            (activity as ActivityMain).hideFullScreen(false)
        }, 550)
    }

    fun animateBackgroundDimmer() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (binding.backgroundDimmer != null) {
                UIElements.viewObjectAnimator(binding.backgroundDimmer, "alpha", 1f, 500, 0, LinearInterpolator())
            } else {
                animateBackgroundDimmer()
                Log.e("ERR", "Run")
            }
        }, 10)

    }

    fun categorizeColour(colour: String): ArrayList<String> {
        val array = arrayListOf<String>()
        val hexValue = Color.parseColor(colour)
        val hsv = floatArrayOf(0f, 1f, 1f)
        Color.colorToHSV(hexValue, hsv)

        val hueValue = hsv[0].toInt()
        val satValue = (hsv[1] * 100).toInt()
        val valValue = (hsv[2] * 100).toInt()

        if (hueValue <= 18 || hueValue >= 300) {
            if (satValue >= 15 && valValue >= 10) {
                //Add Red
                array.add("red")
            }
        }
        if (hueValue in 5..40) {
            if (satValue >= 20 && valValue >= 10) {
                //Add Orange
                array.add("orange")
            }
        }
        if (hueValue in 35..80) {
            if (satValue >= 20 && valValue >= 10) {
                //Add Yellow
                array.add("yellow")
            }
        }
        if (hueValue in 70..160) {
            if (satValue >= 20 && valValue >= 10) {
                //Add Green
                array.add("green")
            }
        }
        if (hueValue in 160..250) {
            if (satValue >= 20 && valValue >= 10) {
                //Add Blue
                array.add("blue")
            }
        }
        if (hueValue in 250..330) {
            if (satValue >= 15 && valValue >= 10) {
                //Add Purple
                array.add("purple")
            }
        }
        if (satValue <= 30 && valValue >= 80) {
            //Add White
            array.add("white")
        }
        if (valValue <= 20) {
            //Add Black
            array.add("black")
        }
        return array
    }

    fun runNotification(icon: Int, text: Int) {
        /** Check that notification isn't already showing **/
        if (!notificationShowing) {
            /** Set initial UI Elements **/
            notificationShowing = true
            binding.notificationIcon.setImageResource(icon)
            binding.notificationText.setText(text)

            /** Animates notification in **/
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    vibrateNotification(context)
                    binding.notification.visibility = View.VISIBLE
                    UIElements.viewObjectAnimator(binding.notification, "translationY", (binding.notification.height + getCutoutHeight(context.window) +
                            convertFloatToDP(context, 16f)), 500, 0, DecelerateInterpolator(3f))
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            UIElements.viewObjectAnimator(binding.notification, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
                            Handler(Looper.getMainLooper()).postDelayed({
                                try {
                                    binding.notification.visibility = View.INVISIBLE
                                    notificationShowing = false
                                } catch (e: Exception){}

                            }, 500)
                        } catch (e: Exception) {}
                    }, 4000)
                } catch (e: Exception){}
            }, 500)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun buttonFunctionality() {
        binding.buttonBack.setOnClickListener {
            Values.currentGradientScreenPos = -1
            vibrateMedium(context)
            hideUI()
            Values.currentlySplitScreened = false
            Values.currentActivity = "Browse"
        }
        binding.buttonOptions.setOnClickListener {
            if (!optionsAnimating) {
                vibrateMedium(context)
                optionsAnimating = true

                if (optionsExpanded) {
                    UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                            0f, 500, 0, DecelerateInterpolator(3f))
                    UIElements.constraintLayoutElevationAnimator(binding.optionsHolder, 0f,
                            500, 0, DecelerateInterpolator(3f))
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.optionsHolder.visibility = View.INVISIBLE
                        optionsAnimating = false
                        optionsExpanded = false
                    }, 500)
                } else {
                    binding.optionsHolder.elevation = 0f
                    binding.optionsHolder.visibility = View.VISIBLE
                    UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                            -(binding.optionsHolder.height + convertFloatToDP(context, -66f)), 500, 0, DecelerateInterpolator(3f))
                    UIElements.constraintLayoutElevationAnimator(binding.optionsHolder, 1f,
                            500, 0, DecelerateInterpolator(3f))
                    Handler(Looper.getMainLooper()).postDelayed({
                        optionsAnimating = false
                        optionsExpanded = true
                    }, 500)
                }
            }

            if (optionsExpanded) {
                UIElements.viewObjectAnimator(binding.detailsHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
                UIElements.constraintLayoutElevationAnimator(binding.optionsHolder, 0f, 500, 0, DecelerateInterpolator())
                Handler(Looper.getMainLooper()).postDelayed({
                    if (binding.detailsHolder.translationY == 0f) {
                        binding.optionsHolder.visibility = View.GONE
                        optionsExpanded = false
                    }
                }, 500)
            } else {
                UIElements.viewObjectAnimator(binding.detailsHolder, "translationY", convertFloatToDP(context, -66f), 500, 0, DecelerateInterpolator(3f))
                UIElements.constraintLayoutElevationAnimator(binding.optionsHolder, convertFloatToDP(context, 12f), 500, 100, DecelerateInterpolator())
                binding.optionsHolder.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    if (binding.detailsHolder.translationY != 0f) {
                        optionsExpanded = true
                    }
                }, 500)
            }
        }
        binding.buttonSaveGradient.setOnClickListener {
            vibrateWeak(context)
            //UIElements.saveGradientDialog(context, Values.gradientScreenColours, (activity as MainActivity).window)
            val fm = fragmentManager as FragmentManager
            val saveGradientDialog = DialogSaveGradient.newInstance(Values.gradientScreenColours)
            saveGradientDialog.show(fm, "saveGradientDialog")
            //Save Gradient
        }
        binding.buttonSetWallpaper.setOnClickListener {
            val fm = (activity as ActivityMain).supportFragmentManager
            vibrateWeak(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Values.dialogPopup = DialogPopup.newDialog(
                    arraySetWallpaper(), "setWallpaper", R.drawable.icon_wallpaper, R.string.dual_set_wallpaper,
                        null, R.string.question_set_wallpaper, null)
                Values.dialogPopup.show(fm, "setWallpaper")
            } else {
                Values.dialogPopup = DialogPopup.newDialog(
                    arrayYesCancel(), "setWallpaperOutdated", R.drawable.icon_warning, R.string.dual_outdated_android,
                        null, R.string.question_outdated_android, null)
                Values.dialogPopup.show(fm, "setWallpaperOutdated")
            }
            //Set Wallpaper
        }
        binding.gradientViewer.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!animatingFullscreen) {
                        animatingFullscreen = true
                        /** Animate UI Elements out **/
                        UIElements.viewObjectAnimator(binding.detailsHolder, "translationY",
                                (90 * resources.displayMetrics.density) + binding.detailsHolder.height,
                                500, 30, DecelerateInterpolator(3f))
                        UIElements.viewObjectAnimator(binding.actionsHolder, "translationY",
                                (74 * resources.displayMetrics.density) + binding.detailsHolder.height,
                                500, 0, DecelerateInterpolator(3f))

                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.optionsHolder.visibility = View.INVISIBLE
                            optionsExpanded = false
                        }, 100)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    showUI()
                    Handler(Looper.getMainLooper()).postDelayed({
                        animatingFullscreen = false
                    }, 500)
                }
            }
            true
        }
    }
}