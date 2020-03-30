package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchGradient : AppCompatActivity() {

    private lateinit var backButton: LinearLayout
    private lateinit var buttonSearch: LinearLayout
    private lateinit var searchPopup: LinearLayout
    private lateinit var touchBlocker: View

    private lateinit var searchResultsRecycler: RecyclerView

    private lateinit var searchField: EditText
    private lateinit var searchFieldHolder: LinearLayout
    private lateinit var searchEntry: TextView

    private lateinit var browseItems: ArrayList<HashMap<String, String>>
    private lateinit var searchResults: ArrayList<HashMap<String, String>>

    var searchChanged = true

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_search_gradient)
        postponeEnterTransition()

        //Initiate LinearLayouts
        backButton = findViewById(R.id.buttonBack)
        buttonSearch = findViewById(R.id.buttonSearch)
        searchPopup = findViewById(R.id.searchPopup)

        touchBlocker = findViewById(R.id.touchBlocker)

        //Initiate RecyclerView
        searchResultsRecycler = findViewById(R.id.searchResultsRecycler)
        searchResultsRecycler.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this, 2)
        searchResultsRecycler.layoutManager = gridLayoutManager

        //Initiate EditText
        searchField = findViewById(R.id.searchField)
        searchFieldHolder = findViewById(R.id.searchFieldHolder)
        searchEntry = findViewById(R.id.textEntry)

        browseItems = Values.browse
        searchResults = ArrayList()

        searchField.post {
            startPostponedEnterTransition()
            Values.currentActivity = "SearchGradient"
        }

        searchLogic()


        backButton.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            onBackPressed()
        }
    }

    private fun searchLogic() {

        buttonSearch.setOnClickListener {
            searchGradients()
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchChanged = true
            }
        })

        searchField.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchGradients()
            }
            false
        }

        searchField.setOnFocusChangeListener { _, b ->
            if (b) {
                UIElements.linearLayoutObjectAnimator(backButton, "translationY", -80f, 300, 0, DecelerateInterpolator())
                UIElements.linearLayoutObjectAnimator(searchFieldHolder, "translationY", -80f, 300, 0, DecelerateInterpolator())
                UIElements.linearLayoutObjectAnimator(buttonSearch, "translationY", -80f, 300, 0, DecelerateInterpolator())
            } else {
                UIElements.linearLayoutObjectAnimator(backButton, "translationY", 0f, 300, 0, DecelerateInterpolator())
                UIElements.linearLayoutObjectAnimator(searchFieldHolder, "translationY", 0f, 300, 0, DecelerateInterpolator())
                UIElements.linearLayoutObjectAnimator(buttonSearch, "translationY", 0f, 300, 0, DecelerateInterpolator())
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun searchGradients() {
        searchField.clearFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

        searchResults.clear()

        val searchFieldText = searchField.text.toString()
        val textForCode = searchFieldText.replace(" ", "").toLowerCase()

        var searchedGradients = 0
        var foundGradients = 0

        if (textForCode != "") {
            searchChanged = false
            for (count in 0 until browseItems.size) {
                val found = HashMap<String, String>()

                searchedGradients++

                if (browseItems[count]["backgroundName"]!!.replace(" ", "").toLowerCase()
                                .contains(textForCode)) {

                    found["backgroundName"] = browseItems[count]["backgroundName"] as String
                    found["startColour"] = browseItems[count]["startColour"] as String
                    found["endColour"] = browseItems[count]["endColour"] as String
                    found["description"] = browseItems[count]["description"] as String

                    searchResults.add(found)

                    foundGradients++
                }
            }
            setResultsGrid()

            if (foundGradients == 0) {
                searchPopup.visibility = View.VISIBLE
            } else {
                searchPopup.visibility = View.GONE
            }

        } else {
            searchPopup.visibility = View.VISIBLE
        }
    }

    private fun setResultsGrid() {
        try {
            val gridAdapter = BrowseRecyclerViewAdapter(this, searchResults)
            searchResultsRecycler.adapter = gridAdapter

            gridAdapter.setClickListener { view, position ->
                Vibration.lowFeedback(this)
                touchBlocker.visibility = View.VISIBLE

                val details = Intent(this, ActivityGradientDetails::class.java)
                val info = searchResults[position]

                val gradientName = info["backgroundName"]
                val startColour = info["startColour"]
                val endColour = info["endColour"]
                val description = info["description"]

                details.putExtra("gradientName", gradientName)
                details.putExtra("startColour", startColour)
                details.putExtra("endColour", endColour)
                details.putExtra("description", description)

                val detailsActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.gradient), gradientName as String)
                startActivity(details, detailsActivityOptions.toBundle())
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.search_gradient.set_results_grid: ${e.localizedMessage}")
        }

        try {
            searchEntry.text = searchField.text.toString().replace(" ", "\n")
        } catch (e: Exception) {
            Log.e("ERR", "No TextView")
        }
    }

    override fun onResume() {
        super.onResume()

        Values.currentActivity = "SearchGradient"
        Values.saveValues(this)
        touchBlocker.visibility = View.GONE
    }
}
