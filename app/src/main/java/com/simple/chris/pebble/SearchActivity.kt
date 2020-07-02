package com.simple.chris.pebble

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.backButton
import kotlinx.android.synthetic.main.activity_search.bottomSheet
import kotlinx.android.synthetic.main.activity_search.coordinatorLayout
import kotlinx.android.synthetic.main.activity_search.resultsText
import kotlinx.android.synthetic.main.activity_search.titleHolder
import kotlinx.android.synthetic.main.activity_search.touchBlocker
import kotlinx.android.synthetic.main.activity_search.wallpaperImageViewer
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.roundToInt

class SearchActivity : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener, SearchColourRecyclerViewAdapter.OnButtonListener {

    //Array lists that store the gradient data
    private val allItems: ArrayList<HashMap<String, String>> = Values.gradientList
    private var searchResults: ArrayList<HashMap<String, String>> = ArrayList()

    var fieldChange = false
    var screenHeight = 0
    var bottomSheetPeekHeight = 0
    var colourPickerAnimating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_search)
        Values.currentActivity = "SearchActivity"
        UIElements.setWallpaper(this, wallpaperImageViewer)
        searchLogic()

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            searchColourButtons()
        }

        //Initiate searchResultsRecycler
        searchResultsRecycler.setHasFixedSize(true)
        searchResultsRecycler.layoutManager = GridLayoutManager(this, 2)

        //Logic - backButton
        backButton.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            onBackPressed()
        }
    }

    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height")
            bottomSheetPeekHeight = (screenHeight * 0.667).toInt()
            titleHolder.translationY = (((screenHeight * 0.333) - titleHolder.measuredHeight) / 2).toFloat()
        } catch (e: Exception) {
            Log.e("ERR", "pebble.search_activity.get_heights: ${e.localizedMessage}")
        }
    }

    private fun bottomSheet() {
        val bottomSheetBehavior: BottomSheetBehavior<CardView> = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * -0.333 * slideOffset + screenHeight * 0.333 - (titleHolder.measuredHeight)) / 2).toFloat()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })

        searchResultsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!colourPickerAnimating) {
                    colourPickerAnimating = true
                    if (dy > 60) {
                        UIElements.viewObjectAnimator(searchColourRecycler, "translationY", 80f, 300, 0, DecelerateInterpolator(3f))
                        UIElements.viewObjectAnimator(searchColourRecycler, "alpha", 0f, 150, 0, LinearInterpolator())
                    } else if (dy < -80) {
                        UIElements.viewObjectAnimator(searchColourRecycler, "translationY", 0f, 300, 0, DecelerateInterpolator(3f))
                        UIElements.viewObjectAnimator(searchColourRecycler, "alpha", 1f, 150, 0, LinearInterpolator())
                    }
                    Handler().postDelayed({
                        colourPickerAnimating = false
                    }, 250)
                }
            }
        })
    }

    private fun searchColourButtons() {
        /**
         * Creates recycler for searchByColour
         */

        val colourArray = ArrayList<HashMap<String, String>>()

        val redHash = HashMap<String, String>()
        redHash["buttonColour"] = "#F44336"
        colourArray.add(redHash)

        val orangeHash = HashMap<String, String>()
        orangeHash["buttonColour"] = "#FF9800"
        colourArray.add(orangeHash)

        val yellowHash = HashMap<String, String>()
        yellowHash["buttonColour"] = "#FFEB3B"
        colourArray.add(yellowHash)

        val greenHash = HashMap<String, String>()
        greenHash["buttonColour"] = "#4CAF50"
        colourArray.add(greenHash)

        val blueHash = HashMap<String, String>()
        blueHash["buttonColour"] = "#2196F3"
        colourArray.add(blueHash)

        val purpleHash = HashMap<String, String>()
        purpleHash["buttonColour"] = "#9C27B0"
        colourArray.add(purpleHash)

        val brownHash = HashMap<String, String>()
        brownHash["buttonColour"] = "#5D4037"
        colourArray.add(brownHash)

        val blackHash = HashMap<String, String>()
        blackHash["buttonColour"] = "#1c1c1c"
        colourArray.add(blackHash)

        val whiteHash = HashMap<String, String>()
        whiteHash["buttonColour"] = "#f5f5f5"
        colourArray.add(whiteHash)

        searchColourRecycler.setHasFixedSize(true)
        val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val buttonAdapter = SearchColourRecyclerViewAdapter(this, colourArray, this)

        searchColourRecycler.layoutManager = buttonLayoutManager
        searchColourRecycler.adapter = buttonAdapter

        //red, orange, yellow, green, blue, purple, brown, black, white
    }

    /**
     * Prevents lag or crashing if search spam is attempted
     */
    private fun searchLogic() {

        //Improves performance by stopping search spam
        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fieldChange = true
            }
        })

        //Detects 'Enter' key press and begins search
        searchField.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (fieldChange) {
                    fieldChange = false
                    searchSystem()
                }
            }
            false
        }
    }

    /**
     * Searches for gradients with name from searchField
     */
    private fun searchSystem() {

        //Hides Keyboard
        searchField.clearFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchField.windowToken, 0)

        //Search prerequisites
        searchResults.clear()
        var foundGradients = 0
        val stringForCode = searchField.text.toString().replace(" ", "").toLowerCase()

        //Searches for requested gradient
        if (stringForCode != "") {
            try {
                for (count in 0 until allItems.size) {
                    if (allItems[count]["gradientName"]!!.replace(" ", "").toLowerCase().contains(stringForCode)) {
                        val found = HashMap<String, String>()

                        found["gradientName"] = allItems[count]["gradientName"] as String
                        found["startColour"] = allItems[count]["startColour"] as String
                        found["endColour"] = allItems[count]["endColour"] as String
                        found["description"] = allItems[count]["description"] as String

                        searchResults.add(found)
                        foundGradients++
                    }
                }

                //After search, set the view
                RecyclerGrid.gradientGrid(this, searchResultsRecycler, searchResults, this, this)
                resultsText.text = "$foundGradients results found"

            } catch (e: Exception) {
                Log.e("ERR", "pebble.search_activity.search_system: ${e.localizedMessage}")
            }

        } else {
            resultsText.text = "Enter text below"
        }

    }

    override fun onResume() {
        super.onResume()

        //Detects if gradientGrid has an adapter; known to disconnect if app is paused for too long
        if (searchResultsRecycler == null) {
            startActivity(Intent(this, SplashScreen::class.java))
        } else {
            Values.currentActivity = "Search"
            touchBlocker.visibility = View.GONE
        }
    }

    override fun onGradientClick(position: Int, view: View) {
        Vibration.lowFeedback(this)
        touchBlocker.visibility = View.VISIBLE
        RecyclerGrid.gradientGridOnClickListener(this, searchResults, view, position)
    }

    override fun onGradientLongClick(position: Int, view: View) {
        //Long Click functionality
        Vibration.lowFeedback(this)
        val gradientScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f)
        val gradientScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f)
        gradientScaleX.duration = 125
        gradientScaleY.duration = 125
        gradientScaleX.interpolator = DecelerateInterpolator()
        gradientScaleY.interpolator = DecelerateInterpolator()
        gradientScaleX.start()
        gradientScaleY.start()

        Handler().postDelayed({
            gradientScaleX.reverse()
            gradientScaleY.reverse()

            RecyclerGrid.gradientGridOnLongClickListener(this, searchResults, position, window.decorView)

            Handler().postDelayed({
                Vibration.mediumFeedback(this)
            }, 150)
        }, 150)
    }

    override fun onButtonClick(position: Int, view: View, buttonColour: String) {
        searchField.setText("")
        Toast.makeText(this, buttonColour, Toast.LENGTH_SHORT).show()
        searchByColour(buttonColour)
    }

    /**
     * searchByColour System
     */
    private fun searchByColour(baseColour: String) {
        //Search prerequisites
        searchResults.clear()
        var foundGradients = 0

        try {
            for (count in 0 until allItems.size) {
                if (searchByColourSystem(baseColour, allItems[count]["startColour"] as String, allItems[count]["endColour"] as String)) {
                    val found = HashMap<String, String>()

                    found["gradientName"] = allItems[count]["gradientName"] as String
                    found["startColour"] = allItems[count]["startColour"] as String
                    found["endColour"] = allItems[count]["endColour"] as String
                    found["description"] = allItems[count]["description"] as String

                    searchResults.add(found)
                    foundGradients++
                }
            }

            //After search, set the view
            RecyclerGrid.gradientGrid(this, searchResultsRecycler, searchResults, this, this)
            resultsText.text = "$foundGradients results found"
            //Toast.makeText(this, searchByColourSystem(baseColour, "#ffffff"), Toast.LENGTH_SHORT).show()
            /*searchByColourSystem(baseColour, "#eb6134")*/

        } catch (e: Exception) {
            Log.e("ERR", "pebble.search_activity.search_system: ${e.localizedMessage}")
        }
    }

    /*private fun searchByColourSystem(baseHex: String, colourGiven: String) : Boolean{
        try {
            //Remove # from hex
            val base = baseHex.replace("#", "")
            val given = colourGiven.replace("#", "")

            //Get RGB in values of baseHex
            val baseR = Integer.valueOf(base.substring(0, 2), 16)
            val baseG = Integer.valueOf(base.substring(2, 4), 16)
            val baseB = Integer.valueOf(base.substring(4, 6), 16)

            //Get RGB in values of colourGiven
            val givenR = Integer.valueOf(given.substring(0, 2), 16)
            val givenG = Integer.valueOf(given.substring(2, 4), 16)
            val givenB = Integer.valueOf(given.substring(4, 6), 16)

            //Calculate different between base & given
            var diffR: Double = 255 - abs(baseR - givenR).toDouble()
            var diffG: Double = 255 - abs(baseG - givenG).toDouble()
            var diffB: Double = 255 - abs(baseB - givenB).toDouble()

            //Limit RGB values between 0 & 1
            diffR /= 255
            diffG /= 255
            diffB /= 255

            Log.e("baseR", "$baseR")
            Log.e("baseG", "$baseG")
            Log.e("baseB", "$baseB")

            Log.e("givenR", "$givenR")
            Log.e("givenG", "$givenG")
            Log.e("givenB", "$givenB")

            Log.e("diffR", "$diffR")
            Log.e("diffG", "$diffG")
            Log.e("diffB", "$diffB")

            //Log.e("INFO", "${((diffR + diffG + diffB) / 3).roundToInt()}")
            return ((diffR + diffG + diffB) / 3) > 0.7
        } catch (e: Exception) {
            Log.e("ERR", e.localizedMessage)
        }
        return false
    }*/

    private fun searchByColourSystem(baseHex: String, startColour: String, endColour: String) : Boolean{
        try {
            //Remove # from hex
            val base = baseHex.replace("#", "")
            val start = startColour.replace("#", "")
            val end = endColour.replace("#", "")

            //Get average of two colours
            val startR = Integer.valueOf(start.substring(0, 2), 16)
            val startG = Integer.valueOf(start.substring(2, 4), 16)
            val startB = Integer.valueOf(start.substring(4, 6), 16)

            val endR = Integer.valueOf(end.substring(0, 2), 16)
            val endG = Integer.valueOf(end.substring(2, 4), 16)
            val endB = Integer.valueOf(end.substring(4, 6), 16)

            val avgR = (startR + (endR - startR) * 0.5).roundToInt().toString(16).padStart(2, '0')
            val avgG = (startG + (endG - startG) * 0.5).roundToInt().toString(16).padStart(2, '0')
            val avgB = (startB + (endB - startB) * 0.5).roundToInt().toString(16).padStart(2, '0')

            val given = avgR + avgG + avgB

            //Get RGB in values of baseHex
            val baseR = Integer.valueOf(base.substring(0, 2), 16)
            val baseG = Integer.valueOf(base.substring(2, 4), 16)
            val baseB = Integer.valueOf(base.substring(4, 6), 16)

            //Get RGB in values of colourGiven
            val givenR = Integer.valueOf(given.substring(0, 2), 16)
            val givenG = Integer.valueOf(given.substring(2, 4), 16)
            val givenB = Integer.valueOf(given.substring(4, 6), 16)

            //Calculate different between base & given
            var diffR: Double = 255 - abs(baseR - givenR).toDouble()
            var diffG: Double = 255 - abs(baseG - givenG).toDouble()
            var diffB: Double = 255 - abs(baseB - givenB).toDouble()

            //Limit RGB values between 0 & 1
            diffR /= 255
            diffG /= 255
            diffB /= 255

            return ((diffR + diffG + diffB) / 3) > 0.8
        } catch (e: Exception) {
            Log.e("ERR", e.localizedMessage)
        }
        return false
    }
}