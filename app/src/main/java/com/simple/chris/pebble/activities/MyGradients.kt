package com.simple.chris.pebble.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.simple.chris.pebble.*
import com.simple.chris.pebble.adapters_helpers.MyGradientsRecyclerView
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SQLiteHelper
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_my_gradients.*
import kotlinx.android.synthetic.main.activity_support.backButton
import kotlinx.android.synthetic.main.activity_support.bottomSheet
import kotlinx.android.synthetic.main.activity_support.buttonIcon
import kotlinx.android.synthetic.main.activity_support.coordinatorLayout
import kotlinx.android.synthetic.main.activity_support.titleHolder
import kotlinx.android.synthetic.main.activity_support.wallpaperImageAlpha
import kotlinx.android.synthetic.main.activity_support.wallpaperImageViewer
import java.util.HashMap

class MyGradients : AppCompatActivity(), PopupDialogButtonRecycler.OnButtonListener, MyGradientsRecyclerView.OnGradientListener {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var bottomSheetPeekHeight = 0

    /**
     * MyGradients Activity - Shows users their gradients, allows them to view or submit takedown
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_my_gradients)
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            recycler()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

    }

    /**
     * Get/Set bottomSheetPeekHeight based on screen height and other elements
     */
    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height")

            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()

            titleHolder.translationY = (((screenHeight * (0.333) - titleHolder.measuredHeight) / 2).toFloat())
            buttonIcon.translationY = (((screenHeight * (0.333) - titleHolder.measuredHeight) / 8).toFloat())
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.get_screen_height: ${e.localizedMessage}")
        }
    }

    /**
     * Sets anything related to the bottomSheet
     */
    private fun bottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomSheetPeekHeight = Calculations.screenMeasure(this, "height")
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2).toFloat()
                buttonIcon.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 8).toFloat()
                val cornerRadius = ((slideOffset * -1) + 1) * Calculations.convertToDP(this@MyGradients, 20f)
                val bottomShe = findViewById<CardView>(R.id.bottomSheet)
                bottomShe.radius = cornerRadius
            }
        })

    }

    fun deleteGradient(context: Context, gradientUID: String) {
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec"

        val stringRequest: StringRequest = object : StringRequest(Method.POST, gradientDatabaseURL,
                Response.Listener {

                },
                Response.ErrorListener {
                }) {
            override fun getParams(): MutableMap<String, String> {
                val details: MutableMap<String, String> = HashMap()
                details["action"] = "deleteGradients"
                details["gradientUID"] = gradientUID
                return details
            }
        }

        val retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = retryPolicy

        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun recycler() {
        myGradientsGrid.setHasFixedSize(true)
        val buttonLayoutManager = GridLayoutManager(this, 2)
        val buttonAdapter = MyGradientsRecyclerView(this, SQLiteHelper(this).readGradients(), this)

        myGradientsGrid.layoutManager = buttonLayoutManager
        myGradientsGrid.adapter = buttonAdapter
    }

    /*override fun onBackPressed() {
        //UIElement.dialogsToShow.clear()
        //UIElement.popupDialogHider()

        Handler().postDelayed({
            this.onBackPressed()
        }, 120)
    }*/

    //Called when Activity Pauses and Finishes
    override fun onPause() {
        super.onPause()
        Values.saveValues(this)
    }

    override fun onResume() {
        super.onResume()
        /**
         * Checks if app settings unloaded during pause
         */
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            Values.saveValues(this)
        }
    }

    override fun onGradientClick(position: Int, view: View) {
        when (view.id) {
            R.id.removeText -> {
                Vibration.strongFeedback(this)
                Toast.makeText(this, "Remove Gradient", Toast.LENGTH_SHORT).show()
                UIElement.popupDialog(this, "requestDelete", R.drawable.icon_delete, R.string.word_storage, null, R.string.sentence_needs_storage_permission, HashMaps.arraySureNotThisTimeDontAsk(),
                        window.decorView, this)
            }
            R.id.viewText -> {
                Vibration.mediumFeedback(this)
                try {
                    val details = Intent(this, GradientDetailsActivity::class.java)
                    val gradientInfo = SQLiteHelper(this).readGradients()[position]

                    details.putExtra("gradientName", gradientInfo["gradientName"])
                    details.putExtra("startColour", gradientInfo["startColour"])
                    details.putExtra("endColour", gradientInfo["endColour"])
                    details.putExtra("description", gradientInfo["description"])

                    /**
                     * View Hierarchy
                     * root
                     *    -> (holder)
                     *       -> removeText
                     *       -> viewText
                     *    -> gradient
                     *
                     *    Call .parent twice to get 'gradient' inside 'root'
                     */
                    val parent = view.parent.parent as ViewGroup
                    val gradientView: ImageView = parent.findViewById(R.id.gradient)
                    val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, gradientView, gradientInfo["gradientName"] as String)
                    startActivity(details, activityOptions.toBundle())
                } catch (e: Exception) {
                    Log.e("ERR", "pebble.my_gradients.on_gradient_click: ${e.localizedMessage}")
                }
            }
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "requestDelete" -> {
                when (position) {
                    0 -> {
                        //Yes
                        //deleteGradient(this, view.tag.toString())
                        //Log.e("INFO", view.tag)
                    }
                    1 -> {
                        //Cancel
                    }
                }
            }
        }
    }

}
