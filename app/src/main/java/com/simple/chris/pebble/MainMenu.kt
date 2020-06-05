package com.simple.chris.pebble

import android.Manifest
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenu : AppCompatActivity(), MainMenuRecyclerViewAdapter.OnButtonListener {

    var downloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_main_menu)
        initialPlacement()
        setWallpaperBlur()
        getGradients()
        menuButtons()

        pebbleLogo.setOnClickListener {
            when (Values.theme) {
                "light" -> Values.theme = "dark"
                "dark" -> Values.theme = "black"
                "black" -> Values.theme = "light"
            }

            startActivity(Intent(this, MainMenu::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        if (!Permissions.readStoragePermissionGiven(this)) {
            UIElements.oneButtonDialog(this, R.drawable.icon_storage, R.string.dialog_title_eng_permission_storage, R.string.dialog_body_eng_permission_storage_read, R.string.text_eng_ok, storageDialogListener)
            Log.e("ERR", "Made it")
        } else {
            Log.e("ERR", "Made else")
        }
    }

    private fun initialPlacement() {
        pebbleLogo.translationY = Calculations.screenMeasure(this, "height") / 2 - Calculations.convertToDP(this, 90f) - Calculations.convertToDP(this, 32f)
        pebbleText.translationY = Calculations.convertToDP(this, 20f)
        menuButtonRecycler.translationY = Calculations.convertToDP(this, 120f)
        UIElements.setWallpaper(this, wallpaperImageViewer)
        inAnimation()
    }

    private fun inAnimation() {
        //Animate pebbleLogo
        UIElements.viewObjectAnimator(pebbleLogo, "translationY", 0f, 700, 1000, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(pebbleLogo, "scaleX", 0.7f, 700, 1000, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(pebbleLogo, "scaleY", 0.7f, 700, 1000, DecelerateInterpolator(3f))

        //Animate pebbleText
        UIElements.viewObjectAnimator(pebbleText, "translationY", Calculations.convertToDP(this, -12f), 500, 1200, DecelerateInterpolator())
        UIElements.viewObjectAnimator(pebbleText, "alpha", 1f, 150, 1200, DecelerateInterpolator())

        //Animate wallpaperAlpha
        UIElements.viewObjectAnimator(wallpaperAlpha, "alpha", 0.9f, 300, 1000, DecelerateInterpolator())

        UIElements.viewObjectAnimator(menuButtonRecycler, "translationY", 0f, 700, 1000, DecelerateInterpolator())
        UIElements.viewObjectAnimator(menuButtonRecycler, "alpha", 1f, 150, 1200, DecelerateInterpolator())
    }

    private fun setWallpaperBlur() {
        val radius = 20f

        val decorView: View = window.decorView
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after blur is applied.
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(decorView.findViewById(android.R.id.content))
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true)
    }

    private fun getGradients() {
        downloading = true
        val mQueue: RequestQueue = Volley.newRequestQueue(this)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getGradients"

        val request = JsonObjectRequest(Request.Method.GET, gradientDatabaseURL, null,
                Response.Listener { response ->
                    try {
                        connectionText.text = getString(R.string.status_downloading)
                        val gradientArray = response.getJSONArray("items")
                        val gradientList = ArrayList<HashMap<String, String>>()

                        for (i in gradientArray.length() - 1 downTo 0) {
                            val downloadedItem = gradientArray.getJSONObject(i)

                            val item = HashMap<String, String>()
                            item["backgroundName"] = downloadedItem.getString("gradientName")
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
        downloading = false
        connectionText.text = "Online"
        connectionProgress.visibility = View.INVISIBLE
        connectionIcon.visibility = View.VISIBLE
        connectionIcon.setImageResource(R.drawable.icon_check)
        UIElements.viewObjectAnimator(connectionHolder, "translationY", connectionHolder.height + Calculations.convertToDP(this, 24f), 500, 1000, DecelerateInterpolator(3f))
        //connectionHolder.backgroundTintList = this.resources.getColorStateList(R.color.connectionOnline)
    }

    override fun onResume() {
        super.onResume()
        UIElements.setWallpaper(this, wallpaperImageViewer)
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
                if (downloading) {
                    Toast.makeText(this, "Downloading Gradients", Toast.LENGTH_SHORT).show()
                } else {
                    val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.buttonIcon), "buttonIcon")
                    startActivity(Intent(this, ActivityBrowse::class.java), activityOptions.toBundle())
                }
            }
            1 -> {
                startActivity(Intent(this, CreateGradientNew::class.java))
            }
            2 -> {
                startActivity(Intent(this, Feedback::class.java))
            }
            3 -> Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            4 -> {
                getGradients()
                connectionText.text = "Connecting..."
                connectionProgress.visibility = View.VISIBLE
                connectionIcon.visibility = View.INVISIBLE
                UIElements.viewObjectAnimator(connectionHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
                //connectionHolder.backgroundTintList = this.resources.getColorStateList(R.color.connectionOnline)
            }
        }
    }

    private val storageDialogListener = View.OnClickListener {
        UIElements.oneButtonHider(this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }
}
