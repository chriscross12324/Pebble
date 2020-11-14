package com.simple.chris.pebble.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.BrowseMenuRecyclerView
import com.simple.chris.pebble.adapters_helpers.GradientRecyclerView
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.fragment_browse.bottomSheet
import kotlinx.android.synthetic.main.fragment_browse.browseMenu
import kotlinx.android.synthetic.main.fragment_browse.buttonIcon
import kotlinx.android.synthetic.main.fragment_browse.createButton
import kotlinx.android.synthetic.main.fragment_browse.menu
import kotlinx.android.synthetic.main.fragment_browse.menuArrow
import kotlinx.android.synthetic.main.fragment_browse.menuButton
import kotlinx.android.synthetic.main.fragment_browse.resultsText
import kotlinx.android.synthetic.main.fragment_browse.titleHolder
import kotlinx.android.synthetic.main.fragment_browse.touchBlocker
import kotlinx.android.synthetic.main.fragment_browse.touchBlockerDark
import java.lang.Exception

class BrowseFrag : Fragment(R.layout.fragment_browse), GradientRecyclerView.OnGradientListener, GradientRecyclerView.OnGradientLongClickListener, BrowseMenuRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener {

    private lateinit var context: Activity
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var bottomSheetPeekHeight = 0
    private var createButtonExpanded = true
    var createButtonWidth = 0
    var menuHeight = 0
    var menuWidth = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = (activity as MainActivity)

        bottomSheet.post {
            getHeights()
            bottomSheet()
            showGradients()
            browseMenuButtons()
        }

        createButton.setOnClickListener {
            (activity as MainActivity).startGradientCreator()
        }

        menuButton.setOnClickListener {
            showMenu()
        }

        touchBlockerDark.setOnClickListener {
            hideMenu()
        }
    }

    private fun getHeights() {
        try {
            titleHolder.translationY = (((Values.screenHeight * (0.333)) / 2) - (titleHolder.measuredHeight / 2)).toFloat()
            buttonIcon.translationY = (((Values.screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat()
            createButtonWidth = createButton.measuredWidth
            bottomSheetPeekHeight = (Values.screenHeight * (0.667)).toInt()

            menuHeight = menu.measuredHeight
            menuWidth = menu.measuredWidth
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

                if (slideOffset >= 0.4f) {
                    if (createButtonExpanded) {
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP((activity as MainActivity), 35f).toInt(), 0, 250)
                        UIElement.animateViewWidth("width", createButton, Calculations.convertToDP((activity as MainActivity), 50f).toInt(), 0, 250)
                        createButtonExpanded = false
                    }
                } else {
                    if (!createButtonExpanded) {
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP((activity as MainActivity), 60f).toInt(), 0, 250)
                        UIElement.animateViewWidth("width", createButton, Calculations.viewWrapContent(createButton, "width") + Calculations.convertToDP((activity as MainActivity), 11f).toInt(), 0, 250)
                        createButtonExpanded = true
                    }

                }

                //browseGrid.setPadding(browseGrid.paddingLeft, Calculations.convertToDP(this@Browse, ((Calculations.cutoutHeight(window) * slideOffset) + 16f)).toInt(), browseGrid.paddingRight, browseGrid.paddingBottom)
                //Toast.makeText(this@Browse, "${browseGrid.paddingTop + Calculations.convertToDP(this@Browse, (Calculations.cutoutHeight(window) * slideOffset))}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showMenu() {
        touchBlockerDark.visibility = View.VISIBLE
        menuArrow.visibility = View.INVISIBLE
        menu.visibility = View.VISIBLE
        menu.layoutParams.height = Calculations.convertToDP((activity as MainActivity), 60f).toInt()
        UIElements.viewObjectAnimator(touchBlockerDark, "alpha", 1f, 250, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(menu, "alpha", 1f, 250, 0, LinearInterpolator())
        UIElement.animateViewWidth("height", menu, Calculations.viewWrapContent(menu, "height"), 50, 500)
        UIElements.viewObjectAnimator(menuArrow, "translationY", Calculations.convertToDP((activity as MainActivity), 0f), 250, 250, DecelerateInterpolator())
        UIElements.viewVisibility(menuArrow, View.VISIBLE, 250)
        Vibration.lowFeedback((activity as MainActivity))
    }

    private fun hideMenu() {
        UIElement.animateViewWidth("height", menu, Calculations.convertToDP((activity as MainActivity), 55f).toInt(), 0, 400)
        UIElements.viewObjectAnimator(menu, "alpha", 0f, 175, 125, LinearInterpolator())
        UIElements.viewObjectAnimator(menuArrow, "translationY", Calculations.convertToDP((activity as MainActivity), -25f), 150, 0, DecelerateInterpolator())
        UIElements.viewVisibility(menuArrow, View.INVISIBLE, 150)
        UIElements.viewObjectAnimator(touchBlockerDark, "alpha", 0f, 175, 175, LinearInterpolator())
        Handler().postDelayed({
            touchBlockerDark.visibility = View.GONE
            menuArrow.visibility = View.GONE
            menu.visibility = View.GONE
        }, 400)
    }

    fun showGradients() {
        UIElements.viewObjectAnimator(gradientGrid, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(gradientGrid, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
        UIElements.viewObjectAnimator(gradientGrid, "alpha", 0f, 150, 200, LinearInterpolator())

        Handler().postDelayed({
            UIElements.viewObjectAnimator(gradientGrid, "scaleX", 1f, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientGrid, "scaleY", 1f, 0, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(gradientGrid, "alpha", 1f, 0, 0, LinearInterpolator())
            RecyclerGrid.gradientGrid((activity as MainActivity), gradientGrid, Values.gradientList, this, this)
            resultsText.text = "${Values.gradientList.size} gradientes"
            gradientGrid.scheduleLayoutAnimation()
        }, 500)
    }

    private fun browseMenuButtons() {
        browseMenu.setHasFixedSize(true)
        val buttonLayoutManager = LinearLayoutManager((activity as MainActivity), LinearLayoutManager.VERTICAL, false)
        val buttonAdapter = BrowseMenuRecyclerView((activity as MainActivity), HashMaps.browseMenuArray(), this)

        browseMenu.layoutManager = buttonLayoutManager
        browseMenu.adapter = buttonAdapter
    }

    override fun onGradientClick(position: Int, view: View) {
        Vibration.lowFeedback((activity as MainActivity))
        touchBlocker.visibility = View.VISIBLE
        RecyclerGrid.gradientGridOnClickListener((activity as MainActivity), Values.gradientList, view, position)
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

        Handler().postDelayed({
            gradientScaleX.reverse()
            gradientScaleY.reverse()

            RecyclerGrid.gradientGridOnLongClickListener((activity as MainActivity), Values.gradientList, position, (activity as MainActivity).window.decorView)

            Handler().postDelayed({
                Vibration.mediumFeedback((activity as MainActivity))
            }, 150)
        }, 150)
    }

    override fun onButtonClick(position: Int, view: View) {
        when (position) {
            0 -> {
                hideMenu()
                Handler().postDelayed({
                    startActivity(Intent((activity as MainActivity), Feedback::class.java))
                    (activity as MainActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }
            1 -> {
                hideMenu()
                Handler().postDelayed({
                    (activity as MainActivity).startDonating()
                }, 250)
            }
            2 -> {
                hideMenu()
                Handler().postDelayed({
                    (activity as MainActivity).startSettings()
                }, 250)
            }
            3 -> {
                Toast.makeText((activity as MainActivity), "Not migrated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        //TODO("Not yet implemented")
    }

    fun gridToTop() {
        gradientGrid.smoothScrollToPosition(0)
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
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
            }
            Values.saveValues((activity as MainActivity))

            /*if (Values.refreshTheme) {
                startActivity(Intent((activity as MainActivity), SplashScreen::class.java))
                (activity as MainActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                (activity as MainActivity).finish()
            }

            if (Values.justSubmitted) {
                gradientsDownloaded()
                Values.justSubmitted = false
            }*/
        }
    }

}