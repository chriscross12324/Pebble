package com.simple.chris.pebble.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.*
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SettingsRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.gradientCreatorSharedElementView
import kotlinx.android.synthetic.main.activity_main.ssDescription
import kotlinx.android.synthetic.main.activity_main.ssIcon
import kotlinx.android.synthetic.main.activity_main.ssRecycler
import kotlinx.android.synthetic.main.activity_main.ssTitle
import kotlinx.android.synthetic.main.activity_main.wallpaperImageAlpha
import kotlinx.android.synthetic.main.activity_main.wallpaperImageViewer

class MainActivity : FragmentActivity(), SettingsRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener {
    private lateinit var mInterstitialAd: InterstitialAd
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
    var secondaryFragPos = IntArray(2)

    /**
     * gradientsDownload
     * startSettings
     * startDonating
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_main)
        if (!Values.refreshTheme) {
            if (Values.gradientList.isEmpty()) {
                Connection.checkConnection(this, window.decorView, this)
                connectionChecker()
            }
        } else {
            Values.refreshTheme = false
        }


        browseFragment = FragBrowse()
        searchFragment = FragSearch()
        gradientFragment = FragGradientScreen()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, browseFragment)
            commit()
        }

        tapReturn.setOnClickListener {
            hideSmallScreen()
        }

        adMob()
    }

    override fun onAttachedToWindow() {
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
        fragmentHolder.post {
            Values.screenHeight = Calculations.screenMeasure(this, "height", window)
            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()
        }
    }

    private fun gradientsDownloaded() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.gradientList.isNotEmpty()) {
                (browseFragment as FragBrowse).showGradients()
            }
        }, 500)
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
        }
        ssRecycler.post {
            showSmallScreen(smallScreenFragHolder.measuredHeight.toFloat())
        }
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
        shrinkFrag(fragmentHolder, 0.7f, 500, DecelerateInterpolator(3f))
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.connectionOffline) {
                UIElement.popupDialog(this, "noConnection", R.drawable.icon_wifi_empty, R.string.dual_no_connection, null, R.string.sentence_needs_internet_connection,
                        HashMaps.noConnectionArrayList(), window.decorView, this)
            } else {
                val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, gradientCreatorSharedElementView, "gradientCreatorViewer")
                startActivity(Intent(this, GradientCreator::class.java), activityOptions.toBundle())
            }
        }, 0)
    }

    /*fun openGradientScreen(position: Int, view: View) {
        startSecondary(gradientFragment)
        passThroughVariables(position)
        (gradientFragment as GradientFrag).passThroughVariables()
        sharedElement(position, view)
    }

    fun startSec(fragment: Fragment, delay: Long, canAnim: Boolean, canBackStack: Boolean) {
        if (Values.currentlySplitScreened) {
            //
            when (Values.currentActivity) {

            }
        } else {
            if (Values.useSplitScreen && Calculations.pxToIn(this, window) >= 4) {
                replaceFrag(fragment, delay, canAnim, canBackStack)
                if (fragment == gradientFragment) {

                }
            }

        }
    }
    fun startSecondary(fragment: Fragment) {
        if (!Values.currentlySplitScreened) {
            Toast.makeText(this, "Not Split Screened", Toast.LENGTH_SHORT).show()
            //Not Split Screen Already
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolderSecondary, fragment)
                    .commitAllowingStateLoss()
            if (Values.useSplitScreen && Calculations.pxToIn(this, window) >= 4) {
                openSplitScreen(true)
            } else {
                Toast.makeText(this, "Full", Toast.LENGTH_SHORT).show()
                UIElements.viewWidthAnimator(fragmentHolderSecondary, fragmentHolderSecondary.width.toFloat(),
                        Calculations.screenMeasure(this, "width", window).toFloat(), 0, 0, LinearInterpolator())
                if (fragment == gradientFragment) {
                    separator.post {
                        UIElements.viewObjectAnimator(fragmentHolderSecondary, "translationX",
                                -Calculations.screenMeasure(this, "width", window).toFloat() - separator.width, 0, 0, LinearInterpolator())
                        (gradientFragment as GradientFrag).passThroughVariables()
                    }
                } else {
                    Toast.makeText(this, "Not gradientFragment", Toast.LENGTH_SHORT).show()
                    moveFragLeft(Calculations.screenMeasure(this, "width", window).toFloat(), fragmentHolder)
                    moveFragLeft(Calculations.screenMeasure(this, "width", window).toFloat() + separator.width, fragmentHolderSecondary)
                    shrinkFrag(fragmentHolder, 0.9f, 100, DecelerateInterpolator(3f))
                    shrinkFrag(fragmentHolderSecondary, 0.9f, 100, DecelerateInterpolator(3f))
                    Handler(Looper.getMainLooper()).postDelayed({
                        growFrag(fragmentHolder, 1f, 200, AccelerateInterpolator())
                        growFrag(fragmentHolderSecondary, 1f, 200, AccelerateInterpolator())
                    }, 100)
                }
            }
        } else {
            Toast.makeText(this, "Already Split Screened", Toast.LENGTH_SHORT).show()
            //Fade Instead
        }
    }

    fun openSplitScreen(animation: Boolean) {
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
    }

    fun hideSharedElementHero() {
        UIElements.viewWidthAnimator(gradientScreenAnimationHero, 0f, 0f, 0, 0, LinearInterpolator())
        UIElements.viewHeightAnimator(gradientScreenAnimationHero, 0f, 0f, 0, 0, LinearInterpolator())
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

    fun replaceFrag(fragment: Fragment, delay: Long, canAnim: Boolean, canBackStack: Boolean) {
        Handler(Looper.getMainLooper()).postDelayed({
            val manager = supportFragmentManager
            val ft = manager.beginTransaction()
            ft.replace(R.id.fragmentHolderSecondary, fragment)
            ft.commitAllowingStateLoss()
            if (canAnim) {
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            if (canBackStack) {
                ft.addToBackStack(backStack)
            }
        }, delay)
    }

    fun sharedElement(position: Int, view: View) {
        Handler(Looper.getMainLooper()).postDelayed({
            gradientViewSizeX = view.gradient.width.toFloat()
            gradientViewSizeY = view.gradient.height.toFloat()
            view.gradient.getLocationOnScreen(gradientViewPos)
            fragmentHolderSecondary.getLocationOnScreen(secondaryFragPos)
            val gradientColours = Values.gradientList[position]["gradientColours"]!!.replace("[", "").replace("]", "")
                    .split(",").map { it.trim() }
            var posX: Float
            var scaleX: Float
            if (Values.useSplitScreen && Calculations.pxToIn(this, window) >= 4) {
                posX = (Calculations.screenMeasure(this, "width", window) / 2) + (separator.width / 2).toFloat()
                scaleX = Calculations.screenMeasure(this, "width", window) / 2 - (separator.width / 2).toFloat()
            } else {
                posX = 0f
                scaleX = Calculations.screenMeasure(this, "width", window).toFloat()
            }
            UIElements.viewWidthAnimator(gradientScreenAnimationHero, 0f, gradientViewSizeX, 0, 0, LinearInterpolator())
            UIElements.viewHeightAnimator(gradientScreenAnimationHero, 0f, gradientViewSizeY, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX", gradientViewPos[0].toFloat(), 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationY", gradientViewPos[1].toFloat(), 0, 0, LinearInterpolator())
            UIElement.gradientDrawableNew(this, gradientScreenAnimationHero, ArrayList(gradientColours), 20f)
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationX", posX, 710, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(gradientScreenAnimationHero, "translationY", 0f, 710, 0, DecelerateInterpolator(3f))
            UIElements.viewWidthAnimator(gradientScreenAnimationHero, gradientViewSizeX, scaleX, 710, 0, DecelerateInterpolator(3f))
            UIElements.viewHeightAnimator(gradientScreenAnimationHero, gradientViewSizeY, fragmentHolderSecondary.height.toFloat(), 710, 0, DecelerateInterpolator(3f))
        }, 10)
    }

    fun returnSearchFragment(): Fragment {
        return searchFragment
    }

    fun returnGradientFragment(): Fragment {
        return gradientFragment
    }*/

    fun refreshTheme() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentHolder)
        (browseFragment as FragBrowse).gridToTop()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Portrait: ${Values.currentActivity}", Toast.LENGTH_SHORT).show()
            when (Values.currentActivity) {
                "GradientScreen" -> {

                }
                "Search" -> {

                }
            }
        } else {
            Toast.makeText(this, "Landscape: ${Values.currentActivity}", Toast.LENGTH_SHORT).show()
            when (Values.currentActivity) {
                "GradientScreen" -> {

                }
                "Search" -> {

                }
            }
        }
        /*if (Values.currentlySplitScreened) {
            Values.currentlySplitScreened = false
            //Toast.makeText(this, "Yes currently", Toast.LENGTH_SHORT).show()
            startSecondary(
                    when (Values.currentActivity) {
                        "Search" -> searchFragment
                        "GradientScreen" -> gradientFragment
                        else -> searchFragment
                    }
            )
        } else {
            //Toast.makeText(this, "No currently", Toast.LENGTH_SHORT).show()
        }*/
        when (Values.currentActivity) {
            "GradientCreator" -> {
                if (Values.justSubmitted) {
                    gradientsDownloaded()
                    Values.justSubmitted = false
                }
                growFrag(fragmentHolder, 1f, 500, DecelerateInterpolator(3f))
                Values.currentActivity = "Browse"
            }
            "Browse" -> {
                /*Toast.makeText(this, "Browse", Toast.LENGTH_SHORT).show()
                closeSearch()*/
            }
            "Search" -> {
                /*Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                startSearch()*/
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Toast.makeText(this, "${Values.currentActivity}", Toast.LENGTH_SHORT).show()
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                when (Values.currentActivity) {
                    "GradientScreen" -> {
                        Toast.makeText(this, "GradientScreen", Toast.LENGTH_SHORT).show()
                    }
                    "Search" -> {
                        Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                when (Values.currentActivity) {
                    "GradientScreen" -> {
                        Toast.makeText(this, "GradientScreen", Toast.LENGTH_SHORT).show()
                    }
                    "Search" -> {
                        Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> {

            }
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun onButtonClick(screenName: String, position: Int, view: View) {
        when (screenName) {
            "settings" -> {
                when (position) {
                    0 -> {
                        //Theme
                        UIElement.popupDialog(this, "settingTheme", R.drawable.icon_brush, R.string.word_theme, null, R.string.question_setting_theme,
                                HashMaps.lightDarkDarker(), window.decorView, this)
                    }
                    1 -> {
                        //Vibration
                        UIElement.popupDialog(this, "settingVibration", R.drawable.icon_vibrate_on, R.string.word_vibration, null, R.string.question_setting_vibration,
                                HashMaps.onOff(), window.decorView, this)
                    }
                    2 -> {
                        //Special Effects
                        UIElement.popupDialog(this, "settingSpecialEffects", R.drawable.icon_blur_on, R.string.dual_special_effects, null, R.string.question_setting_effects,
                                HashMaps.onOff(), window.decorView, this)
                    }
                    3 -> {
                        //Cellular Data
                        UIElement.popupDialog(this, "settingSplitScreen", R.drawable.split_screen, R.string.dual_split_screen, null, R.string.question_setting_split_screen,
                                HashMaps.onOff(), window.decorView, this)
                    }
                    4 -> {
                        //Cellular Data
                        UIElement.popupDialog(this, "settingNetwork", R.drawable.icon_cell_wifi, R.string.word_network, null, R.string.question_setting_network,
                                HashMaps.onOffAsk(), window.decorView, this)
                    }
                }
            }
            "donate" -> {
                when (position) {
                    0 -> {
                        mInterstitialAd.loadAd(AdRequest.Builder().build())
                        UIElement.popupDialog(this, "loadingAd", null, R.string.dual_ad_loading, null, R.string.sentence_ad_loading, null, window.decorView, null)
                    }
                }
            }
        }

    }

    fun sharedElement(position: Int, view: View) {

    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
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
                UIElement.popupDialogHider()
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
                UIElement.popupDialogHider()
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
                UIElement.popupDialogHider()
                UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
            }
            "settingSplitScreen" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.useSplitScreen = true
                    }
                    1 -> {
                        //Off
                        Values.useSplitScreen = false
                    }
                }
                UIElement.popupDialogHider()
            }
            "settingNetwork" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.useMobileData = "on"
                    }
                    1 -> {
                        //Off
                        Values.useMobileData = "off"
                    }
                    2 -> {
                        //Ask Every-time
                        Values.useMobileData = "ask"
                    }
                }
                UIElement.popupDialogHider()
            }
            "leave" -> {
                when (position) {
                    0 -> {
                        finishAndRemoveTask()
                    }
                    1 -> UIElement.popupDialogHider()
                }
            }
            "stillConnecting" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialog(this, "connecting", null, R.string.word_connecting, null, R.string.sentence_pebble_is_connecting, null, window.decorView, null)
                        Connection.checkDownload(this, window.decorView, this)
                    }
                    1 -> {
                        Connection.cancelConnection()
                        Connection.connectionOffline(this)
                    }
                    2 -> {
                        Connection.cancelConnection()
                        Connection.checkConnection(this, window.decorView, this)
                    }
                }
            }
            "askMobile" -> {
                when (position) {
                    0 -> {
                        Connection.getGradients(this, window.decorView, this)
                        UIElement.popupDialogHider()
                    }
                    1 -> {
                        Values.useMobileData = "on"
                        Connection.getGradients(this, window.decorView, this)
                        UIElement.popupDialogHider()
                    }
                    2 -> {
                        UIElement.popupDialogHider()
                        Handler(Looper.getMainLooper()).postDelayed({
                            Connection.checkConnection(this, window.decorView, this)
                        }, Values.dialogShowAgainTime)
                    }
                }
            }
            "noConnection" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Handler(Looper.getMainLooper()).postDelayed({
                            Connection.checkConnection(this, window.decorView, this)
                        }, Values.dialogShowAgainTime)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        Connection.connectionOffline(this)
                    }
                }
            }
        }
        Values.saveValues(this)
    }

    private fun adMob() {
        MobileAds.initialize(this) { Values.adMobInitialized = true }
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                //Hide popup
                mInterstitialAd.show()
                UIElement.popupDialogHider()
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
        }, 500)
    }

    override fun onBackPressed() {
        when (Values.currentActivity) {
            "Browse" -> {
                UIElement.popupDialog(this, "leave", R.drawable.icon_door, R.string.word_leave, null, R.string.question_leave, HashMaps.arrayYesCancel(), window.decorView, this)
            }
            "Search" -> {
                //closeSecondary()
            }
        }
    }

    fun passThroughVariables(position: Int) {
        val gradientInfo = Values.gradientList[position]

        Values.gradientScreenName = gradientInfo["gradientName"] as String
        Values.gradientScreenDesc = gradientInfo["gradientDescription"] as String
        val gradientColours = gradientInfo["gradientColours"]!!.replace("[", "").replace("]", "")
                .split(",").map { it.trim() }
        Values.gradientScreenColours = ArrayList(gradientColours)
    }

    fun getFragmentWidth(): Float {
        return fragmentHolderSecondary.measuredWidth.toFloat()
    }

    fun errorPopup() {

    }
}