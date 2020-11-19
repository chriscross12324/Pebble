package com.simple.chris.pebble.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
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
import kotlinx.android.synthetic.main.activity_browse.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.gradientCreatorSharedElementView
import kotlinx.android.synthetic.main.activity_main.ssDescription
import kotlinx.android.synthetic.main.activity_main.ssIcon
import kotlinx.android.synthetic.main.activity_main.ssRecycler
import kotlinx.android.synthetic.main.activity_main.ssTitle
import kotlinx.android.synthetic.main.activity_main.wallpaperImageAlpha
import kotlinx.android.synthetic.main.activity_main.wallpaperImageViewer
import kotlinx.android.synthetic.main.small_screen.*

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


        browseFragment = BrowseFrag()
        searchFragment = SearchFrag()
        gradientFragment = GradientFrag()

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
        Handler().postDelayed({
            if (Values.gradientList.isNotEmpty()) {
                (browseFragment as BrowseFrag).showGradients()
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

    fun shrinkFrag(fragment: CardView) {
        UIElements.viewObjectAnimator(fragment, "scaleY", 0.7f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(fragment, "scaleX", 0.7f, 500, 0, DecelerateInterpolator(3f))
        UIElement.cardRadiusAnimator(fragment, Calculations.convertToDP(this, 30f), 500, 0, DecelerateInterpolator(3f))
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

    private fun growFrag(fragment: CardView) {
        UIElements.viewObjectAnimator(fragment, "scaleY", 1f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(fragment, "scaleX", 1f, 500, 0, DecelerateInterpolator(3f))
        UIElement.cardRadiusAnimator(fragment, 0f, 500, 0, DecelerateInterpolator(3f))
    }

    private fun moveDownFrag() {
        UIElements.viewObjectAnimator(fragmentHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
    }

    fun showSmallScreen(height: Float) {
        tapReturn.visibility = View.VISIBLE
        tapReturnText.visibility = View.VISIBLE
        shrinkFrag(fragmentHolder)
        moveUpFrag(height)
        UIElements.viewObjectAnimator(smallScreenFragHolder, "translationY", -height, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(tapReturn, "alpha", 0.6f, 250, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(tapReturnText, "alpha", 1f, 250, 0, LinearInterpolator())
        tapReturnText.translationY = (-height / 2)
    }

    fun hideSmallScreen() {
        growFrag(fragmentHolder)
        moveDownFrag()
        UIElements.viewObjectAnimator(smallScreenFragHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(tapReturn, "alpha", 0f, 150, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(tapReturnText, "alpha", 0f, 150, 0, LinearInterpolator())
        Handler().postDelayed({
            tapReturn.visibility = View.GONE
            tapReturnText.visibility = View.GONE
        }, 150)
        //onBackPressed()
    }

    fun startGradientCreator() {
        shrinkFrag(fragmentHolder)
        Handler().postDelayed({
            if (Values.connectionOffline) {
                UIElement.popupDialog(this, "noConnection", R.drawable.icon_wifi_empty, R.string.dual_no_connection, null, R.string.sentence_needs_internet_connection,
                        HashMaps.noConnectionArrayList(), window.decorView, this)
            } else {
                val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, gradientCreatorSharedElementView, "gradientCreatorViewer")
                startActivity(Intent(this, GradientCreator::class.java), activityOptions.toBundle())
            }
        }, 0)
    }

    fun startSearch() {
        fragmentHolderSecondary.translationX = fragmentHolderSecondary.width.toFloat()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolderSecondary, searchFragment)
                .commitAllowingStateLoss()
        moveFragLeft(fragmentHolder.width.toFloat(), fragmentHolder)
        moveFragLeft(0f, fragmentHolderSecondary)
        shrinkFrag(fragmentHolder)
        shrinkFrag(fragmentHolderSecondary)
        Handler().postDelayed({
            growFrag(fragmentHolder)
            growFrag(fragmentHolderSecondary)
            Values.currentActivity = "Search"
        }, 500)
    }

    fun closeSearch() {
        moveFragRight(0f, fragmentHolder)
        moveFragRight(fragmentHolder.width.toFloat(), fragmentHolderSecondary)
        shrinkFrag(fragmentHolder)
        shrinkFrag(fragmentHolderSecondary)
        Handler().postDelayed({
            growFrag(fragmentHolder)
            growFrag(fragmentHolderSecondary)
            Values.currentActivity = "Browse"
        }, 500)
    }

    fun refreshTheme() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentHolder)
        (browseFragment as BrowseFrag).gridToTop()
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        if (Values.currentActivity == "CreateGradient") {
            if (Values.justSubmitted) {
                gradientsDownloaded()
                Values.justSubmitted = false
            }
            growFrag(fragmentHolder)
        }
        Values.currentActivity = "MainActivity"
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
                Handler().postDelayed({
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
                        Handler().postDelayed({
                            Connection.checkConnection(this, window.decorView, this)
                        }, Values.dialogShowAgainTime)
                    }
                }
            }
            "noConnection" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
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
        Handler().postDelayed({
            if (Values.gradientList.isNotEmpty()) {
                ((browseFragment as BrowseFrag).showGradients())
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
                closeSearch()
            }
        }

    }
}