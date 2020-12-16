package com.simple.chris.pebble.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.GradientRecyclerView
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SearchColourRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlin.math.abs

class FragSearch : Fragment(R.layout.fragment_search), GradientRecyclerView.OnGradientListener, GradientRecyclerView.OnGradientLongClickListener, SearchColourRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener {

    private var searchResults: ArrayList<HashMap<String, String>> = ArrayList()
    private lateinit var context: Activity
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var bottomSheetPeekHeight = 0
    private var fieldChange = false
    private var colourPickerExpanded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = (activity as MainActivity)

        backButton.setOnClickListener {
            //(activity as MainActivity).closeSecondary()
        }

        searchByColourButton.setOnClickListener {
            if (!colourPickerExpanded) {
                Vibration.lowFeedback(context)
                colourPickerExpanded = true
                val colourPickerButtonExpandedSize = (activity as MainActivity).getFragmentWidth() - Calculations.convertToDP(context, 180f)
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
                Vibration.lowFeedback(context)
                colourPickerExpanded = false
                UIElements.viewWidthAnimator(searchByColourButton, searchByColourButton.width.toFloat(), Calculations.convertToDP(context, 50f), 500, 100, DecelerateInterpolator(3f))
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

        bottomSheet.post {
            getHeights()
            bottomSheet()
            searchLogic()
            searchByColourInitializer()
        }
    }

    private fun getHeights() {
        try {
            titleHolder.translationY = (((Values.screenHeight * (0.333)) / 2) - (titleHolder.measuredHeight / 2)).toFloat()
            buttonIcon.translationY = (((Values.screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat()
            bottomSheetPeekHeight = (Values.screenHeight * (0.667)).toInt()

        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse_frag.get_heights: ${e.localizedMessage}")
        }

    }

    private fun bottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomSheetPeekHeight = Calculations.screenMeasure(context, "height", context.window)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((Values.screenHeight * (-0.333) * slideOffset + Values.screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2).toFloat()
                buttonIcon.translationY = ((Values.screenHeight * (-0.333) * slideOffset + Values.screenHeight * (0.333) - (titleHolder.measuredHeight)) / 8).toFloat()
                val cornerRadius = ((slideOffset * -1) + 1) * Calculations.convertToDP((activity as MainActivity), 20f)
                val bottomShe = view?.findViewById<CardView>(R.id.bottomSheet)
                bottomShe?.radius = cornerRadius
            }
        })
    }

    private fun searchByColourInitializer() {
        searchColourRecycler.setHasFixedSize(true)
        val buttonLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val buttonAdapter = SearchColourRecyclerView(context, HashMaps.searchByColourButtons(), this)

        searchColourRecycler.layoutManager = buttonLayoutManager
        searchColourRecycler.adapter = buttonAdapter
    }

    private fun searchLogic() {
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                fieldChange = true
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        /** Detect 'Enter' Key press **/
        searchField.setOnKeyListener { view, i, _ ->
            if (i == KeyEvent.KEYCODE_ENTER) {
                if (fieldChange) {
                    fieldChange = false
                    UIElement.hideSoftKeyboard(context)
                    view.clearFocus()
                    searchByName()
                }
            }
            false
        }
    }

    private fun searchByName() {
        searchResults.clear()
        var foundGradients = 0
        val stringForCode = searchField.text.toString().replace(" ", "").toLowerCase()

        /** Find Gradient by Name **/
        if (stringForCode != "") {
            try {
                for (count in 0 until Values.gradientList.size) {
                    if (Values.gradientList[count]["gradientName"]!!.replace(" ", "").toLowerCase().contains(stringForCode)) {
                        val found = HashMap<String, String>()

                        found["gradientName"] = Values.gradientList[count]["gradientName"] as String
                        found["gradientColours"] = Values.gradientList[count]["gradientColours"] as String
                        found["gradientDescription"] = Values.gradientList[count]["gradientDescription"] as String

                        searchResults.add(found)
                        foundGradients++
                    }
                }

                UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

                Handler(Looper.getMainLooper()).postDelayed({
                    UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 1f, 0, 0, LinearInterpolator())
                    UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 1f, 0, 0, LinearInterpolator())
                    UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 1f, 0, 0, LinearInterpolator())
                    RecyclerGrid.gradientGrid(context, searchResultsRecycler, searchResults, this, this)
                    resultsText.text = "$foundGradients results found"
                    searchResultsRecycler.scheduleLayoutAnimation()
                }, 400)

            } catch (e: Exception) {
                Log.e("ERR", "pebble.search_fragment.search_system: ${e.localizedMessage}")
            }

        } else {
            resultsText.text = "Enter text below"
        }
    }

    override fun onGradientClick(position: Int, view: View) {
        Vibration.lowFeedback((activity as MainActivity))
        touchBlocker.visibility = View.VISIBLE
        RecyclerGrid.gradientGridOnClickListener((activity as MainActivity), searchResults, view, position)
    }

    override fun onGradientLongClick(position: Int, view: View) {
        Vibration.lowFeedback((activity as MainActivity))
        val gradientScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f)
        val gradientScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f)
        gradientScaleX.duration = 125
        gradientScaleY.duration = 125
        gradientScaleX.interpolator = DecelerateInterpolator()
        gradientScaleY.interpolator = DecelerateInterpolator()
        gradientScaleX.start()
        gradientScaleY.start()

        Handler(Looper.getMainLooper()).postDelayed({
            gradientScaleX.reverse()
            gradientScaleY.reverse()

            RecyclerGrid.gradientGridOnLongClickListener((activity as MainActivity), searchResults, position, context.window)

            Handler(Looper.getMainLooper()).postDelayed({
                Vibration.mediumFeedback((activity as MainActivity))
            }, 150)
        }, 150)
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        //TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()

        /**
         * Checks if app settings unloaded during pause
         */
        if (!Values.valuesLoaded) {
            startActivity(Intent((activity as MainActivity), SplashScreen::class.java))
            (activity as MainActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            (activity as MainActivity).finish()
        } else {
            when (Values.currentActivity) {
                else -> {
                    Values.currentActivity = "Search"
                    touchBlocker.visibility = View.GONE
                }
            }
            Values.saveValues((activity as MainActivity))
        }
    }

    override fun onButtonClick(position: Int, view: View, buttonColour: String) {
        searchField.setText("")
        UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

        Handler(Looper.getMainLooper()).postDelayed({
            UIElements.viewObjectAnimator(searchResultsRecycler, "scaleX", 1f, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(searchResultsRecycler, "scaleY", 1f, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(searchResultsRecycler, "alpha", 1f, 0, 0, LinearInterpolator())
            searchByDomColour(buttonColour)
            searchResultsRecycler.scheduleLayoutAnimation()
        }, 400)
    }

    private fun searchByDomColour(baseColour: String) {
        //UIElement.popupDialog(context, "searching", null, R.string.word_connecting, null, R.string.sentence_pebble_is_connecting, null, context.window.decorView, null)
        searchResults.clear()
        var foundGradients = 0

        try {
            for (count in 0 until Values.gradientList.size) {
                val colourList = Values.gradientList[count]["gradientColours"]!!.replace("[", "").replace("]", "").split(",").map { it.trim() }
                val nl = ArrayList<String>(colourList)
                val bitmap = Calculations.createBitmap(UIElement.gradientDrawableNew(context, null, nl, 0f) as Drawable,
                        10, 10)
                Palette.Builder(bitmap).generate {
                    it?.let { palette ->
                        val colour = palette.getDominantColor(Color.parseColor("#ffffff"))
                        val colourHEX = "#" + Integer.toHexString(colour).substring(2)
                        if (searchByColourSystem(baseColour, colourHEX)) {
                            val found = HashMap<String, String>()

                            found["gradientName"] = Values.gradientList[count]["gradientName"] as String
                            found["gradientColours"] = Values.gradientList[count]["gradientColours"] as String
                            found["gradientDescription"] = Values.gradientList[count]["gradientDescription"] as String

                            searchResults.add(found)
                            foundGradients++
                            resultsText.text = "$foundGradients results found"
                        }
                    }
                }
            }

            /** Set View **/
            //UIElement.popupDialogHider()
            RecyclerGrid.gradientGrid(context, searchResultsRecycler, searchResults, this, this)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.activities.search_frag.search_by_dom_colour: ${e.localizedMessage}")
        }
    }

    private fun searchByColour(baseColour: String) {
        searchResults.clear()
        var foundGradients = 0

        try {
            for (count in 0 until Values.gradientList.size) {
                val colourList = Values.gradientList[count]["gradientColours"]!!.replace("[", "").replace("]", "").split(",").map { it.trim() }
                val nl = ArrayList<String>(colourList)

                for (countNL in 0 until nl.size) {
                    if (searchByColourSystem(baseColour, nl[countNL])) {
                        val found = HashMap<String, String>()

                        found["gradientName"] = Values.gradientList[count]["gradientName"] as String
                        found["gradientColours"] = Values.gradientList[count]["gradientColours"] as String
                        found["gradientDescription"] = Values.gradientList[count]["gradientDescription"] as String

                        searchResults.add(found)
                        foundGradients++
                        break
                    }
                }
            }

            /** Set View **/
            RecyclerGrid.gradientGrid(context, searchResultsRecycler, searchResults, this, this)
            resultsText.text = "$foundGradients results found"

        } catch (e: Exception) {
            Log.e("ERR", "pebble.search_activity.search_system: ${e.localizedMessage}")
        }
    }

    private fun searchByColourSystem(baseHex: String, colour: String): Boolean {
        try {
            //Remove # from hex
            val base = baseHex.replace("#", "")
            val given = colour.replace("#", "")

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
            Log.e("ERR", "pebble.activities.search.search_by_colour_system: ${e.localizedMessage}")
        }
        return false
    }

}