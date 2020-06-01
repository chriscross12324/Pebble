package com.simple.chris.pebble

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_connecting.*
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_main_menu)
        initialPlacement()
        setWallpaperBlur()
        getGradients()

        pebbleLogo.setOnClickListener {
            startActivity(Intent(this, ConnectingActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun initialPlacement() {
        pebbleLogo.translationY = Calculations.screenMeasure(this, "height") / 2 - Calculations.convertToDP(this, 90f) - Calculations.convertToDP(this, 32f)
        pebbleText.translationY = Calculations.convertToDP(this, 20f)
        UIElements.setWallpaper(this, wallpaperImageViewer)
        inAnimation()
    }

    private fun inAnimation() {
        //Animate pebbleLogo
        UIElements.viewObjectAnimator(pebbleLogo, "translationY", 0f, 500, 1000, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(pebbleLogo, "scaleX", 0.7f, 400, 950, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(pebbleLogo, "scaleY", 0.7f, 400, 950, DecelerateInterpolator(3f))

        //Animate pebbleText
        UIElements.viewObjectAnimator(pebbleText, "translationY", 0f, 500, 1200, DecelerateInterpolator())
        UIElements.viewObjectAnimator(pebbleText, "alpha", 1f, 150, 1200, DecelerateInterpolator())

        //Animate wallpaperAlpha
        UIElements.viewObjectAnimator(wallpaperAlpha, "alpha", 0.9f, 300, 1000, DecelerateInterpolator())
    }

    private fun setWallpaperBlur() {
        val radius = 20f

        val decorView: View = window.decorView
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(decorView.findViewById(android.R.id.content))
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true)
    }

    private fun getGradients() {
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
    }

    private fun connectionOnline() {
        connectionText.text = "Online"
        connectionProgress.visibility = View.INVISIBLE
        connectionIcon.visibility = View.VISIBLE
        connectionIcon.setImageResource(R.drawable.icon_check)
        UIElements.viewObjectAnimator(connectionHolder, "translationY", connectionHolder.height + Calculations.convertToDP(this, 24f), 500, 2000, DecelerateInterpolator(3f))
        //connectionHolder.backgroundTintList = this.resources.getColorStateList(R.color.connectionOnline)
    }

    override fun onResume() {
        super.onResume()
        UIElements.setWallpaper(this, wallpaperImageViewer)
    }
}
