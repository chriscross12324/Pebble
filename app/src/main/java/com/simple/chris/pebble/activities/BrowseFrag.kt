package com.simple.chris.pebble.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.BrowseMenuRecyclerView
import com.simple.chris.pebble.adapters_helpers.GradientRecyclerView
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_browse.*
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.fragment_browse.bottomSheet
import kotlinx.android.synthetic.main.fragment_browse.buttonIcon
import kotlinx.android.synthetic.main.fragment_browse.createButton
import kotlinx.android.synthetic.main.fragment_browse.menu
import kotlinx.android.synthetic.main.fragment_browse.resultsText
import kotlinx.android.synthetic.main.fragment_browse.titleHolder
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
            Toast.makeText(context, "Got here", Toast.LENGTH_SHORT).show()
            getHeights()
            bottomSheet()
            showGradients()
        }

        createButton.setOnClickListener {
            (activity as MainActivity).startSettingFrag()
            Log.e("INFO", "Create Clicked")
        }
    }

    private fun getHeights() {
        try {
            Toast.makeText(context, "${Values.screenHeight}", Toast.LENGTH_SHORT).show()
            titleHolder.translationY = (((Values.screenHeight * (0.333)) / 2) - (titleHolder.measuredHeight / 2)).toFloat()
            buttonIcon.translationY = (((Values.screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat()
            //Toast.makeText(activity, "${Calculations.screenMeasure((activity as MainActivity), "height", (activity as MainActivity).window)}", Toast.LENGTH_SHORT).show()
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

    private fun showGradients() {
        RecyclerGrid.gradientGrid((activity as MainActivity), gradientGrid, Values.gradientList, this, this)
        resultsText.text = "${Values.gradientList.size} gradients"
    }

    override fun onGradientClick(position: Int, view: View) {
        //TODO("Not yet implemented")
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
        //TODO("Not yet implemented")
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        //TODO("Not yet implemented")
    }

    fun gridToTop() {
        gradientGrid.smoothScrollToPosition(0)
    }

}