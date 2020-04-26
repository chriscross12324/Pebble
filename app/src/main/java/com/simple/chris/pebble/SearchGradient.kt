package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_search_gradient.*

class SearchGradient : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener {

    private var browseItems: ArrayList<HashMap<String, String>> = Values.gradientList
    private var searchResults: ArrayList<HashMap<String, String>> = ArrayList()

    var searchChanged = true

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_search_gradient)
        Values.currentActivity = "SearchGradient"
        searchLogic()

        //Initiate RecyclerView
        searchResultsRecycler.setHasFixedSize(true)
        searchResultsRecycler.layoutManager = GridLayoutManager(this, 2)

        buttonBack.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            onBackPressed()
        }
    }

    private fun searchLogic() {
        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchChanged = true
            }
        })

        searchField.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (searchChanged) {
                    searchChanged = false
                    searchGradients()
                }
            }
            false
        }
    }

    /**
     * Scans browseItems for gradients with a name containing searchField text
     */
    @SuppressLint("DefaultLocale")
    private fun searchGradients() {
        searchField.clearFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchField.windowToken, 0)

        searchResults.clear()

        val searchFieldText = searchField.text.toString()
        val textForCode = searchFieldText.replace(" ", "").toLowerCase()

        var foundGradients = 0

        if (textForCode != "") {
            searchChanged = false
            for (count in 0 until browseItems.size) {
                val found = HashMap<String, String>()

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
            RecyclerGrid.gradientGrid(this, searchResultsRecycler, searchResults, this, this)

            if (foundGradients == 0) {
                searchPopup.visibility = View.VISIBLE
            } else {
                searchPopup.visibility = View.GONE
            }
        } else {
            searchPopup.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

        Values.currentActivity = "SearchGradient"
        Values.saveValues(this)
        touchBlocker.visibility = View.GONE
    }

    override fun onGradientClick(position: Int, view: View) {
        Vibration.lowFeedback(this)
        touchBlocker.visibility = View.VISIBLE
        RecyclerGrid.gradientGridOnClickListener(this, searchResults, view, position)
    }

    override fun onGradientLongClick(position: Int, view: View) {
        Vibration.strongFeedback(this)
        RecyclerGrid.gradientGridOnLongClickListener(this, searchResults, position, blurLayout)
    }
}
