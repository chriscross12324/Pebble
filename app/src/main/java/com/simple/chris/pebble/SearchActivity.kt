package com.simple.chris.pebble

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_search.*
import kotlin.math.abs

class SearchActivity : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener, SearchColourRecyclerViewAdapter.OnButtonListener {

    //Array lists that store the gradient data
    private val allItems: ArrayList<HashMap<String, String>> = Values.gradientList
    private var searchResults: ArrayList<HashMap<String, String>> = ArrayList()

    var fieldChange = false
    var screenHeight = 0
    var bottomSheetPeekHeight = 0
    var colourPickerAnimating = false
    var colourPickerExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_search)
        Values.currentActivity = "SearchActivity"
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
        searchLogic()

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            searchColourButtons()
            /*val gradientStrokeShader = SweepGradient((searchByColourCircle.width/2).toFloat(), (searchByColourCircle.height/ 2).toFloat(), intArrayOf(Color.parseColor("#f00000"), Color.parseColor("#ffff00"), Color.parseColor("#00ff00"), Color.parseColor("#00ffff"), Color.parseColor("#0000ff"), Color.parseColor("#ff00ff"), Color.parseColor("#f00000")), null)
            val bitmap = Bitmap.createBitmap((searchByColourCircle.width/2), (searchByColourCircle.height/ 2), Bitmap.Config.ARGB_8888) as Bitmap
            val paint = Paint()
            paint.shader = gradientStrokeShader
            val canvas = Canvas(bitmap)
            canvas.drawCircle((searchByColourCircle.width/2).toFloat(), (searchByColourCircle.height/2).toFloat(), Calculations.convertToDP(this, 5f), paint)
            searchByColourCircle.setImageBitmap(bitmap)*/
        }

        //Initiate searchResultsRecycler
        searchResultsRecycler.setHasFixedSize(true)
        searchResultsRecycler.layoutManager = GridLayoutManager(this, 2)

        //Logic - backButton
        backButton.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            onBackPressed()
        }

        /*searchByColourCircle.post {
            val gradientStrokeShader = SweepGradient((searchByColourCircle.width/2).toFloat(), (searchByColourCircle.height/2).toFloat(), intArrayOf(Color.parseColor("#F44336"), Color.parseColor("#FF9800"), Color.parseColor("#FFEB3B"), Color.parseColor("#4CAF50"), Color.parseColor("#2196F3")), null)
            val rainbowCircle = Bitmap.createBitmap(searchByColourCircle.width, searchByColourCircle.height, Bitmap.Config.ARGB_8888)
            val rainbowCanvas = Canvas(rainbowCircle)
            val rainbowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            rainbowPaint.shader = gradientStrokeShader
            rainbowCanvas.drawCircle((searchByColourCircle.width/2).toFloat(), (searchByColourCircle.width/2).toFloat(), Calculations.convertToDP(this, 15f), rainbowPaint)
            searchByColourCircle.setImageBitmap(rainbowCircle)
        }*/

        searchByColourButton.setOnClickListener {
            if (!colourPickerExpanded) {
                colourPickerExpanded = true
                val colourPickerButtonExpandedSize = (Calculations.screenMeasure(this, "width") - Calculations.convertToDP(this, 180f))
                UIElements.viewWidthAnimator(searchByColourButton, searchByColourButton.width.toFloat(), colourPickerButtonExpandedSize, 500, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(searchField, "alpha", 0f, 150, 0, LinearInterpolator())
                UIElements.viewVisibility(searchField, View.GONE, 150)
                UIElements.viewObjectAnimator(iconText, "alpha", 1f, 150, 50, LinearInterpolator())
                UIElements.viewVisibility(iconText, View.VISIBLE, 150)

                UIElements.viewObjectAnimator(searchByColourCircle, "alpha", 0f, 150, 0, LinearInterpolator())
                UIElements.viewVisibility(searchByColourCircle, View.GONE, 150)
                UIElements.viewObjectAnimator(searchColourRecycler, "alpha", 1f, 150, 100, LinearInterpolator())
                UIElements.viewVisibility(searchColourRecycler, View.VISIBLE, 150)
            }
        }

        searchFieldHolder.setOnClickListener {
            if (colourPickerExpanded) {
                colourPickerExpanded = false
                UIElements.viewWidthAnimator(searchByColourButton, searchByColourButton.width.toFloat(), Calculations.convertToDP(this, 50f), 500, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(searchField, "alpha", 1f, 150, 50, LinearInterpolator())
                UIElements.viewVisibility(searchField, View.VISIBLE, 150)
                UIElements.viewObjectAnimator(iconText, "alpha", 0f, 150, 0, LinearInterpolator())
                UIElements.viewVisibility(iconText, View.GONE, 150)

                UIElements.viewObjectAnimator(searchByColourCircle, "alpha", 1f, 150, 100, LinearInterpolator())
                UIElements.viewVisibility(searchByColourCircle, View.VISIBLE, 150)
                UIElements.viewObjectAnimator(searchColourRecycler, "alpha", 0f, 150, 0, LinearInterpolator())
                UIElements.viewVisibility(searchColourRecycler, View.GONE, 150)
            }
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

        /*searchResultsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        })*/
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

        /*val brownHash = HashMap<String, String>()
        brownHash["buttonColour"] = "#5D4037"
        colourArray.add(brownHash)*/

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

                UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

                Handler().postDelayed({
                    UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 1f, 0, 0, LinearInterpolator())
                    UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 1f, 0, 0, LinearInterpolator())
                    UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 1f, 0, 0, LinearInterpolator())
                    RecyclerGrid.gradientGrid(this, searchResultsRecycler, searchResults, this, this)
                    resultsText.text = "$foundGradients results found"
                    searchResultsRecycler.scheduleLayoutAnimation()
                }, 400)

                //After search, set the view


            } catch (e: Exception) {
                Log.e("ERR", "pebble.search_activity.search_system: ${e.localizedMessage}")
            }

        } else {
            resultsText.text = "Enter text below"
        }

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
            when (Values.currentActivity) {
                else -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
            }
            Values.saveValues(this)
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
        //Toast.makeText(this, buttonColour, Toast.LENGTH_SHORT).show()
        UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

        Handler().postDelayed({
            UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 1f, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 1f, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 1f, 0, 0, LinearInterpolator())
            searchByColour(buttonColour)
            searchResultsRecycler.scheduleLayoutAnimation()
        }, 400)
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

    private fun searchByColourSystem(baseHex: String, startColour: String, endColour: String): Boolean {
        try {
            //Remove # from hex
            val base = baseHex.replace("#", "")

            val averageColour = Calculations.averageColour(startColour, endColour) as String

            //Get RGB in values of baseHex
            val baseR = Integer.valueOf(base.substring(0, 2), 16)
            val baseG = Integer.valueOf(base.substring(2, 4), 16)
            val baseB = Integer.valueOf(base.substring(4, 6), 16)

            //Get RGB in values of colourGiven
            val givenR = Integer.valueOf(averageColour.substring(0, 2), 16)
            val givenG = Integer.valueOf(averageColour.substring(2, 4), 16)
            val givenB = Integer.valueOf(averageColour.substring(4, 6), 16)

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
            Log.e("ERR", e.localizedMessage as String)
        }
        return false
    }

    private fun searchColourByHueAlgorithm(baseColour: String, startColour: String, endColour: String): Boolean {
        try {
            val baseRem = baseColour.replace("#", "")
            val averageColour = "#" + Calculations.averageColour(startColour, endColour)

            /** Gets HSV values of baseColour **/
            val baseHSV = floatArrayOf(0f, 1f, 1f)
            Color.colorToHSV(Color.parseColor(baseColour), baseHSV)

            /** Gets HSV values of averageColour **/
            val averageHSV = floatArrayOf(0f, 1f, 1f)
            Color.colorToHSV(Color.parseColor(averageColour), averageHSV)

            var hueDiff: Double = 360 - abs(baseHSV[0] - averageHSV[0]).toDouble()
            var saturationDiff: Double = 100 - abs(baseHSV[1] - averageHSV[1]).toDouble()
            var valueDiff: Double = 100 - abs(baseHSV[2] - averageHSV[2]).toDouble()

            hueDiff /= 360
            saturationDiff /= 100
            valueDiff /= 100

            Log.e("INFO", "$hueDiff $saturationDiff $valueDiff")

            if (baseColour != "#f5f5f5" || baseColour != "#1c1c1c") {
                return (((hueDiff) > 0.7) && ((saturationDiff) > 0.2) && ((valueDiff) > 0.3))
            } else if (baseColour == "#f5f5f5") {
                return (((saturationDiff) > 0.6) && ((valueDiff) > 0.3))
            } else if (baseColour == "#1c1c1c") {
                return ((valueDiff) > 0.5)
            }

        } catch (e: Exception) {
            Log.e("ERR", "pebble.search_activity.search_colour_by_hue_algorithm: ${e.localizedMessage}")
        }
        return false
    }

}