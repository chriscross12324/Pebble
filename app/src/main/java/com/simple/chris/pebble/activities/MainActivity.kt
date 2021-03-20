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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.google.android.gms.ads.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.DialogChangelog
import com.simple.chris.pebble.adapters_helpers.DialogPopup
import com.simple.chris.pebble.adapters_helpers.SettingsRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.module_browse_normal.view.*
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : FragmentActivity(), SettingsRecyclerView.OnButtonListener {
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var mBillingClient: BillingClient
    private lateinit var mSkuDetails: SkuDetails
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
        setContentView(R.layout.activity_main)
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
        gradientFragment = FragGradientScreen()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, browseFragment)
            commit()
        }

        tapReturn.setOnClickListener {
            hideSmallScreen()
        }

        adMob()
        appError()
        //setupBillingClient()
    }

    override fun onAttachedToWindow() {
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
        fragmentHolder.post {
            Values.screenHeight = Calculations.screenMeasure(this, "height", window)
            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()
        }
    }

    fun startSettings() {
        if (currentSmallScreen != "settings") {
            currentSmallScreen = "settings"
            ssIcon.setImageResource(R.drawable.icon_settings)
            ssTitle.setText(R.string.word_settings)
            ssDescription.setText(R.string.sentence_customize_pebble)

            ssRecycler.setHasFixedSize(true)
            val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val buttonAdapter = SettingsRecyclerView(this, "settings", HashMaps.settingsArray(), this)

            ssRecycler.layoutManager = buttonLayoutManager
            ssRecycler.adapter = buttonAdapter
            smallScreenScrollBar(buttonLayoutManager)

            //buttonLayoutManager.scrollToPositionWithOffset(1, 50)
        }
        ssRecycler.post {
            showSmallScreen(smallScreenFragHolder.measuredHeight.toFloat())
        }
    }

    fun startDonating() {
        if (currentSmallScreen != "donate") {
            currentSmallScreen = "donate"
            ssIcon.setImageResource(R.drawable.icon_money)
            ssTitle.setText(R.string.word_donate)
            ssDescription.setText(R.string.sentence_donate)

            ssRecycler.setHasFixedSize(true)
            val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val buttonAdapter = SettingsRecyclerView(this, "donate", HashMaps.donateArray(), this)

            ssRecycler.layoutManager = buttonLayoutManager
            ssRecycler.adapter = buttonAdapter
            smallScreenScrollBar(buttonLayoutManager)
        }
        ssRecycler.post {
            showSmallScreen(smallScreenFragHolder.measuredHeight.toFloat())
        }
    }

    fun startAbout() {
        if (currentSmallScreen != "about") {
            currentSmallScreen = "about"
            ssIcon.setImageResource(R.drawable.icon_question)
            ssTitle.setText(R.string.word_about)
            ssDescription.setText(R.string.sentence_about)

            ssRecycler.setHasFixedSize(true)
            val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val buttonAdapter = SettingsRecyclerView(this, "about", HashMaps.aboutArray(), this)

            ssRecycler.layoutManager = buttonLayoutManager
            ssRecycler.adapter = buttonAdapter
            smallScreenScrollBar(buttonLayoutManager)
        }
        ssRecycler.post {
            showSmallScreen(smallScreenFragHolder.measuredHeight.toFloat())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun smallScreenScrollBar(layoutManager: LinearLayoutManager) {
        ssScrollbarWidth = smallScreenFragHolder.measuredWidth - Calculations.convertToDP(this, 70f)
        ssScrollbarArea = ssScrollbarWidth - ssScrollbar.measuredWidth
        UIElements.viewHeightAnimator(ssScrollbar, Calculations.convertToDP(this, 5f), Calculations.convertToDP(this, 2.55f), 0, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(ssScrollbar, "translationY", Calculations.convertToDP(this, 8f), 0, 0, DecelerateInterpolator(3f))
        ssScrollbarOffset = 0
        ssRecycler.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            ssScrollbarExtent = ssRecycler.computeHorizontalScrollExtent()
            ssScrollbarRange = ssRecycler.computeHorizontalScrollRange()
            scrollbarTranslationCalculator()
        }

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            Vibration.lowFeedback(this)
            UIElements.viewHeightAnimator(ssScrollbar, Calculations.convertToDP(this, 5f), Calculations.convertToDP(this, 2.55f), 500, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(ssScrollbar, "translationY", Calculations.convertToDP(this, 8f), 500, 0, DecelerateInterpolator(3f))
        }

        ssScrollbarTrigger.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e("INFO", "Down")
                    Vibration.mediumFeedback(this)
                    handler.removeCallbacks(runnable)
                    UIElements.viewHeightAnimator(ssScrollbar, ssScrollbar.measuredHeight.toFloat(), Calculations.convertToDP(this, 5f), 500, 0, DecelerateInterpolator(3f))
                    UIElements.viewObjectAnimator(ssScrollbar, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
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
                    val xIntStart = Calculations.convertToDP(this, 57.5f)
                    val xIntEnd = Calculations.screenMeasure(this, "width", window) - xIntStart
                    val xProgress = 0f.coerceAtLeast(100f.coerceAtMost((100 / (xIntEnd - xIntStart)) * (x - xIntStart)))
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
        ssScrollbarOffset = ssRecycler.computeHorizontalScrollOffset()
        val percent = (100f * ssScrollbarOffset / (ssScrollbarRange - ssScrollbarExtent))
        ssScrollbar.translationX = (ssScrollbarArea / 100) * percent
    }

    fun shrinkFrag(fragment: CardView, scale: Float, duration: Long, interpolator: Interpolator) {
        UIElements.viewObjectAnimator(fragment, "scaleY", scale, duration, 0, interpolator)
        UIElements.viewObjectAnimator(fragment, "scaleX", scale, duration, 0, interpolator)
        UIElement.cardRadiusAnimator(fragment, Calculations.convertToDP(this, 30f), duration, 0, interpolator)
    }

    private fun moveUpFrag(height: Float) {
        UIElements.viewObjectAnimator(fragmentHolder, "translationY", -(height * 0.8).toFloat(), 500, 0, DecelerateInterpolator(3f))
    }

    private fun moveFragLeft(width: Float, fragment: View) {
        UIElements.viewObjectAnimator(fragment, "translationX", -width, 500, 0, DecelerateInterpolator(3f))
    }

    private fun moveFragRight(width: Float, fragment: View) {
        UIElements.viewObjectAnimator(fragment, "translationX", width, 500, 0, DecelerateInterpolator(3f))
    }

    private fun growFrag(fragment: CardView, scale: Float, duration: Long, interpolator: Interpolator) {
        UIElements.viewObjectAnimator(fragment, "scaleY", scale, duration, 0, interpolator)
        UIElements.viewObjectAnimator(fragment, "scaleX", scale, duration, 0, interpolator)
        UIElement.cardRadiusAnimator(fragment, 0f, duration, 0, interpolator)
    }

    private fun moveDownFrag() {
        UIElements.viewObjectAnimator(fragmentHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
    }

    fun showSmallScreen(height: Float) {
        tapReturn.visibility = View.VISIBLE
        tapReturnText.visibility = View.VISIBLE
        shrinkFrag(fragmentHolder, 0.7f, 500, DecelerateInterpolator(3f))
        moveUpFrag(height)
        UIElements.viewObjectAnimator(smallScreenFragHolder, "translationY", -height, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(tapReturn, "alpha", 0.6f, 250, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(tapReturnText, "alpha", 1f, 250, 0, LinearInterpolator())
        tapReturnText.translationY = (-height / 2)
    }

    fun hideSmallScreen() {
        growFrag(fragmentHolder, 1f, 500, DecelerateInterpolator(3f))
        moveDownFrag()
        UIElements.viewObjectAnimator(smallScreenFragHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(tapReturn, "alpha", 0f, 150, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(tapReturnText, "alpha", 0f, 150, 0, LinearInterpolator())
        Handler(Looper.getMainLooper()).postDelayed({
            tapReturn.visibility = View.GONE
            tapReturnText.visibility = View.GONE
        }, 150)
        //onBackPressed()
    }

    fun startGradientCreator() {
        /** Check to see if GradientCreator already has colours **/
        if (Values.gradientCreatorColours.isEmpty()) {
            Values.gradientCreatorColours.add("#${Integer.toHexString(Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))).substring(2)}")
            Values.gradientCreatorColours.add("#${Integer.toHexString(Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))).substring(2)}")
        }

        shrinkFrag(fragmentHolder, 0.7f, 500, DecelerateInterpolator(3f))
        Values.animatingSharedElement = true
        Handler(Looper.getMainLooper()).postDelayed({
            /** Set initial properties of SharedElement **/
            UIElements.viewVisibility(gradientScreenAnimationHero, View.VISIBLE, 0)
            UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero,
                    Calculations.convertToDP(this, 20f), 0, 0, LinearInterpolator())
            UIElements.viewWidthAnimator(gradientScreenAnimationHero, 0f,
                    Calculations.convertToDP(this, 150f), 0, 0, LinearInterpolator())
            UIElements.viewHeightAnimator(gradientScreenAnimationHero, 0f,
                    Calculations.convertToDP(this, 150f), 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX",
                    (Calculations.screenMeasure(this, "width", window) - Calculations.convertToDP(this, 150f)) / 2,
                    0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationY",
                    Calculations.screenMeasure(this, "height", window) + Calculations.convertToDP(this, 150f),
                    0, 0, LinearInterpolator())
            UIElement.gradientDrawableNew(this, sharedElementGradientViewer, Values.gradientCreatorColours, 0f)

            /** Animate SharedElement **/
            UIElements.viewWidthAnimator(gradientScreenAnimationHero, gradientScreenAnimationHero.measuredWidth.toFloat(),
                    Calculations.screenMeasure(this, "width", window).toFloat(), Values.sharedElementLength, 0, DecelerateInterpolator(3f))
            UIElements.viewHeightAnimator(gradientScreenAnimationHero, gradientScreenAnimationHero.measuredHeight.toFloat(),
                    Calculations.screenMeasure(this, "height", window).toFloat(), Values.sharedElementLength, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX", 0f, Values.sharedElementLength, 0,
                    DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationY",
                    (Calculations.screenMeasure(this, "height", window) - gradientScreenAnimationHero.measuredHeight).toFloat() / 2,
                    Values.sharedElementLength, 0, DecelerateInterpolator(3f))
            UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero, 0f, Values.sharedElementLength - 100, 0, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, GradientCreate::class.java))
                overridePendingTransition(0, 0)
                Values.animatingSharedElement = false
            }, Values.sharedElementLength)
        }, 0)
    }

    fun closeSecondary() {
        if (Values.currentlySplitScreened) {
            Values.currentlySplitScreened = false
            //Toast.makeText(this, "Exiting SplitScreen", Toast.LENGTH_SHORT).show()
            //Is in SplitScreen mode
            UIElements.viewWidthAnimator(fragmentHolder, fragmentHolder.width.toFloat(),
                    Calculations.screenMeasure(this, "width", window).toFloat(), 500, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(separator, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
        } else {
            //Not in SplitScreen
            if (Values.currentActivity == "GradientScreen") {
                UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX", 0f, 0, 0, LinearInterpolator())
            } else {
                UIElements.viewObjectAnimator(fragmentHolder, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
                shrinkFrag(fragmentHolder, 0.9f, 100, DecelerateInterpolator(3f))
                shrinkFrag(fragmentHolderSecondary, 0.9f, 100, DecelerateInterpolator(3f))
                Handler(Looper.getMainLooper()).postDelayed({
                    growFrag(fragmentHolder, 1f, 200, AccelerateInterpolator())
                    growFrag(fragmentHolderSecondary, 1f, 200, AccelerateInterpolator())
                }, 100)
            }
        }
        Values.currentActivity = "Browse"
    }

    private fun refreshTheme() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentHolder)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
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
                        Values.dialogPopup = DialogPopup.newDialog(HashMaps.lightDarkDarker(), "settingTheme", R.drawable.icon_brush, R.string.word_theme,
                                null, R.string.question_setting_theme, null)
                        Values.dialogPopup.show(fm, "settingTheme")
                    }
                    1 -> {
                        //Vibration
                        Values.dialogPopup = DialogPopup.newDialog(HashMaps.onOff(), "settingVibration", R.drawable.icon_vibrate, R.string.word_vibration,
                                null, R.string.question_setting_vibration, null)
                        Values.dialogPopup.show(fm, "settingVibration")
                    }
                    2 -> {
                        //Special Effects
                        Values.dialogPopup = DialogPopup.newDialog(HashMaps.onOff(), "settingSpecialEffects", R.drawable.icon_blur_on, R.string.dual_special_effects,
                                null, R.string.question_setting_blur, null)
                        Values.dialogPopup.show(fm, "settingSpecialEffects")
                    }
                    3 -> {
                        //Split Screen
                        Values.dialogPopup = DialogPopup.newDialog(HashMaps.onOff(), "settingSplitScreen", R.drawable.icon_split_screen, R.string.dual_split_screen,
                                null, R.string.question_setting_split_screen, null)
                        Values.dialogPopup.show(fm, "settingSplitScreen")
                    }
                    4 -> {
                        //Cellular Data
                        Values.dialogPopup = DialogPopup.newDialog(HashMaps.onOffAsk(), "settingNetwork", R.drawable.icon_cell_wifi, R.string.word_network,
                                null, R.string.question_setting_network, null)
                        Values.dialogPopup.show(fm, "settingNetwork")
                    }
                }
            }
            "donate" -> {
                when (position) {
                    0 -> {
                        Values.adLoading = true
                        mInterstitialAd.loadAd(AdRequest.Builder().build())
                        //UIElement.popupDialog(this, "loadingAd", null, R.string.dual_ad_loading, null, R.string.sentence_ad_loading, null, window.decorView, null)
                        val fm = supportFragmentManager
                        Values.dialogPopup = DialogPopup.newDialog(null, "loadingAd", null,
                                R.string.dual_ad_loading, null, R.string.sentence_ad_loading, null)
                        Values.dialogPopup.show(fm, "loadingAd")
                    }
                }
            }
            "about" -> {
                when (position) {
                    /*3 -> {
                        val dialogChangelog = DialogChangelog.newDialog(null)
                        dialogChangelog.show(fm, "loadingAd")
                    }*/
                }
            }
        }

    }

    fun startGradientScreen(animateNew: Boolean) {
        Log.e("INFO", "${Values.currentlySplitScreened}")
        if (!Values.currentlySplitScreened) {
            if (Calculations.splitScreenPossible(this, window)) {
                openSplitScreen(true)
            } else {
                openFullScreen(false)
            }
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolderSecondary, gradientFragment)
                    .commitAllowingStateLoss()
            (gradientFragment as FragGradientScreen).startSplitScreen()
        } else {
            Log.e("INFO", "${Values.currentActivity}")
            if (Values.currentActivity != "GradientScreen") {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragmentHolderSecondary, gradientFragment)
                        .commitAllowingStateLoss()
                (gradientFragment as FragGradientScreen).startSplitScreen()
            } else {
                if (animateNew) {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragmentHolderSecondary, gradientFragment)
                            .commitAllowingStateLoss()
                    (gradientFragment as FragGradientScreen).startSplitScreen()
                } else {
                    (gradientFragment as FragGradientScreen).continueSplitScreen()
                }
            }
        }
    }

    fun startSearch() {
        //Toast.makeText(this, "${Values.currentlySplitScreened} : ${Values.currentActivity}", Toast.LENGTH_SHORT).show()
        Values.currentGradientScreenPos = -1
        if (!Values.currentlySplitScreened) {
            if (Calculations.splitScreenPossible(this, window)) {
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

    fun sharedElement(position: Int, view: View) {
        if (Values.currentGradientScreenPos == position) {
            Vibration.notification(this)
            shrinkFrag(fragmentHolderSecondary, 0.95f, 150, AccelerateDecelerateInterpolator())
            Handler(Looper.getMainLooper()).postDelayed({
                growFrag(fragmentHolderSecondary, 1f, 150, AccelerateDecelerateInterpolator())
                Handler(Looper.getMainLooper()).postDelayed({
                    Values.canDismissSharedElement = true
                    Values.animatingSharedElement = false
                }, 300)
            }, 150)
        } else {
            Values.currentGradientScreenPos = position
            /** Get scale/position of clicked Gradient **/
            gradientViewSizeX = view.gradient.width.toFloat()
            gradientViewSizeY = view.gradient.height.toFloat()
            view.gradient.getLocationOnScreen(gradientViewPos)

            /** Get scale/position of secondFrag **/
            if (Values.settingSplitScreen && Calculations.pxToIn(this, window) >= 4) {
                secondaryFragPosX = (Calculations.screenMeasure(this, "width", window) / 2) + (separator.width / 2).toFloat()
                secondaryFragScaleX = (Calculations.screenMeasure(this, "width", window) / 2) - (separator.width / 2).toFloat()
                secondaryFragScaleY = Calculations.screenMeasure(this, "height", window).toFloat()
            } else {
                secondaryFragPosX = 0f
                secondaryFragScaleX = Calculations.screenMeasure(this, "width", window).toFloat()
                secondaryFragScaleY = Calculations.screenMeasure(this, "height", window).toFloat()
            }

            /** Set values for FragGradientScreen in Values.kt **/
            if (Values.isSearchMode) {
                Values.gradientScreenName = Values.searchList[position]["gradientName"] as String
                Values.gradientScreenDesc = Values.searchList[position]["gradientDescription"] as String
                Values.gradientScreenColours = ArrayList(Values.searchList[position]["gradientColours"]!!.replace("[", "").replace("]", "")
                        .split(",").map { it.trim() })
            } else {
                Values.gradientScreenName = Values.gradientList[position]["gradientName"] as String
                Values.gradientScreenDesc = Values.gradientList[position]["gradientDescription"] as String
                Values.gradientScreenColours = ArrayList(Values.gradientList[position]["gradientColours"]!!.replace("[", "").replace("]", "")
                        .split(",").map { it.trim() })
            }
            startGradientScreen(false)

            /** Set initial properties of SharedElement **/
            UIElements.viewVisibility(gradientScreenAnimationHero, View.VISIBLE, 0)
            UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero, Calculations.convertToDP(this, 20f), 0, 0, LinearInterpolator())
            UIElements.viewWidthAnimator(gradientScreenAnimationHero, 0f, gradientViewSizeX, 0, 0, LinearInterpolator())
            UIElements.viewHeightAnimator(gradientScreenAnimationHero, 0f, gradientViewSizeY, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX", gradientViewPos[0].toFloat(), 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationY", gradientViewPos[1].toFloat(), 0, 0, LinearInterpolator())
            UIElement.gradientDrawableNew(this, sharedElementGradientViewer, Values.gradientScreenColours, 0f)

            /** Animate SharedElement **/
            UIElements.viewWidthAnimator(gradientScreenAnimationHero, gradientViewSizeX, secondaryFragScaleX, Values.sharedElementLength, 0, DecelerateInterpolator(3f))
            UIElements.viewHeightAnimator(gradientScreenAnimationHero, gradientViewSizeY, secondaryFragScaleY, Values.sharedElementLength, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX", secondaryFragPosX, Values.sharedElementLength, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationY", 0f, Values.sharedElementLength, 0, DecelerateInterpolator(3f))
            UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero, 0f, Values.sharedElementLength - 100, 0, LinearInterpolator())

            /** Tells app sharedElement can be dismissed **/
            Handler(Looper.getMainLooper()).postDelayed({
                Values.canDismissSharedElement = true
            }, Values.sharedElementLength)
        }
    }

    fun endSharedElement() {
        /** Called from FragGradientScreen.kt; hides sharedElement **/
        UIElements.viewVisibility(gradientScreenAnimationHero, View.GONE, 0)
        (gradientFragment as FragGradientScreen).showUI()
    }

    fun openSplitScreen(animation: Boolean) {
        separator.post {
            val duration: Long = if (animation) 500 else 0
            UIElements.viewWidthAnimator(fragmentHolderSecondary, fragmentHolderSecondary.width.toFloat(),
                    Calculations.screenMeasure(this, "width", window) / 2 - (separator.width / 2).toFloat(), 0, 0, LinearInterpolator())
            UIElements.viewWidthAnimator(fragmentHolder, fragmentHolder.width.toFloat(),
                    (Calculations.screenMeasure(this, "width", window) / 2) - (separator.width / 2).toFloat(), duration, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(separator, "translationX",
                    -((Calculations.screenMeasure(this, "width", window) / 2) + (separator.width / 2)).toFloat(), duration, 0, DecelerateInterpolator(3f))
            val secondaryPlacement = (Calculations.screenMeasure(this, "width", window) / 2) + (separator.width / 2).toFloat()
            UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX", -secondaryPlacement, duration, 0, DecelerateInterpolator(3f))
            Values.currentlySplitScreened = true

            if (Values.isSearchMode) {
                (browseFragment as FragBrowse).setSBCRecyclerWidth(true)
            }
        }
    }

    fun openFullScreen(animation: Boolean) {
        separator.post {
            if (animation) {
                fragmentHolderSecondary.layoutParams.width = Calculations.screenMeasure(this, "width", window)
                moveFragLeft(fragmentHolder.width.toFloat(), fragmentHolder)
                moveFragLeft((Calculations.screenMeasure(this, "width", window) + separator.width).toFloat(), fragmentHolderSecondary)
                shrinkFrag(fragmentHolder, 0.9f, 100, DecelerateInterpolator(3f))
                shrinkFrag(fragmentHolderSecondary, 0.9f, 100, DecelerateInterpolator(3f))
                Handler(Looper.getMainLooper()).postDelayed({
                    growFrag(fragmentHolder, 1f, 200, AccelerateInterpolator())
                    growFrag(fragmentHolderSecondary, 1f, 200, AccelerateInterpolator())
                    Values.currentActivity = "Search"
                }, 100)
            } else {
                shrinkFrag(fragmentHolder, 0.9f, 200, DecelerateInterpolator(3f))
                UIElements.viewWidthAnimator(fragmentHolderSecondary, fragmentHolderSecondary.width.toFloat(),
                        Calculations.screenMeasure(this, "width", window).toFloat(), 0, 0, LinearInterpolator())
                UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX",
                        -(Calculations.screenMeasure(this, "width", window) + separator.width).toFloat(), 0, 0, LinearInterpolator())
            }
        }
    }

    fun hideFullScreen(animation: Boolean) {
        if (animation) {
            Values.currentActivity = "Browse"
            UIElements.viewObjectAnimator(fragmentHolder, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
            shrinkFrag(fragmentHolder, 0.9f, 100, DecelerateInterpolator(3f))
            shrinkFrag(fragmentHolderSecondary, 0.9f, 100, DecelerateInterpolator(3f))
            Handler(Looper.getMainLooper()).postDelayed({
                growFrag(fragmentHolder, 1f, 200, AccelerateInterpolator())
                growFrag(fragmentHolderSecondary, 1f, 200, AccelerateInterpolator())
            }, 100)
        } else {
            //Gradient Screen
            Values.animatingSharedElement = true
            if (!Calculations.splitScreenPossible(this, window)) {
                //Shared Element
                /** Get scale/position of clicked Gradient **/
                gradientViewSizeX = Values.currentGradientScreenView.gradient.width.toFloat()
                gradientViewSizeY = Values.currentGradientScreenView.gradient.height.toFloat()
                Values.currentGradientScreenView.gradient.getLocationOnScreen(gradientViewPos)

                /** Get scale/position of secondFrag **/
                if (Values.settingSplitScreen && Calculations.pxToIn(this, window) >= 4) {
                    secondaryFragPosX = (Calculations.screenMeasure(this, "width", window) / 2) + (separator.width / 2).toFloat()
                    secondaryFragScaleX = (Calculations.screenMeasure(this, "width", window) / 2) - (separator.width / 2).toFloat()
                    secondaryFragScaleY = Calculations.screenMeasure(this, "height", window).toFloat()
                } else {
                    secondaryFragPosX = 0f
                    secondaryFragScaleX = Calculations.screenMeasure(this, "width", window).toFloat()
                    secondaryFragScaleY = Calculations.screenMeasure(this, "height", window).toFloat()
                }

                /** Set initial properties of SharedElement **/
                UIElement.gradientDrawableNew(this, sharedElementGradientViewer, Values.gradientScreenColours, 0f)
                UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero, 0f, 0, 0, LinearInterpolator())
                UIElements.viewWidthAnimator(gradientScreenAnimationHero, 0f, secondaryFragScaleX, 0, 0, LinearInterpolator())
                UIElements.viewHeightAnimator(gradientScreenAnimationHero, 0f, secondaryFragScaleY, 0, 0, LinearInterpolator())
                UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX", secondaryFragPosX, 0, 0, LinearInterpolator())
                UIElements.viewVisibility(gradientScreenAnimationHero, View.VISIBLE, 0)
                shrinkFrag(fragmentHolder, 0.9f, 0, LinearInterpolator())

                UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX",
                        0f, 0, 0, LinearInterpolator())

                /** Animate SharedElement **/
                UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleX", 0.3f, 500, 0, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleY", 0.3f, 500, 0, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(gradientScreenAnimationHero, "alpha", 0f, 300, 0, DecelerateInterpolator(3f))
                UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero, Calculations.convertToDP(this, 20f), 600, 0, LinearInterpolator())
                growFrag(fragmentHolder, 1f, 400, DecelerateInterpolator(3f))

                /** Tells app sharedElement can be dismissed **/
                Handler(Looper.getMainLooper()).postDelayed({
                    UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleX", 1f, 0, 0, LinearInterpolator())
                    UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleY", 1f, 0, 0, LinearInterpolator())
                    UIElements.viewVisibility(gradientScreenAnimationHero, View.GONE, 0)
                    UIElements.viewObjectAnimator(gradientScreenAnimationHero, "alpha", 1f, 0, 0, LinearInterpolator())
                    Values.canDismissSharedElement = true
                    Values.animatingSharedElement = false
                }, 600)
            } else {
                /** RUNS IF EXITING SPLITSCREEN **/
                UIElements.viewWidthAnimator(fragmentHolder, fragmentHolder.width.toFloat(),
                        Calculations.screenMeasure(this, "width", window).toFloat(), 500, 0, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(separator, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
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
        UIElement.gradientDrawableNew(this, sharedElementGradientViewer, Values.gradientCreatorColours, 0f)
        UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero, 0f, 0, 0, LinearInterpolator())
        UIElements.viewWidthAnimator(gradientScreenAnimationHero, 0f, Calculations.screenMeasure(this, "width", window).toFloat(), 0, 0, LinearInterpolator())
        UIElements.viewHeightAnimator(gradientScreenAnimationHero, 0f, Calculations.screenMeasure(this, "height", window).toFloat(), 0, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX", 0f, 0, 0, LinearInterpolator())
        UIElements.viewVisibility(gradientScreenAnimationHero, View.VISIBLE, 0)
        shrinkFrag(fragmentHolder, 0.9f, 0, LinearInterpolator())

        UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX",
                0f, 0, 0, LinearInterpolator())

        /** Animate SharedElement **/
        UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleX", 0.3f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleY", 0.3f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(gradientScreenAnimationHero, "alpha", 0f, 300, 0, DecelerateInterpolator(3f))
        UIElements.cardViewCornerRadiusAnimator(gradientScreenAnimationHero, Calculations.convertToDP(this, 20f), 600, 0, LinearInterpolator())
        growFrag(fragmentHolder, 1f, 400, DecelerateInterpolator(3f))

        /** Tells app sharedElement can be dismissed **/
        Handler(Looper.getMainLooper()).postDelayed({
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleX", 1f, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "scaleY", 1f, 0, 0, LinearInterpolator())
            UIElements.viewVisibility(gradientScreenAnimationHero, View.GONE, 0)
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "alpha", 1f, 0, 0, LinearInterpolator())
            Values.canDismissSharedElement = true
            Values.animatingSharedElement = false
        }, 600)
    }

    @SuppressLint("NewApi")
    fun popupDialogHandler(dialogName: String, position: Int) {
        val fm = supportFragmentManager
        when (dialogName) {
            "settingTheme" -> {
                val current = Values.settingThemes
                when (position) {
                    0 -> {
                        //Light
                        Values.settingThemes = "light"
                    }
                    1 -> {
                        //Dark
                        Values.settingThemes = "dark"
                    }
                    2 -> {
                        //Darker
                        Values.settingThemes = "darker"
                    }
                }
                ////dialogPopupHider()
                Handler(Looper.getMainLooper()).postDelayed({
                    if (current != Values.settingThemes) {
                        hideSmallScreen()
                        refreshTheme()
                    }
                }, 450)
            }
            "settingVibration" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingVibrations = true
                    }
                    1 -> {
                        //Off
                        Values.settingVibrations = false
                    }
                }
                //dialogPopupHider()
            }
            "settingSpecialEffects" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingsSpecialEffects = true
                    }
                    1 -> {
                        //Off
                        Values.settingsSpecialEffects = false
                    }
                }
                ////dialogPopupHider()
                UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
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
            "settingNetwork" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.useMobileData = "on"
                    }
                    1 -> {
                        //Ask Every-time
                        Values.useMobileData = "ask"
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
            "askMobile" -> {
                when (position) {
                    0 -> {
                        //Values.dialogPopup.dismiss()
                        Connection.getGradientsFireStore(this)
                        Log.e("INFO", "0")
                        //UIElement.popupDialogHider()
                    }
                    1 -> {
                        Values.useMobileData = "on"
                        //Values.dialogPopup.dismiss()
                        Connection.getGradientsFireStore(this)
                        Log.e("INFO", "1")
                        //UIElement.popupDialogHider()
                    }
                    2 -> {
                        //UIElement.popupDialogHider()
                        Handler(Looper.getMainLooper()).postDelayed({
                            Connection.checkConnection(this, this)
                            Log.e("INFO", "2")
                        }, Values.dialogShowAgainTime)
                    }
                }
            }
            "noConnection" -> {
                when (position) {
                    0 -> {
                        //UIElement.popupDialogHider()
                        Handler(Looper.getMainLooper()).postDelayed({
                            Connection.checkConnection(this, this)
                        }, Values.dialogShowAgainTime)
                    }
                    1 -> {
                        //UIElement.popupDialogHider()
                        Connection.connectionOffline(this)
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
                                wallpaperManager.setBitmap(Calculations.createBitmap(UIElement.gradientDrawableNew(this, null, Values.gradientScreenColours, 0f) as Drawable,
                                        Calculations.screenMeasure(this, "width", window), Calculations.screenMeasure(this, "height", window)),
                                        null, true, WallpaperManager.FLAG_SYSTEM)
                                (gradientFragment as FragGradientScreen).runNotification(R.drawable.icon_wallpaper, R.string.sentence_enjoy_your_wallpaper)
                            }, 250)
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper: ${e.localizedMessage}")
                        }
                    }
                    1 -> {
                        //dialogPopupHider()
                        try {
                            Handler(Looper.getMainLooper()).postDelayed({
                                wallpaperManager.setBitmap(Calculations.createBitmap(UIElement.gradientDrawableNew(this, null, Values.gradientScreenColours, 0f) as Drawable,
                                        Calculations.screenMeasure(this, "width", window), Calculations.screenMeasure(this, "height", window)),
                                        null, true, WallpaperManager.FLAG_LOCK)
                                (gradientFragment as FragGradientScreen).runNotification(R.drawable.icon_wallpaper, R.string.sentence_enjoy_your_wallpaper)
                            }, 250)
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper: ${e.localizedMessage}")
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
                            wallpaperManager.setBitmap(Calculations.createBitmap(UIElement.gradientDrawableNew(this, null, Values.gradientScreenColours, 0f) as Drawable,
                                    Calculations.screenMeasure(this, "width", window), Calculations.screenMeasure(this, "height", window)))
                            (gradientFragment as FragGradientScreen).runNotification(R.drawable.icon_wallpaper, R.string.sentence_enjoy_your_wallpaper)
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper_outdated: ${e.localizedMessage}")
                        }
                    }
                    //1 -> //dialogPopupHider()
                }
            }
            "appError" -> {
                when (position) {
                    0 -> {
                        startActivity(Intent(this, SplashScreen::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
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
            "thanksAdvertise" -> {
                Values.dialogPopup = DialogPopup.newDialog(HashMaps.restartArray(), "thanksAdvertise", R.drawable.icon_warning, R.string.word_error,
                        null, R.string.sentence_app_error, null)
                Values.dialogPopup.show(fm, "thanksAdvertise")
            }
        }
        Values.saveValues(this)
    }

    fun appError() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.errorOccurred) {
                Values.errorOccurred = false
                val fm = supportFragmentManager
                Values.dialogPopup = DialogPopup.newDialog(HashMaps.restartArray(), "appError", R.drawable.icon_warning, R.string.word_error,
                        null, R.string.sentence_app_error, null)
                Values.dialogPopup.show(fm, "appError")
            } else {
                appError()
            }
        }, 100)
    }

    private fun adMob() {
        MobileAds.initialize(this) { Values.adMobInitialized = true }
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                //Hide popup
                Values.adLoading = false
                mInterstitialAd.show()
                Values.dialogPopup.dismiss()
                //UIElement.popupDialogHider()
                //Show Ad
            }

            override fun onAdFailedToLoad(p0: LoadAdError?) {
                //Warn user
            }

            override fun onAdClosed() {

            }
        }

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
                Values.dialogPopup = DialogPopup.newDialog(HashMaps.arrayYesCancel(), "leave", R.drawable.icon_door, R.string.word_leave,
                        null, R.string.question_leave, null)
                Values.dialogPopup.show(fm, "leave")
            }
            "Search" -> {
                //closeSecondary()
            }
        }
    }

    fun getFragmentWidth(): Float {
        return fragmentHolder.measuredWidth.toFloat()
    }

    fun dialogPopupHider() {
        if (Values.dialogPopup.dialog != null) {
            Values.dialogPopup.onDismiss(Values.dialogPopup.dialog!!)
        }
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
                growFrag(fragmentHolder, 1f, 500, DecelerateInterpolator(3f))
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
        } else {
            appError()
        }
    }

    /*private fun setupBillingClient() {
        mBillingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build()
        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i("Information","pebble.billing_service: Connected")
                    loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i("Information","pebble.billing_service: Disconnected")
            }
        })
    }

    private fun loadAllSKUs() = if (mBillingClient.isReady) {
        val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(mSkuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
        mBillingClient.querySkuDetailsAsync(params) {billingResults, skuDetailsList ->
            if (billingResults.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {

                }
            }
        }
    } else {
        Log.i("Information","pebble.billing_service: Not Ready Yet")
    }*/

}