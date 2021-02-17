package com.simple.chris.pebble.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FieldValue
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.GradientCreatorRecycler
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_gradient_create.*
import kotlinx.android.synthetic.main.activity_gradient_create.buttonAddColour
import kotlinx.android.synthetic.main.activity_gradient_create.buttonNext
import kotlinx.android.synthetic.main.activity_gradient_create.iconRemoveActive
import kotlinx.android.synthetic.main.activity_gradient_creator.*
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

class GradientCreate : AppCompatActivity(), GradientCreatorRecycler.OnButtonListener {

    lateinit var buttonAdapter: GradientCreatorRecycler
    var modeColourDelete = false
    var modeSubmitGradient = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_gradient_create)
        Values.currentActivity = "GradientCreator"

        startCreator()

        val gradient = hashMapOf(
                "gradientName" to "Test",
                "gradientColours" to Values.gradientCreatorColours.toString().replace(" ", ""),
                "gradientDescription" to "Test Description",
                "gradientCategories" to arrayListOf("empty"),
                "gradientTimestamp" to FieldValue.serverTimestamp()
        )
        Values.getFireStore().collection("gradientList")
                .add(gradient)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully pushed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error logged", Toast.LENGTH_SHORT).show()
                    Log.e("ERR", it.toString())
                }
        //push()
    }

    fun push() {
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec"

        val stringRequest: StringRequest = object : StringRequest(Method.POST, gradientDatabaseURL,
                Response.Listener { Log.e("INFO", "Done") },
                Response.ErrorListener {
                    /** Set Error Listener **/
                }) {
            override fun getParams(): MutableMap<String, String> {
                val details: MutableMap<String, String> = HashMap()
                details["action"] = "addGradientV3"
                details["gradientName"] = "Here"
                details["gradientColours"] = Values.gradientCreatorColours.toString().replace(" ", "")
                details["gradientDescription"] = "Descr"
                return details
            }
        }

        val retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = retryPolicy

        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    fun startCreator() {
        /** Wait for UI to populate **/
        Handler(Looper.getMainLooper()).postDelayed({
            if (recyclerGradientColours != null) {
                /** Set UI Elements **/
                if (Values.gradientCreatorColours.isEmpty()) {
                    Values.gradientCreatorColours.add("#${Integer.toHexString(Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))).substring(2)}")
                    Values.gradientCreatorColours.add("#${Integer.toHexString(Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))).substring(2)}")
                }
                recyclerGradientColours()
                setViewStartingLocations()
                buttonFunctionality()
            }

        }, 50)
    }

    private fun buttonFunctionality() {
        buttonBack.setOnClickListener {
            Vibration.lowFeedback(this)
            if (!modeSubmitGradient) {
                //Exit
                Handler(Looper.getMainLooper()).postDelayed({
                    onBackPressed()
                }, 0)
            } else {
                //Back
                modeSubmitGradient = false
            }
        }

        buttonNext.setOnClickListener {
            Vibration.lowFeedback(this)
            if (!modeSubmitGradient) {
                modeSubmitGradient = true
            } else {

            }
        }
    }

    private fun recyclerGradientColours() {
        /** Builds functionality of recyclerView **/
        buttonAdapter = GradientCreatorRecycler(this, Values.gradientCreatorColours, this)
        recyclerGradientColours.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@GradientCreate, 3)
            adapter = buttonAdapter
        }
    }

    private fun setViewStartingLocations() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (buttonAdapter != null) {
                buttonBack.translationY = buttonBack.measuredHeight + Calculations.convertToDP(this, 24f)
                buttonNext.translationY = buttonNext.measuredHeight + Calculations.convertToDP(this, 24f)
                colourMenuHolder.translationY = colourMenuHolder.measuredHeight + Calculations.convertToDP(this, 24f)

                animateViewEnterLocations()
            } else {
                setViewStartingLocations()
            }
        }, 50)
    }

    private fun animateViewEnterLocations() {
        /** Sets views to VISIBLE **/
        buttonBack.visibility = View.VISIBLE
        colourMenuHolder.visibility = View.VISIBLE
        buttonNext.visibility = View.VISIBLE

        /** Animates views **/
        UIElements.viewObjectAnimator(buttonBack, "translationY", 0f, 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourMenuHolder, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(buttonNext, "translationY", 0f, 700, 100, DecelerateInterpolator(3f))

        /** Sets imageView src **/
        iconBack.setImageResource(R.drawable.icon_close)
        iconNext.setImageResource(R.drawable.icon_arrow)
    }

    override fun onButtonClick(position: Int, view: View) {
        if (!modeColourDelete) {

        } else {
            /** Delete Colour **/
            if (Values.gradientCreatorColours.size != 1) {
                Values.gradientCreatorColours.removeAt(position)
                recyclerGradientColours()
                if (Values.gradientCreatorColours.size < 6) {
                    buttonAddColour.visibility = View.VISIBLE
                }
            } else {
                modeColourDelete = false
                iconRemoveActive.visibility = View.INVISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /** Check if appSettings unloaded **/
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        /** Check if resuming from colourPicker **/
        if (Values.currentActivity == "ColourPicker") {
            Values.currentActivity = "GradientCreator"
            recyclerGradientColours()
        }
    }
}