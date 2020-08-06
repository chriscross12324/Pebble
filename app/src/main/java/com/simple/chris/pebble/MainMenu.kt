package com.simple.chris.pebble

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenu : AppCompatActivity(), MainMenuRecyclerViewAdapter.OnButtonListener, PopupDialogButtonRecyclerAdapter.OnButtonListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_main_menu)
        if (!Values.refreshTheme) {
            initialPlacement()
            if (Values.gradientList.isEmpty()) {
                Handler().postDelayed({
                    checkConnection()
                }, 1700)
            }
        } else {Values.refreshTheme = false; inAnimation()}
        menuButtons()

        pebbleLogo.setOnLongClickListener {
            startActivity(Intent(this, MainMenuTest::class.java))
            true
        }

        if (!Permissions.readStoragePermissionGiven(this)) {
            UIElements.oneButtonDialog(this, R.drawable.icon_storage, R.string.dialog_title_eng_permission_storage, R.string.dialog_body_eng_permission_storage_read, R.string.text_eng_ok, storageDialogListener)
            Log.e("ERR", "Made it")
        } else {
            Log.e("ERR", "Made else")
        }
    }

    private fun refreshTheme() {
        startActivity(Intent(this, MainMenu::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun initialPlacement() {
        pebbleLogo.translationY = Calculations.screenMeasure(this, "height") / 2 - Calculations.convertToDP(this, 90f) - Calculations.convertToDP(this, 32f)
        pebbleText.translationY = Calculations.convertToDP(this, 20f)
        menuButtonRecycler.translationY = Calculations.convertToDP(this, 200f)
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
        inAnimation()
    }

    private fun checkConnection() {
        when (Connection.getConnectionType(this)) {
            0 -> {
                UIElement.popupDialog(this, "noConnection", R.drawable.icon_warning, R.string.dialog_title_eng_no_connection, null,
                        R.string.dialog_body_eng_no_connection, AppHashMaps.noConnectionArrayList(), window.decorView, this)
            }
            1 -> {
                getGradients()
            }
            2 -> {
                if (Values.askMobileData) {
                    UIElement.popupDialog(this, "askMobile", R.drawable.icon_warning, R.string.dialog_title_eng_data_warning, null,
                            R.string.dialog_body_eng_data_warning, AppHashMaps.dataWarningArrayList(), window.decorView, this)
                } else {
                    getGradients()
                }
            }
        }
    }

    private fun inAnimation() {
        //Animate pebbleLogo
        UIElements.viewObjectAnimator(pebbleLogo, "translationY", 0f, 700, 1000, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(pebbleLogo, "scaleX", 0.7f, 700, 1000, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(pebbleLogo, "scaleY", 0.7f, 700, 1000, DecelerateInterpolator(3f))

        //Animate pebbleText
        UIElements.viewObjectAnimator(pebbleText, "translationY", Calculations.convertToDP(this, -12f), 500, 1200, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(pebbleText, "alpha", 1f, 500, 1200, DecelerateInterpolator())

        //Animate wallpaperAlpha
        UIElements.viewObjectAnimator(wallpaperImageAlpha, "alpha", 0.5f, 300, 1000, DecelerateInterpolator())

        UIElements.viewObjectAnimator(menuButtonRecycler, "translationY", 0f, 700, 1000, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(menuButtonRecycler, "alpha", 1f, 700, 1200, DecelerateInterpolator())
    }

    private fun getGradients() {
        /** Start connecting animation **/
        connectionText.text = "Connecting..."
        connectionProgress.visibility = View.VISIBLE
        connectionIcon.visibility = View.INVISIBLE
        UIElements.viewObjectAnimator(connectionHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))

        /** Start gradient database download **/
        Values.downloadingGradients = true
        val mQueue: RequestQueue = Volley.newRequestQueue(this)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getGradients"

        val request = JsonObjectRequest(Request.Method.GET, gradientDatabaseURL, null,
                Response.Listener { response ->
                    try {
                        val gradientArray = response.getJSONArray("items")
                        val gradientList = ArrayList<HashMap<String, String>>()

                        for (i in gradientArray.length() - 1 downTo 0) {
                            val downloadedItem = gradientArray.getJSONObject(i)

                            val item = HashMap<String, String>()
                            item["gradientName"] = downloadedItem.getString("gradientName")
                            item["startColour"] = downloadedItem.getString("startColour")
                            item["endColour"] = downloadedItem.getString("endColour")
                            item["description"] = downloadedItem.getString("description")

                            gradientList.add(item)
                            Values.gradientList = gradientList
                        }
                        connectionOnline()
                    } catch (e: Exception) {
                        Log.e("ERR", "pebble.main_menu.get_gradients: ${e.localizedMessage}")
                    }
                },
                Response.ErrorListener {
                    Log.e("ERR", "pebble.main_menu.get_gradients.request.error_listener: ${it.networkResponse}")
                })
        mQueue.add(request)
        request.retryPolicy = DefaultRetryPolicy(20000, 5, 1.25f)
    }

    private fun connectionOnline() {
        Values.downloadingGradients = false
        connectionText.text = "Online"
        connectionProgress.visibility = View.INVISIBLE
        connectionIcon.visibility = View.VISIBLE
        connectionIcon.setImageResource(R.drawable.icon_check)
        UIElements.viewObjectAnimator(connectionHolder, "translationY", connectionHolder.height + Calculations.convertToDP(this, 24f), 500, 1000, DecelerateInterpolator(3f))
    }

    private fun connectionOffline() {
        Values.downloadingGradients = false
        connectionText.text = "Offline"
        connectionProgress.visibility = View.INVISIBLE
        connectionIcon.visibility = View.VISIBLE
        connectionIcon.setImageResource(R.drawable.icon_close)
        UIElements.viewObjectAnimator(connectionHolder, "translationY", connectionHolder.height + Calculations.convertToDP(this, 24f), 500, 1000, DecelerateInterpolator(3f))
    }

    override fun onResume() {
        super.onResume()
        if (Values.currentActivity != "GradientCreator") {
            UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
        }

        /**
         * Check if app settings unloaded during pause
         */
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            Values.saveValues(this)
            Values.currentActivity = "MainMenu"

            /** If gradient submitted, reloads database **/
            if (Values.justSubmitted) {
                Values.justSubmitted = false
                connectionText.text = "Connecting..."
                connectionIcon.visibility = View.INVISIBLE
                connectionProgress.visibility = View.VISIBLE
                UIElements.viewObjectAnimator(connectionHolder, "translation", 0f, 500, 0, DecelerateInterpolator(3f))
                //getGradients()
                checkConnection()
            }
        }

        if (Values.refreshTheme) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun menuButtons() {
        val buttonArray = ArrayList<HashMap<String, String>>()
        val buttonHash = HashMap<String, String>()
        buttonHash["buttonTitle"] = "Browse"
        buttonHash["buttonIcon"] = R.drawable.icon_browse.toString()
        buttonArray.add(buttonHash)

        val buttonHash1 = HashMap<String, String>()
        buttonHash1["buttonTitle"] = "Create"
        buttonHash1["buttonIcon"] = R.drawable.icon_apps.toString()
        buttonArray.add(buttonHash1)

        val buttonHash2 = HashMap<String, String>()
        buttonHash2["buttonTitle"] = "Feedback"
        buttonHash2["buttonIcon"] = R.drawable.icon_feedback.toString()
        buttonArray.add(buttonHash2)

        val buttonHash3 = HashMap<String, String>()
        buttonHash3["buttonTitle"] = "Settings"
        buttonHash3["buttonIcon"] = R.drawable.icon_settings.toString()
        buttonArray.add(buttonHash3)

        val buttonHash4 = HashMap<String, String>()
        buttonHash4["buttonTitle"] = "Refresh"
        buttonHash4["buttonIcon"] = R.drawable.icon_reload.toString()
        buttonArray.add(buttonHash4)

        menuButtonRecycler.setHasFixedSize(true)
        val buttonLayoutManager = GridLayoutManager(this, 2)
        val buttonAdapter = MainMenuRecyclerViewAdapter(this, buttonArray, this)

        menuButtonRecycler.layoutManager = buttonLayoutManager
        menuButtonRecycler.adapter = buttonAdapter
    }

    override fun onButtonClick(position: Int, view: View) {
        when (position) {
            0 -> {
                if (Values.downloadingGradients) {
                    UIElement.popupDialog(this, "null", null, R.string.dialog_title_eng_connecting, null, R.string.dialog_body_eng_connecting, null, window.decorView, null)
                    Handler().postDelayed({
                        UIElement.popupDialogHider()
                    }, 1500)
                } else {
                    if (Values.gradientList.isEmpty()) {
                        UIElement.popupDialog(this, "noConnection", R.drawable.icon_no_connection, R.string.dialog_title_eng_offline_mode, null, R.string.dialog_body_eng_offline_mode, AppHashMaps.offlineModeArrayList(), window.decorView, this)
                    } else {
                        startActivity(Intent(this, BrowseActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                }
            }
            1 -> {
                if (Values.downloadingGradients) {
                    UIElement.popupDialog(this, "null", null, R.string.dialog_title_eng_connecting, null, R.string.dialog_body_eng_connecting, null, window.decorView, null)
                    Handler().postDelayed({
                        UIElement.popupDialogHider()
                    }, 1500)
                } else {
                    val createGradientSharedOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, gradientCreatorSharedElementPlaceholder, "gradientCreatorViewer")
                    startActivity(Intent(this, GradientCreator::class.java), createGradientSharedOptions.toBundle())
                }
            }
            2 -> {
                startActivity(Intent(this, Feedback::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            3 -> {
                startActivity(Intent(this, Settings::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            4 -> {
                checkConnection()
            }
        }
    }

    private val storageDialogListener = View.OnClickListener {
        UIElements.oneButtonHider(this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "askMobile" -> {
                when (position) {
                    0 -> {
                        getGradients()
                        UIElement.popupDialogHider()
                    }
                    1 -> {
                        Values.askMobileData = false
                        getGradients()
                        UIElement.popupDialogHider()
                    }
                    2 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            checkConnection()
                        }, Values.dialogShowAgainTime)
                    }
                }
            }

            "noConnection" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            checkConnection()
                        }, Values.dialogShowAgainTime)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        connectionOffline()
                    }
                }
            }
        }
    }


}
