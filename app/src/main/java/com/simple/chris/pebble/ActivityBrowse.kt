package com.simple.chris.pebble

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.roundToInt

class ActivityBrowse : AppCompatActivity() {

    private lateinit var bottomSheet: CardView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private lateinit var featuredScroller: ImageView
    private lateinit var imageOptionsButton: ImageView
    private lateinit var browseGrid: RecyclerView

    private lateinit var optionsHolder: LinearLayout
    private lateinit var buttonOptions: LinearLayout
    private lateinit var buttonSearch: LinearLayout
    private lateinit var buttonSettings: LinearLayout
    private lateinit var buttonReload: LinearLayout

    private var screenHeight = 0
    private var bottomSheetPeekHeight: Int = 0
    private var actionsPopupHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_browse)
        //Values.saveValues(this)

        val coordinatorLayout: CoordinatorLayout = findViewById(R.id.coordinatorLayout)
        val viewTreeObserver = coordinatorLayout.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            getScreenHeight()
            bottomSheet()
        }
        browseGrid()
        featuredScroller()
        options()
    }

    private fun getScreenHeight() {
        try {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            screenHeight = displayMetrics.heightPixels
            bottomSheetPeekHeight = (screenHeight * 0.6).roundToInt()

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

            val browseGridAdapter = BrowseRecyclerViewAdapter(this@ActivityBrowse, Values.browse)
            browseGrid.adapter = browseGridAdapter
            browseGridAdapter.setClickListener { view, position ->
                browseGrid.isEnabled = false
                val details = Intent(this, ActivityGradientDetailsK::class.java)
                val info = Values.browse[position]

                val gradientName = info["backgroundName"]
                val startColour = info["startColour"]
                val endColour = info["endColour"]
                val description = info["description"]

                details.putExtra("gradientName", gradientName)
                details.putExtra("startColour", startColour)
                details.putExtra("endColour", endColour)
                details.putExtra("description", description)

                val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.gradient), gradientName as String)
                startActivity(details, activityOptions.toBundle())
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.browse_grid: " + e.localizedMessage)
        }
    }

    private fun featuredScroller() {
        try {
            val randomNumber = (0..Values.browse.size).random()
            featuredScroller = this.findViewById(R.id.featured)
            var hashmap: HashMap<String, String> = Values.browse[randomNumber]
            val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    intArrayOf(Color.parseColor(hashmap["startColour"]), Color.parseColor(hashmap["endColour"]))
            )
            gradientDrawable.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
            featuredScroller.background = gradientDrawable
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.featured_scroller: ${e.localizedMessage}")
        }
    }

    private fun options() {
        var optionsExpanded = false
        optionsHolder = findViewById(R.id.optionsHolder)
        buttonOptions = findViewById(R.id.buttonOptions)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonSettings = findViewById(R.id.buttonSettings)
        buttonReload = findViewById(R.id.buttonReload)
        imageOptionsButton = findViewById(R.id.imageOptionsButton)

        buttonOptions.setOnClickListener {
            if (!optionsExpanded) {
                imageOptionsButton.setBackgroundResource(R.drawable.icon_close)
                buttonSearch.visibility = View.VISIBLE
                buttonSettings.visibility = View.VISIBLE
                buttonReload.visibility = View.VISIBLE
                UIElements.linearLayoutValueAnimator(optionsHolder, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics),
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics), 500, 0, DecelerateInterpolator(3f))
                optionsHolder.elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
                optionsExpanded = true
            } else {
                imageOptionsButton.setBackgroundResource(R.drawable.icon_apps)
                UIElements.linearLayoutValueAnimator(optionsHolder, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics),
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics), 500, 0, DecelerateInterpolator(3f))
                buttonSearch.visibility = View.GONE
                buttonSettings.visibility = View.GONE
                buttonReload.visibility = View.GONE
                optionsHolder.elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics)
                optionsExpanded = false
            }
        }

        buttonReload.setOnClickListener {
            startActivity(Intent(this, ActivityConnecting::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun openSettings() {
        val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, bottomSheet, ViewCompat.getTransitionName(bottomSheet))
        startActivity(Intent(this, ActivitySettings::class.java), activityOptions.toBundle())

    }

    override fun onPause() {
        super.onPause()
        Values.saveValues(this)
    }

    override fun onResume() {
        super.onResume()
        if (browseGrid.adapter == null) {
            Values.loadValues(this)
            startActivity(Intent(this, ActivityConnecting::class.java))
        } else {
            Values.saveValues(this)
            browseGrid.isEnabled = true
        }
    }
}
