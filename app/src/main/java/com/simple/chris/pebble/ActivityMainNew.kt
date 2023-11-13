package com.simple.chris.pebble

import android.os.Bundle
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.simple.chris.pebble.databinding.ActivityMainNewBinding
import com.simple.chris.pebble.functions.Property
import com.simple.chris.pebble.functions.animateView
import com.simple.chris.pebble.functions.canSplitScreen
import com.simple.chris.pebble.functions.convertFloatToDP
import com.simple.chris.pebble.functions.convertInchToPx
import com.simple.chris.pebble.functions.getScreenMetrics
import com.simple.chris.pebble.functions.getViewMetrics
import com.simple.chris.pebble.functions.setAppTheme
import com.simple.chris.pebble.ui.fragmentbrowsenew.FragmentBrowseNew
import com.simple.chris.pebble.ui.fragmentbrowsenew.FragmentGradientDetailsNew

class ActivityMainNew : AppCompatActivity(), FragmentBrowseNew.BrowseFragmentListener,
    FragmentGradientDetailsNew.DetailsFragmentListener {
    private lateinit var binding: ActivityMainNewBinding
    private val viewModel by viewModels<ActivityMainNewViewModel>()

    private lateinit var browseFragment: FragmentBrowseNew
    private lateinit var detailsFragment: FragmentGradientDetailsNew

    private var initialX: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme(this)

        //Bind View
        binding = ActivityMainNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Define Fragments
        browseFragment = FragmentBrowseNew()
        detailsFragment = FragmentGradientDetailsNew()



        if (savedInstanceState == null) {
            viewModel.splitscreenDeltaX = ((getScreenMetrics(this, this.window).width / 2) - convertFloatToDP(this, 5f))
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolderBrowse, browseFragment)
                .replace(R.id.fragmentHolderGradientDetails, detailsFragment)
                .commitNow()
        }

        determineView()
        binding.fragmentSeparator.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = event.rawX
                    animateView(binding.fragmentSeparatorIndicator, Property.ALPHA, 1f)
                    animateView(binding.fragmentSeparatorIndicator, Property.HEIGHT, convertFloatToDP(this, 25f))
                    animateView(binding.fragmentSeparatorIndicator, Property.WIDTH, convertFloatToDP(this, 4f))
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    viewModel.splitscreenDeltaX = event.rawX - initialX
                    adjustFragmentWidths(viewModel.splitscreenDeltaX/2)
                    initialX = event.rawX
                    true
                }

                MotionEvent.ACTION_UP -> {
                    view.performClick()
                    animateView(binding.fragmentSeparatorIndicator, Property.ALPHA, 0.5f)
                    animateView(binding.fragmentSeparatorIndicator, Property.HEIGHT, convertFloatToDP(this, 50f))
                    animateView(binding.fragmentSeparatorIndicator, Property.WIDTH, convertFloatToDP(this, 2f))
                    true
                }

                else -> false
            }
        }
    }

    private fun determineView() {
        if (viewModel.isDetailsScreenShowing) {
            openGradientDetailsFragment()
        } else {
            closeGradientDetailsFragment()
        }
    }

    private fun openGradientDetailsFragment() {
        viewModel.isDetailsScreenShowing = true
        val canSplitScreen = canSplitScreen(this@ActivityMainNew, this@ActivityMainNew.window)
        updateLayoutWeights(
            if (canSplitScreen) 1f else 0f,
            if (canSplitScreen) convertFloatToDP(this@ActivityMainNew, 10f).toInt() else 0,
            1f
        )
    }

    private fun closeGradientDetailsFragment() {
        viewModel.isDetailsScreenShowing = false
        updateLayoutWeights(1f, 0, 0f)
    }

    private fun updateLayoutWeights(
        browseWeight: Float,
        separatorWidth: Int,
        detailsWeight: Float
    ) {
        val browseFragmentParams =
            binding.fragmentHolderBrowse.layoutParams as LinearLayout.LayoutParams
        browseFragmentParams.weight = browseWeight
        binding.fragmentHolderBrowse.layoutParams = browseFragmentParams

        val separatorParams = binding.fragmentSeparator.layoutParams
        separatorParams.width = separatorWidth
        binding.fragmentSeparator.layoutParams = separatorParams

        val gradientDetailsFragmentParams =
            binding.fragmentHolderGradientDetails.layoutParams as LinearLayout.LayoutParams
        gradientDetailsFragmentParams.weight = detailsWeight
        binding.fragmentHolderGradientDetails.layoutParams = gradientDetailsFragmentParams
    }

    private fun adjustFragmentWidths(deltaX: Float) {
        val browseFragmentParams =
            binding.fragmentHolderBrowse.layoutParams as LinearLayout.LayoutParams
        val gradientDetailsFragmentParams =
            binding.fragmentHolderGradientDetails.layoutParams as LinearLayout.LayoutParams

        if (browseFragmentParams.weight * getViewMetrics(binding.root).width + deltaX >= convertInchToPx(
                1.5f, resources
            ) && gradientDetailsFragmentParams.weight * getViewMetrics(binding.root).width - deltaX >= convertInchToPx(
                1.5f, resources
            )
        ) {
            browseFragmentParams.weight += deltaX / getViewMetrics(binding.root).width
            gradientDetailsFragmentParams.weight -= deltaX / getViewMetrics(binding.root).width

            binding.fragmentHolderBrowse.layoutParams = browseFragmentParams
            binding.fragmentHolderGradientDetails.layoutParams = gradientDetailsFragmentParams
        }
    }

    override fun onOpenDetailsButtonClick() {
        openGradientDetailsFragment()
    }

    override fun onCloseDetailsButtonClick() {
        closeGradientDetailsFragment()
    }
}