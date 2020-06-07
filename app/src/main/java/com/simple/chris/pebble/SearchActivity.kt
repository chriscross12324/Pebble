package com.simple.chris.pebble

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_search.*
import java.lang.Exception

class SearchActivity : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener {

    //Array lists that store the gradient data
    private val allItems: ArrayList<HashMap<String, String>> = Values.gradientList
    private var searchResults: ArrayList<HashMap<String, String>> = ArrayList()

    var fieldChange = false
    var screenHeight = 0
    var bottomSheetPeekHeight = 0

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
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * -0.333 * slideOffset + screenHeight * 0.333 - (titleHolder.measuredHeight)) / 2).toFloat()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
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
                    if (allItems[count]["backgroundName"]!!.replace(" ", "").toLowerCase().contains(stringForCode)) {
                        val found = HashMap<String, String>()

                        found["backgroundName"] = allItems[count]["backgroundName"] as String
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
            startActivity(Intent(this, ActivitySplash::class.java))
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
    }

}