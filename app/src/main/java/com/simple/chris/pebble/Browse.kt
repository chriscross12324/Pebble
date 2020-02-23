package com.simple.chris.pebble

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.roundToInt

class Browse : AppCompatActivity() {

    private lateinit var bottomSheet: CardView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private lateinit var browseGrid: RecyclerView

    private lateinit var buttonSearch: ImageView
    private lateinit var buttonSettings: ImageView

    private var screenHeight = 0
    private var bottomSheetPeekHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_browse)
        Values.saveValues(this)

        val coordinatorLayout: CoordinatorLayout = findViewById(R.id.coordinatorLayout)
        val viewTreeObserver = coordinatorLayout.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            getScreenHeight()
            bottomSheet()

        }
        browseGrid()
        buttonSearch()
        buttonSettings()
    }

    private fun getScreenHeight() {
        try {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            screenHeight = displayMetrics.heightPixels
            bottomSheetPeekHeight = (screenHeight * 0.4).roundToInt()
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.get_screen_height: " + e.localizedMessage)
        }
    }

    private fun bottomSheet() {
        bottomSheet = findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight
    }

    private fun browseGrid() {
        try {
            browseGrid = this.findViewById(R.id.browseGrid)
            browseGrid.setHasFixedSize(true)

            val gridLayoutManager = GridLayoutManager(this, 2)
            browseGrid.layoutManager = gridLayoutManager

            val browseGridAdapter = BrowseRecyclerViewAdapter(this@Browse, Values.browse)
            browseGrid.adapter = browseGridAdapter
            browseGridAdapter.setClickListener { view, position ->
                val details = Intent(this, GradientDetails::class.java)
                val info = Values.browse[position]

                val gradientName = info["backgroundName"]
                val startColour = info["startColour"]
                val endColour = info["endColour"]
                val description = info["description"]

                details.putExtra("gradientName", gradientName)
                details.putExtra("startColour", startColour)
                details.putExtra("endColour", endColour)
                details.putExtra("description", description)

                val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.gradient), gradientName)
                startActivity(details, activityOptions.toBundle())
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.browse_grid: " + e.localizedMessage)
        }
    }

    private fun buttonSearch() {
        buttonSearch = findViewById(R.id.buttonSearch)
    }

    private fun buttonSettings() {
        buttonSettings = findViewById(R.id.buttonSettings)
        buttonSettings.setOnClickListener {
            val popupMenu = PopupMenu(this, buttonSettings)
            popupMenu.menuInflater.inflate(R.menu.browse_popup, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.settingsOption -> openSettings()
                    R.id.aboutOption -> Toast.makeText(this, "About doesn't exist", Toast.LENGTH_SHORT).show()
                    else -> Log.e("INFO", "pebble.browse.button_settings: Menu selection error")
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun openSettings() {
        val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, bottomSheet, ViewCompat.getTransitionName(bottomSheet))
        startActivity(Intent(this, Settings::class.java), activityOptions.toBundle())

    }
}
