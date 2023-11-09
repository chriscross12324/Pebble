package com.simple.chris.pebble.activities

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.android.gms.ads.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.simple.chris.pebble.R
import com.simple.chris.pebble.dialogs.DialogPopup
import com.simple.chris.pebble.recyclers.SettingsRecyclerView
import com.simple.chris.pebble.databinding.ActivityMainBinding
import com.simple.chris.pebble.functions.*
import kotlin.math.roundToInt
import kotlin.random.Random

class ActivityMain : FragmentActivity(), SettingsRecyclerView.OnButtonListener {
    private lateinit var binding: ActivityMainBinding
    private var mSkuList = listOf("donate_1", "donate_2", "donate_5", "donate_10")

    var screenHeight = 0
    var bottomSheetPeekHeight = 0
    lateinit var browseFragment: Fragment
    lateinit var searchFragment: Fragment
    lateinit var gradientFragment: Fragment
    lateinit var settingsFragment: Fragment
    lateinit var fragmentManager: FragmentManager
    private var currentSmallScreen = "null"
    private val backStack = "STACK"

    var gradientViewSizeX = 0f
    var gradientViewSizeY = 0f
    var gradientViewPos = IntArray(2)
    var secondaryFragScaleX = 0f
    var secondaryFragScaleY = 0f
    var secondaryFragPosX = 0f

    private var ssScrollbarWidth = 0f
    private var ssScrollbarArea = 0f
    var ssScrollbarOffset = 0
    var ssScrollbarExtent = 0
    var ssScrollbarRange = 0

    /**
     * gradientsDownload
     * startSettings
     * startDonating
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Values.setFireStore(Firebase.firestore)
        if (!Values.refreshTheme) {
            if (Values.gradientList.isEmpty()) {
                Connection.checkConnection(this, this)
                connectionChecker()
            }
        } else {
            Values.refreshTheme = false
            //connectionChecker()
        }


        browseFragment = FragBrowse()
        gradientFragment = FragExpandedGradient()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, browseFragment)
            commit()
        }

        binding.tapReturn.setOnClickListener {
            hideSmallScreen()
        }
    }

    override fun onAttachedToWindow() {
        binding.fragmentHolder.post {
            Values.screenHeight = getScreenMetrics(this, window).height
            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()
        }
    }

    fun startSettings() {
        if (currentSmallScreen != "settings") {
            currentSmallScreen = "settings"
            binding.ssIcon.setImageResource(R.drawable.icon_settings)
            binding.ssTitle.setText(R.string.word_settings)
            binding.ssDescription.setText(R.string.sentence_customize_pebble)

            binding.ssRecycler.setHasFixedSize(true)
            val buttonLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val buttonAdapter = SettingsRecyclerView(this, "settings", arraySettings(), this)

            binding.ssRecycler.layoutManager = buttonLayoutManager
            binding.ssRecycler.adapter = buttonAdapter
            smallScreenScrollBar(buttonLayoutManager)

            //buttonLayoutManager.scrollToPositionWithOffset(1, 50)
        }
        binding.ssRecycler.post {
            showSmallScreen(binding.smallScreenFragHolder.measuredHeight.toFloat())
        }
    }

    fun startAbout() {
        if (currentSmallScreen != "about") {
            currentSmallScreen = "about"
            binding.ssIcon.setImageResource(R.drawable.icon_question)
            binding.ssTitle.setText(R.string.word_about)
            binding.ssDescription.setText(R.string.sentence_about)

            binding.ssRecycler.setHasFixedSize(true)
            val buttonLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val buttonAdapter = SettingsRecyclerView(this, "about", arrayAbout(), this)

            binding.ssRecycler.layoutManager = buttonLayoutManager
            binding.ssRecycler.adapter = buttonAdapter
            smallScreenScrollBar(buttonLayoutManager)
        }
        binding.ssRecycler.post {
            showSmallScreen(binding.smallScreenFragHolder.measuredHeight.toFloat())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun smallScreenScrollBar(layoutManager: LinearLayoutManager) {
        ssScrollbarWidth =
            binding.smallScreenFragHolder.measuredWidth - convertFloatToDP(this, 70f)
        ssScrollbarArea = ssScrollbarWidth - binding.ssScrollbar.measuredWidth
        UIElements.viewHeightAnimator(
            binding.ssScrollbar,
            convertFloatToDP(this, 5f),
            convertFloatToDP(this, 2.55f),
            0,
            0,
            DecelerateInterpolator(3f)
        )
        UIElements.viewObjectAnimator(
            binding.ssScrollbar,
            "translationY",
            convertFloatToDP(this, 8f),
            0,
            0,
            DecelerateInterpolator(3f)
        )
        ssScrollbarOffset = 0
        binding.ssRecycler.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            ssScrollbarExtent = binding.ssRecycler.computeHorizontalScrollExtent()
            ssScrollbarRange = binding.ssRecycler.computeHorizontalScrollRange()
            scrollbarTranslationCalculator()
        }

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            Vibration.lowFeedback(this)
            UIElements.viewHeightAnimator(
                binding.ssScrollbar,
                convertFloatToDP(this, 5f),
                convertFloatToDP(this, 2.55f),
                500,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.ssScrollbar,
                "translationY",
                convertFloatToDP(this, 8f),
                500,
                0,
                DecelerateInterpolator(3f)
            )
        }

        binding.ssScrollbarTrigger.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e("INFO", "Down")
                    Vibration.mediumFeedback(this)
                    handler.removeCallbacks(runnable)
                    UIElements.viewHeightAnimator(
                        binding.ssScrollbar,
                        binding.ssScrollbar.measuredHeight.toFloat(),
                        convertFloatToDP(this, 5f),
                        500,
                        0,
                        DecelerateInterpolator(3f)
                    )
                    UIElements.viewObjectAnimator(
                        binding.ssScrollbar,
                        "translationY",
                        0f,
                        500,
                        0,
                        DecelerateInterpolator(3f)
                    )
                    true
                }

                MotionEvent.ACTION_UP -> {
                    Log.e("INFO", "Up")
                    Vibration.mediumFeedback(this)
                    handler.postDelayed(runnable, 1000)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val x = motionEvent.x
                    val xIntStart = convertFloatToDP(this, 57.5f)
                    val xIntEnd = getScreenMetrics(this, window).width - xIntStart
                    val xProgress =
                        0f.coerceAtLeast(100f.coerceAtMost((100 / (xIntEnd - xIntStart)) * (x - xIntStart)))
                    val xOffset = (xProgress / 100) * (ssScrollbarRange - ssScrollbarExtent)
                    layoutManager.scrollToPositionWithOffset(0, -xOffset.roundToInt())
                    scrollbarTranslationCalculator()
                    true
                }

                else -> true
            }
        }
    }

    private fun scrollbarTranslationCalculator() {
        ssScrollbarOffset = binding.ssRecycler.computeHorizontalScrollOffset()
        val percent = (100f * ssScrollbarOffset / (ssScrollbarRange - ssScrollbarExtent))
        binding.ssScrollbar.translationX = (ssScrollbarArea / 100) * percent
    }

    fun shrinkFrag(fragment: CardView, scale: Float, duration: Long, interpolator: Interpolator) {
        UIElements.viewObjectAnimator(fragment, "scaleY", scale, duration, 0, interpolator)
        UIElements.viewObjectAnimator(fragment, "scaleX", scale, duration, 0, interpolator)
        UIElement.cardRadiusAnimator(
            fragment,
            convertFloatToDP(this, 30f),
            duration,
            0,
            interpolator
        )
    }

    private fun moveUpFrag(height: Float) {
        UIElements.viewObjectAnimator(
            binding.fragmentHolder,
            "translationY",
            -(height * 0.8).toFloat(),
            500,
            0,
            DecelerateInterpolator(3f)
        )
    }

    private fun moveFragLeft(width: Float, fragment: View) {
        UIElements.viewObjectAnimator(
            fragment,
            "translationX",
            -width,
            500,
            0,
            DecelerateInterpolator(3f)
        )
    }

    private fun moveFragRight(width: Float, fragment: View) {
        UIElements.viewObjectAnimator(
            fragment,
            "translationX",
            width,
            500,
            0,
            DecelerateInterpolator(3f)
        )
    }

    private fun growFrag(
        fragment: CardView,
        scale: Float,
        duration: Long,
        interpolator: Interpolator
    ) {
        UIElements.viewObjectAnimator(fragment, "scaleY", scale, duration, 0, interpolator)
        UIElements.viewObjectAnimator(fragment, "scaleX", scale, duration, 0, interpolator)
        UIElement.cardRadiusAnimator(fragment, 0f, duration, 0, interpolator)
    }

    private fun moveDownFrag() {
        UIElements.viewObjectAnimator(
            binding.fragmentHolder,
            "translationY",
            0f,
            500,
            0,
            DecelerateInterpolator(3f)
        )
    }

    fun showSmallScreen(height: Float) {
        binding.tapReturn.visibility = View.VISIBLE
        binding.tapReturnText.visibility = View.VISIBLE
        shrinkFrag(binding.fragmentHolder, 0.7f, 500, DecelerateInterpolator(3f))
        moveUpFrag(height)
        UIElements.viewObjectAnimator(
            binding.smallScreenFragHolder,
            "translationY",
            -height,
            500,
            0,
            DecelerateInterpolator(3f)
        )
        UIElements.viewObjectAnimator(
            binding.tapReturn,
            "alpha",
            0.6f,
            250,
            0,
            LinearInterpolator()
        )
        UIElements.viewObjectAnimator(
            binding.tapReturnText,
            "alpha",
            1f,
            250,
            0,
            LinearInterpolator()
        )
        binding.tapReturnText.translationY = (-height / 2)
    }

    fun hideSmallScreen() {
        growFrag(binding.fragmentHolder, 1f, 500, DecelerateInterpolator(3f))
        moveDownFrag()
        UIElements.viewObjectAnimator(
            binding.smallScreenFragHolder,
            "translationY",
            0f,
            500,
            0,
            DecelerateInterpolator(3f)
        )
        UIElements.viewObjectAnimator(binding.tapReturn, "alpha", 0f, 150, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(
            binding.tapReturnText,
            "alpha",
            0f,
            150,
            0,
            LinearInterpolator()
        )
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tapReturn.visibility = View.GONE
            binding.tapReturnText.visibility = View.GONE
        }, 150)
        //onBackPressed()
    }

    fun startGradientCreator() {
        /** Check to see if GradientCreator already has colours **/
        if (Values.gradientCreatorColours.isEmpty()) {
            Values.gradientCreatorColours.add(
                "#${
                    Integer.toHexString(
                        Color.rgb(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        )
                    ).substring(2)
                }"
            )
            Values.gradientCreatorColours.add(
                "#${
                    Integer.toHexString(
                        Color.rgb(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        )
                    ).substring(2)
                }"
            )
            Values.gradientCreatorColours.add(
                "#${
                    Integer.toHexString(
                        Color.rgb(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        )
                    ).substring(2)
                }"
            )
        }

        shrinkFrag(binding.fragmentHolder, 0.7f, 500, DecelerateInterpolator(3f))
        Values.animatingSharedElement = true
        Handler(Looper.getMainLooper()).postDelayed({
            /** Set initial properties of SharedElement **/
            UIElements.viewVisibility(binding.gradientScreenAnimationHero, View.VISIBLE, 0)
            UIElements.cardViewCornerRadiusAnimator(
                binding.gradientScreenAnimationHero,
                convertFloatToDP(this, 20f), 0, 0, LinearInterpolator()
            )
            UIElements.viewWidthAnimator(
                binding.gradientScreenAnimationHero, 0f,
                convertFloatToDP(this, 150f), 0, 0, LinearInterpolator()
            )
            UIElements.viewHeightAnimator(
                binding.gradientScreenAnimationHero, 0f,
                convertFloatToDP(this, 150f), 0, 0, LinearInterpolator()
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero, "translationX",
                (getScreenMetrics(this, window).width - convertFloatToDP(
                    this,
                    150f
                )) / 2,
                0, 0, LinearInterpolator()
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero, "translationY",
                getScreenMetrics(this, window).height + convertFloatToDP(
                    this,
                    150f
                ),
                0, 0, LinearInterpolator()
            )
            UIElement.gradientDrawableNew(
                this,
                binding.sharedElementGradientViewer,
                Values.gradientCreatorColours,
                0f
            )

            /** Animate SharedElement **/
            UIElements.viewWidthAnimator(
                binding.gradientScreenAnimationHero,
                binding.gradientScreenAnimationHero.measuredWidth.toFloat(),
                getScreenMetrics(this, window).width.toFloat(),
                Values.animationDuration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewHeightAnimator(
                binding.gradientScreenAnimationHero,
                binding.gradientScreenAnimationHero.measuredHeight.toFloat(),
                getScreenMetrics(this, window).height.toFloat(),
                Values.animationDuration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "translationX",
                0f,
                Values.animationDuration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero, "translationY",
                (getScreenMetrics(this, window).height - binding.gradientScreenAnimationHero.measuredHeight).toFloat() / 2,
                Values.animationDuration, 0, DecelerateInterpolator(3f)
            )
            UIElements.cardViewCornerRadiusAnimator(
                binding.gradientScreenAnimationHero,
                0f,
                Values.animationDuration - 100,
                0,
                LinearInterpolator()
            )

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ActivityGradientCreator::class.java))
                overridePendingTransition(0, 0)
                Values.animatingSharedElement = false
            }, Values.animationDuration)
        }, 0)
    }

    fun closeSecondary() {
        if (Values.currentlySplitScreened) {
            Values.currentlySplitScreened = false
            //Toast.makeText(this, "Exiting SplitScreen", Toast.LENGTH_SHORT).show()
            //Is in SplitScreen mode
            UIElements.viewWidthAnimator(
                binding.fragmentHolder,
                binding.fragmentHolder.width.toFloat(),
                getScreenMetrics(this, window).width.toFloat(),
                500,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.separator,
                "translationX",
                0f,
                500,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.fragmentHolderSecondary,
                "translationX",
                0f,
                500,
                0,
                DecelerateInterpolator(3f)
            )
        } else {
            //Not in SplitScreen
            if (Values.currentActivity == "GradientScreen") {
                UIElements.viewObjectAnimator(
                    binding.fragmentHolderSecondary,
                    "translationX",
                    0f,
                    0,
                    0,
                    LinearInterpolator()
                )
            } else {
                UIElements.viewObjectAnimator(
                    binding.fragmentHolder,
                    "translationX",
                    0f,
                    500,
                    0,
                    DecelerateInterpolator(3f)
                )
                UIElements.viewObjectAnimator(
                    binding.fragmentHolderSecondary,
                    "translationX",
                    0f,
                    500,
                    0,
                    DecelerateInterpolator(3f)
                )
                shrinkFrag(binding.fragmentHolder, 0.9f, 100, DecelerateInterpolator(3f))
                shrinkFrag(binding.fragmentHolderSecondary, 0.9f, 100, DecelerateInterpolator(3f))
                Handler(Looper.getMainLooper()).postDelayed({
                    growFrag(binding.fragmentHolder, 1f, 200, AccelerateInterpolator())
                    growFrag(binding.fragmentHolderSecondary, 1f, 200, AccelerateInterpolator())
                }, 100)
            }
        }
        Values.currentActivity = "Browse"
    }

    private fun refreshTheme() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentHolder)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ActivityMain::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 500)
    }

    override fun onButtonClick(screenName: String, position: Int, view: View) {
        val fm = supportFragmentManager
        when (screenName) {
            "settings" -> {
                when (position) {
                    0 -> {
                        //Theme
                        Values.dialogPopup = DialogPopup.newDialog(
                            arrayTheme(),
                            "settingTheme",
                            R.drawable.icon_brush,
                            R.string.word_theme,
                            null,
                            R.string.question_setting_theme,
                            null
                        )
                        Values.dialogPopup.show(fm, "settingTheme")
                    }

                    1 -> {
                        //Vibration
                        Values.dialogPopup = DialogPopup.newDialog(
                            arrayOnOff(),
                            "settingVibration",
                            R.drawable.icon_vibrate,
                            R.string.word_vibration,
                            null,
                            R.string.question_setting_vibration,
                            null
                        )
                        Values.dialogPopup.show(fm, "settingVibration")
                    }

                    2 -> {
                        //Special Effects
                        Values.dialogPopup = DialogPopup.newDialog(
                            arrayOnOff(),
                            "settingSpecialEffects",
                            R.drawable.icon_blur_on,
                            R.string.dual_special_effects,
                            null,
                            R.string.question_setting_blur,
                            null
                        )
                        Values.dialogPopup.show(fm, "settingSpecialEffects")
                    }

                    3 -> {
                        //Split Screen
                        Values.dialogPopup = DialogPopup.newDialog(
                            arrayOnOff(),
                            "settingSplitScreen",
                            R.drawable.icon_split_screen,
                            R.string.dual_split_screen,
                            null,
                            R.string.question_setting_split_screen,
                            null
                        )
                        Values.dialogPopup.show(fm, "settingSplitScreen")
                    }

                    4 -> {
                        //Cellular Data
                        Values.dialogPopup = DialogPopup.newDialog(
                            arrayOnOff(),
                            "settingNetwork",
                            R.drawable.icon_cell_wifi,
                            R.string.word_network,
                            null,
                            R.string.question_setting_network,
                            null
                        )
                        Values.dialogPopup.show(fm, "settingNetwork")
                    }
                }
            }
        }
    }

    fun startGradientScreen(animateNew: Boolean) {
        Log.e("INFO", "${Values.currentlySplitScreened}")
        if (!Values.currentlySplitScreened) {
            if (canSplitScreen(this, window)) {
                openSplitScreen(true)
            } else {
                openFullScreen(false)
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolderSecondary, gradientFragment)
                .commitAllowingStateLoss()
            (gradientFragment as FragExpandedGradient).startSplitScreen()
        } else {
            Log.e("INFO", "${Values.currentActivity}")
            if (Values.currentActivity != "GradientScreen") {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragmentHolderSecondary, gradientFragment)
                    .commitAllowingStateLoss()
                (gradientFragment as FragExpandedGradient).startSplitScreen()
            } else {
                if (animateNew) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragmentHolderSecondary, gradientFragment)
                        .commitAllowingStateLoss()
                    (gradientFragment as FragExpandedGradient).startSplitScreen()
                } else {
                    (gradientFragment as FragExpandedGradient).continueSplitScreen()
                }
            }
        }
    }

    fun startSearch() {
        //Toast.makeText(this, "${Values.currentlySplitScreened} : ${Values.currentActivity}", Toast.LENGTH_SHORT).show()
        Values.currentGradientScreenPos = -1
        if (!Values.currentlySplitScreened) {
            if (canSplitScreen(this, window)) {
                //Toast.makeText(this, "Split", Toast.LENGTH_SHORT).show()
                openSplitScreen(true)
            } else {
                //Toast.makeText(this, "Fullscreen", Toast.LENGTH_SHORT).show()
                openFullScreen(true)
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolderSecondary, searchFragment)
                .commitAllowingStateLoss()
        } else {
            if (Values.currentActivity != "Search") {
                Toast.makeText(this, "Converting", Toast.LENGTH_SHORT).show()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragmentHolderSecondary, searchFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    @SuppressLint("CutPasteId")
    fun sharedElement(position: Int, view: View) {
        if (Values.currentGradientScreenPos == position) {
            Vibration.notification(this)
            shrinkFrag(
                binding.fragmentHolderSecondary,
                0.95f,
                150,
                AccelerateDecelerateInterpolator()
            )
            Handler(Looper.getMainLooper()).postDelayed({
                growFrag(
                    binding.fragmentHolderSecondary,
                    1f,
                    150,
                    AccelerateDecelerateInterpolator()
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    Values.canDismissSharedElement = true
                    Values.animatingSharedElement = false
                }, 300)
            }, 150)
        } else {
            Values.currentGradientScreenPos = position
            /** Get scale/position of clicked Gradient **/
            gradientViewSizeX = view.findViewById<ImageView>(R.id.gradient).width.toFloat()
            gradientViewSizeY = view.findViewById<ImageView>(R.id.gradient).height.toFloat()
            view.findViewById<ImageView>(R.id.gradient).getLocationOnScreen(gradientViewPos)

            /** Get scale/position of secondFrag **/
            if (canSplitScreen(this, window)) {
                secondaryFragPosX = (getScreenMetrics(this, window).width / 2) + (binding.separator.width / 2).toFloat()
                secondaryFragScaleX = (getScreenMetrics(this, window).width / 2) - (binding.separator.width / 2).toFloat()
                secondaryFragScaleY = getScreenMetrics(this, window).height.toFloat()
            } else {
                secondaryFragPosX = 0f
                secondaryFragScaleX = getScreenMetrics(this, window).width.toFloat()
                secondaryFragScaleY = getScreenMetrics(this, window).height.toFloat()
            }

            /** Set values for FragGradientScreen in Values.kt **/
            if (Values.isSearchMode) {
                Values.gradientScreenName = Values.searchList[position]["gradientName"] as String
                Values.gradientScreenDesc =
                    Values.searchList[position]["gradientDescription"] as String
                Values.gradientScreenColours =
                    ArrayList(Values.searchList[position]["gradientColours"]!!.replace("[", "")
                        .replace("]", "")
                        .split(",").map { it.trim() })
            } else {
                Values.gradientScreenName = Values.gradientList[position]["gradientName"] as String
                Values.gradientScreenDesc =
                    Values.gradientList[position]["gradientDescription"] as String
                Values.gradientScreenColours =
                    ArrayList(Values.gradientList[position]["gradientColours"]!!.replace("[", "")
                        .replace("]", "")
                        .split(",").map { it.trim() })
            }
            startGradientScreen(false)

            /** Set initial properties of SharedElement **/
            UIElements.viewVisibility(binding.gradientScreenAnimationHero, View.VISIBLE, 0)
            UIElements.cardViewCornerRadiusAnimator(
                binding.gradientScreenAnimationHero,
                convertFloatToDP(this, 20f),
                0,
                0,
                LinearInterpolator()
            )
            UIElements.viewWidthAnimator(
                binding.gradientScreenAnimationHero,
                0f,
                gradientViewSizeX,
                0,
                0,
                LinearInterpolator()
            )
            UIElements.viewHeightAnimator(
                binding.gradientScreenAnimationHero,
                0f,
                gradientViewSizeY,
                0,
                0,
                LinearInterpolator()
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "translationX",
                gradientViewPos[0].toFloat(),
                0,
                0,
                LinearInterpolator()
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "translationY",
                gradientViewPos[1].toFloat(),
                0,
                0,
                LinearInterpolator()
            )
            UIElement.gradientDrawableNew(
                this,
                binding.sharedElementGradientViewer,
                Values.gradientScreenColours,
                0f
            )

            /** Animate SharedElement **/
            UIElements.viewWidthAnimator(
                binding.gradientScreenAnimationHero,
                gradientViewSizeX,
                secondaryFragScaleX,
                Values.animationDuration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewHeightAnimator(
                binding.gradientScreenAnimationHero,
                gradientViewSizeY,
                secondaryFragScaleY,
                Values.animationDuration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "translationX",
                secondaryFragPosX,
                Values.animationDuration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "translationY",
                0f,
                Values.animationDuration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.cardViewCornerRadiusAnimator(
                binding.gradientScreenAnimationHero,
                0f,
                Values.animationDuration - 100,
                0,
                LinearInterpolator()
            )

            /** Tells app sharedElement can be dismissed **/
            Handler(Looper.getMainLooper()).postDelayed({
                Values.canDismissSharedElement = true
            }, Values.animationDuration)
        }
    }

    fun endSharedElement() {
        /** Called from FragGradientScreen.kt; hides sharedElement **/
        UIElements.viewVisibility(binding.gradientScreenAnimationHero, View.GONE, 0)
        (gradientFragment as FragExpandedGradient).showUI()
    }

    fun openSplitScreen(animation: Boolean) {
        binding.separator.post {
            val duration: Long = if (animation) 500 else 0
            UIElements.viewWidthAnimator(
                binding.fragmentHolderSecondary, binding.fragmentHolderSecondary.width.toFloat(),
                getScreenMetrics(this, window).width / 2 - (binding.separator.width / 2).toFloat(), 0, 0, LinearInterpolator()
            )
            UIElements.viewWidthAnimator(
                binding.fragmentHolder,
                binding.fragmentHolder.width.toFloat(),
                (getScreenMetrics(this, window).width / 2) - (binding.separator.width / 2).toFloat(),
                duration,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.separator,
                "translationX",
                -((getScreenMetrics(this, window).width / 2) + (binding.separator.width / 2)).toFloat(),
                duration,
                0,
                DecelerateInterpolator(3f)
            )
            val secondaryPlacement = (getScreenMetrics(this, window).width / 2) + (binding.separator.width / 2).toFloat()
            UIElements.viewObjectAnimator(
                binding.fragmentHolderSecondary,
                "translationX",
                -secondaryPlacement,
                duration,
                0,
                DecelerateInterpolator(3f)
            )
            Values.currentlySplitScreened = true

            if (Values.isSearchMode) {
                (browseFragment as FragBrowse).setSBCRecyclerWidth(true)
            }
        }
    }

    fun openFullScreen(animation: Boolean) {
        binding.separator.post {
            if (animation) {
                binding.fragmentHolderSecondary.layoutParams.width =
                    getScreenMetrics(this, window).width
                moveFragLeft(binding.fragmentHolder.width.toFloat(), binding.fragmentHolder)
                moveFragLeft(
                    (getScreenMetrics(this, window).width + binding.separator.width).toFloat(), binding.fragmentHolderSecondary
                )
                shrinkFrag(binding.fragmentHolder, 0.9f, 100, DecelerateInterpolator(3f))
                shrinkFrag(binding.fragmentHolderSecondary, 0.9f, 100, DecelerateInterpolator(3f))
                Handler(Looper.getMainLooper()).postDelayed({
                    growFrag(binding.fragmentHolder, 1f, 200, AccelerateInterpolator())
                    growFrag(binding.fragmentHolderSecondary, 1f, 200, AccelerateInterpolator())
                    Values.currentActivity = "Search"
                }, 100)
            } else {
                shrinkFrag(binding.fragmentHolder, 0.9f, 200, DecelerateInterpolator(3f))
                UIElements.viewWidthAnimator(
                    binding.fragmentHolderSecondary,
                    binding.fragmentHolderSecondary.width.toFloat(),
                    getScreenMetrics(this, window).width.toFloat(),
                    0,
                    0,
                    LinearInterpolator()
                )
                UIElements.viewObjectAnimator(
                    binding.fragmentHolderSecondary, "translationX",
                    -(getScreenMetrics(this, window).width + binding.separator.width).toFloat(), 0, 0, LinearInterpolator()
                )
            }
        }
    }

    @SuppressLint("CutPasteId")
    fun hideFullScreen(animation: Boolean) {
        if (animation) {
            Values.currentActivity = "Browse"
            UIElements.viewObjectAnimator(
                binding.fragmentHolder,
                "translationX",
                0f,
                500,
                0,
                DecelerateInterpolator(3f)
            )
            UIElements.viewObjectAnimator(
                binding.fragmentHolderSecondary,
                "translationX",
                0f,
                500,
                0,
                DecelerateInterpolator(3f)
            )
            shrinkFrag(binding.fragmentHolder, 0.9f, 100, DecelerateInterpolator(3f))
            shrinkFrag(binding.fragmentHolderSecondary, 0.9f, 100, DecelerateInterpolator(3f))
            Handler(Looper.getMainLooper()).postDelayed({
                growFrag(binding.fragmentHolder, 1f, 200, AccelerateInterpolator())
                growFrag(binding.fragmentHolderSecondary, 1f, 200, AccelerateInterpolator())
            }, 100)
        } else {
            //Gradient Screen
            Values.animatingSharedElement = true
            if (!canSplitScreen(this, window)) {
                //Shared Element
                /** Get scale/position of clicked Gradient **/
                gradientViewSizeX =
                    Values.currentGradientScreenView.findViewById<ImageView>(R.id.gradient).width.toFloat()
                gradientViewSizeY =
                    Values.currentGradientScreenView.findViewById<ImageView>(R.id.gradient).height.toFloat()
                Values.currentGradientScreenView.findViewById<ImageView>(R.id.gradient)
                    .getLocationOnScreen(gradientViewPos)

                /** Get scale/position of secondFrag **/
                if (canSplitScreen(this, window)) {
                    secondaryFragPosX = (getScreenMetrics(this, window).width / 2) + (binding.separator.width / 2).toFloat()
                    secondaryFragScaleX = (getScreenMetrics(this, window).width / 2) - (binding.separator.width / 2).toFloat()
                    secondaryFragScaleY =
                        getScreenMetrics(this, window).height.toFloat()
                } else {
                    secondaryFragPosX = 0f
                    secondaryFragScaleX =
                        getScreenMetrics(this, window).width.toFloat()
                    secondaryFragScaleY =
                        getScreenMetrics(this, window).height.toFloat()
                }

                /** Set initial properties of SharedElement **/
                UIElement.gradientDrawableNew(
                    this,
                    binding.sharedElementGradientViewer,
                    Values.gradientScreenColours,
                    0f
                )
                UIElements.cardViewCornerRadiusAnimator(
                    binding.gradientScreenAnimationHero,
                    0f,
                    0,
                    0,
                    LinearInterpolator()
                )
                UIElements.viewWidthAnimator(
                    binding.gradientScreenAnimationHero,
                    0f,
                    secondaryFragScaleX,
                    0,
                    0,
                    LinearInterpolator()
                )
                UIElements.viewHeightAnimator(
                    binding.gradientScreenAnimationHero,
                    0f,
                    secondaryFragScaleY,
                    0,
                    0,
                    LinearInterpolator()
                )
                UIElements.viewObjectAnimator(
                    binding.gradientScreenAnimationHero,
                    "translationX",
                    secondaryFragPosX,
                    0,
                    0,
                    LinearInterpolator()
                )
                UIElements.viewVisibility(binding.gradientScreenAnimationHero, View.VISIBLE, 0)
                shrinkFrag(binding.fragmentHolder, 0.9f, 0, LinearInterpolator())

                UIElements.viewObjectAnimator(
                    binding.fragmentHolderSecondary, "translationX",
                    0f, 0, 0, LinearInterpolator()
                )

                /** Animate SharedElement **/
                UIElements.viewObjectAnimator(
                    binding.gradientScreenAnimationHero,
                    "scaleX",
                    0.3f,
                    500,
                    0,
                    DecelerateInterpolator(3f)
                )
                UIElements.viewObjectAnimator(
                    binding.gradientScreenAnimationHero,
                    "scaleY",
                    0.3f,
                    500,
                    0,
                    DecelerateInterpolator(3f)
                )
                UIElements.viewObjectAnimator(
                    binding.gradientScreenAnimationHero,
                    "alpha",
                    0f,
                    300,
                    0,
                    DecelerateInterpolator(3f)
                )
                UIElements.cardViewCornerRadiusAnimator(
                    binding.gradientScreenAnimationHero,
                    convertFloatToDP(this, 20f),
                    600,
                    0,
                    LinearInterpolator()
                )
                growFrag(binding.fragmentHolder, 1f, 400, DecelerateInterpolator(3f))

                /** Tells app sharedElement can be dismissed **/
                Handler(Looper.getMainLooper()).postDelayed({
                    UIElements.viewObjectAnimator(
                        binding.gradientScreenAnimationHero,
                        "scaleX",
                        1f,
                        0,
                        0,
                        LinearInterpolator()
                    )
                    UIElements.viewObjectAnimator(
                        binding.gradientScreenAnimationHero,
                        "scaleY",
                        1f,
                        0,
                        0,
                        LinearInterpolator()
                    )
                    UIElements.viewVisibility(binding.gradientScreenAnimationHero, View.GONE, 0)
                    UIElements.viewObjectAnimator(
                        binding.gradientScreenAnimationHero,
                        "alpha",
                        1f,
                        0,
                        0,
                        LinearInterpolator()
                    )
                    Values.canDismissSharedElement = true
                    Values.animatingSharedElement = false
                }, 600)
            } else {
                /** RUNS IF EXITING SPLITSCREEN **/
                UIElements.viewWidthAnimator(
                    binding.fragmentHolder,
                    binding.fragmentHolder.width.toFloat(),
                    getScreenMetrics(this, window).width.toFloat(),
                    500,
                    0,
                    DecelerateInterpolator(3f)
                )
                UIElements.viewObjectAnimator(
                    binding.separator,
                    "translationX",
                    0f,
                    500,
                    0,
                    DecelerateInterpolator(3f)
                )
                UIElements.viewObjectAnimator(
                    binding.fragmentHolderSecondary,
                    "translationX",
                    0f,
                    500,
                    0,
                    DecelerateInterpolator(3f)
                )
                if (Values.isSearchMode) {
                    (browseFragment as FragBrowse).setSBCRecyclerWidth(true)
                }

                /** Tells app it can reopen SplitScreen **/
                Handler(Looper.getMainLooper()).postDelayed({
                    Values.canDismissSharedElement = true
                    Values.animatingSharedElement = false
                }, 500)
            }
        }
    }

    private fun hideGradientCreator() {
        /** Set initial properties of SharedElement **/
        UIElement.gradientDrawableNew(
            this,
            binding.sharedElementGradientViewer,
            Values.gradientCreatorColours,
            0f
        )
        UIElements.cardViewCornerRadiusAnimator(
            binding.gradientScreenAnimationHero,
            0f,
            0,
            0,
            LinearInterpolator()
        )
        UIElements.viewWidthAnimator(
            binding.gradientScreenAnimationHero,
            0f,
            getScreenMetrics(this, window).width.toFloat(),
            0,
            0,
            LinearInterpolator()
        )
        UIElements.viewHeightAnimator(
            binding.gradientScreenAnimationHero,
            0f,
            getScreenMetrics(this, window).height.toFloat(),
            0,
            0,
            LinearInterpolator()
        )
        UIElements.viewObjectAnimator(
            binding.gradientScreenAnimationHero,
            "translationX",
            0f,
            0,
            0,
            LinearInterpolator()
        )
        UIElements.viewVisibility(binding.gradientScreenAnimationHero, View.VISIBLE, 0)
        shrinkFrag(binding.fragmentHolder, 0.9f, 0, LinearInterpolator())

        UIElements.viewObjectAnimator(
            binding.fragmentHolderSecondary, "translationX",
            0f, 0, 0, LinearInterpolator()
        )

        /** Animate SharedElement **/
        UIElements.viewObjectAnimator(
            binding.gradientScreenAnimationHero,
            "scaleX",
            0.3f,
            500,
            0,
            DecelerateInterpolator(3f)
        )
        UIElements.viewObjectAnimator(
            binding.gradientScreenAnimationHero,
            "scaleY",
            0.3f,
            500,
            0,
            DecelerateInterpolator(3f)
        )
        UIElements.viewObjectAnimator(
            binding.gradientScreenAnimationHero,
            "alpha",
            0f,
            300,
            0,
            DecelerateInterpolator(3f)
        )
        UIElements.cardViewCornerRadiusAnimator(
            binding.gradientScreenAnimationHero,
            convertFloatToDP(this, 20f),
            600,
            0,
            LinearInterpolator()
        )
        growFrag(binding.fragmentHolder, 1f, 400, DecelerateInterpolator(3f))

        /** Tells app sharedElement can be dismissed **/
        Handler(Looper.getMainLooper()).postDelayed({
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "scaleX",
                1f,
                0,
                0,
                LinearInterpolator()
            )
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "scaleY",
                1f,
                0,
                0,
                LinearInterpolator()
            )
            UIElements.viewVisibility(binding.gradientScreenAnimationHero, View.GONE, 0)
            UIElements.viewObjectAnimator(
                binding.gradientScreenAnimationHero,
                "alpha",
                1f,
                0,
                0,
                LinearInterpolator()
            )
            Values.canDismissSharedElement = true
            Values.animatingSharedElement = false
        }, 600)
    }

    @SuppressLint("NewApi")
    fun popupDialogHandler(dialogName: String, position: Int) {
        when (dialogName) {
            "settingTheme" -> {
                val current = Values.settingTheme
                when (position) {
                    0 -> {
                        //Light
                        Values.settingTheme = "light"
                    }

                    1 -> {
                        //Dark
                        Values.settingTheme = "dark"
                    }

                    2 -> {
                        //Darker
                        Values.settingTheme = "darker"
                    }
                }
                ////dialogPopupHider()
                Handler(Looper.getMainLooper()).postDelayed({
                    if (current != Values.settingTheme) {
                        hideSmallScreen()
                        refreshTheme()
                    }
                }, 450)
            }

            "settingVibration" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingVibration = true
                    }

                    1 -> {
                        //Off
                        Values.settingVibration = false
                    }
                }
                //dialogPopupHider()
            }

            "settingSpecialEffects" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingSpecialEffects = true
                    }

                    1 -> {
                        //Off
                        Values.settingSpecialEffects = false
                    }
                }
            }

            "settingSplitScreen" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingSplitScreen = true
                    }

                    1 -> {
                        //Off
                        Values.settingSplitScreen = false
                    }
                }
                //dialogPopupHider()
            }

            "leave" -> {
                when (position) {
                    0 -> {
                        finishAndRemoveTask()
                    }
                    //1 -> //dialogPopupHider()
                }
            }

            "noConnection" -> {
                when (position) {
                    0 -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            Connection.checkConnection(this, this)
                        }, Values.dialogShowAgainTime)
                    }

                    1 -> {
                        Values.downloadingGradients = false
                    }
                }
            }

            "setWallpaper" -> {
                val wallpaperManager = WallpaperManager.getInstance(this)
                when (position) {
                    0 -> {
                        //dialogPopupHider()
                        try {
                            Handler(Looper.getMainLooper()).postDelayed({
                                wallpaperManager.setBitmap(
                                    createBitmap(
                                        UIElement.gradientDrawableNew(
                                            this,
                                            null,
                                            Values.gradientScreenColours,
                                            0f
                                        ) as Drawable,
                                        getScreenMetrics(this, window).width,
                                        getScreenMetrics(this, window).height
                                    ),
                                    null, true, WallpaperManager.FLAG_SYSTEM
                                )
                                (gradientFragment as FragExpandedGradient).runNotification(
                                    R.drawable.icon_wallpaper,
                                    R.string.sentence_enjoy_your_wallpaper
                                )
                            }, 250)
                        } catch (e: Exception) {
                            Log.e(
                                "ERR",
                                "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper: ${e.localizedMessage}"
                            )
                        }
                    }

                    1 -> {
                        //dialogPopupHider()
                        try {
                            Handler(Looper.getMainLooper()).postDelayed({
                                wallpaperManager.setBitmap(
                                    createBitmap(
                                        UIElement.gradientDrawableNew(
                                            this,
                                            null,
                                            Values.gradientScreenColours,
                                            0f
                                        ) as Drawable,
                                        getScreenMetrics(this, window).width,
                                        getScreenMetrics(this, window).height
                                    ),
                                    null, true, WallpaperManager.FLAG_LOCK
                                )
                                (gradientFragment as FragExpandedGradient).runNotification(
                                    R.drawable.icon_wallpaper,
                                    R.string.sentence_enjoy_your_wallpaper
                                )
                            }, 250)
                        } catch (e: Exception) {
                            Log.e(
                                "ERR",
                                "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper: ${e.localizedMessage}"
                            )
                        }
                    }
                    //2 -> //dialogPopupHider()
                }
            }

            "setWallpaperOutdated" -> {
                val wallpaperManager = WallpaperManager.getInstance(this)
                when (position) {
                    0 -> {
                        //dialogPopupHider()
                        try {
                            wallpaperManager.setBitmap(
                                createBitmap(
                                    UIElement.gradientDrawableNew(
                                        this,
                                        null,
                                        Values.gradientScreenColours,
                                        0f
                                    ) as Drawable,
                                    getScreenMetrics(this, window).width,
                                    getScreenMetrics(this, window).height
                                )
                            )
                            (gradientFragment as FragExpandedGradient).runNotification(
                                R.drawable.icon_wallpaper,
                                R.string.sentence_enjoy_your_wallpaper
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "ERR",
                                "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper_outdated: ${e.localizedMessage}"
                            )
                        }
                    }
                    //1 -> //dialogPopupHider()
                }
            }

            "serverError" -> {
                when (position) {
                    0 -> {
                        Connection.checkConnection(this, this)
                        connectionChecker()
                    }

                    1 -> {
                        if (Values.gradientList.isEmpty()) {
                            finish()
                        }
                    }
                }
            }
        }
        Values.saveValues(this)
    }

    fun connectionChecker() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.gradientList.isNotEmpty()) {
                ((browseFragment as FragBrowse).showGradients())
            } else {
                connectionChecker()
            }
        }, 50)
    }


    override fun onBackPressed() {
        val fm = supportFragmentManager
        Log.e("INFO", Values.currentActivity)
        when (Values.currentActivity) {
            "Browse" -> {
                Values.dialogPopup = DialogPopup.newDialog(
                    arrayYesCancel(), "leave", R.drawable.icon_door, R.string.word_leave,
                    null, R.string.question_leave, null
                )
                Values.dialogPopup.show(fm, "leave")
            }

            "Search" -> {
                //closeSecondary()
            }
        }
    }

    fun getFragmentWidth(): Float {
        return binding.fragmentHolder.measuredWidth.toFloat()
    }

    override fun onResume() {
        super.onResume()
        if (Values.valuesLoaded) {
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Values.currentlySplitScreened = false
                when (Values.currentActivity) {
                    "GradientScreen" -> {
                        startGradientScreen(false)
                    }
                }
            } else {
                Values.currentlySplitScreened = false
                when (Values.currentActivity) {
                    "GradientScreen" -> {
                        startGradientScreen(false)
                    }
                }
            }
            if (Values.isSearchMode) {
                (browseFragment as FragBrowse).startSearch(false)
            }
            if (Values.currentActivity == "GradientCreator") {
                growFrag(binding.fragmentHolder, 1f, 500, DecelerateInterpolator(3f))
                hideGradientCreator()
                Values.currentActivity = "Browse"

                if (Values.justSubmitted) {
                    Values.justSubmitted = false
                    Values.browseRecyclerScrollPos = 0
                    Connection.checkConnection(this, this)
                    connectionChecker()
                }
                if (!(browseFragment as FragBrowse).areGradientsShowing()) {
                    (browseFragment as FragBrowse).showGradients()
                }
            } else {
                (browseFragment as FragBrowse).showGradients()
            }
        }
    }
}