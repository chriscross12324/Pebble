package com.simple.chris.pebble.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.Query
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.*
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.module_browse_normal.view.*
import kotlin.math.roundToInt

class FragBrowse : Fragment(R.layout.fragment_browse), GradientRecyclerView.OnGradientListener, GradientRecyclerView.OnGradientLongClickListener,
        BrowseMenuRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener, SearchColourRecyclerView.OnButtonListener {

    private lateinit var context: Activity
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var bottomSheetPeekHeight = 0
    private var createButtonExpanded = true
    var createButtonWidth = 0
    var menuHeight = 0
    var menuWidth = 0

    /**
     * Scrollbar
     */
    internal var isDragging = false
    private var browseScrollbarHeight = 0f
    private var browseScrollbarArea = 0f
    var browseScrollbarOffset = 0
    var browseScrollbarExtent = 0
    var browseScrollbarRange = 0

    //Error Loops
    var bottomSheetErrorLoop = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = (activity as MainActivity)

        bottomSheet.post {
            initiate()
        }
    }

    private fun initiate() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.screenHeight != 0) {
                try {
                    getHeights()
                    bottomSheet()
                    browseMenuButtons()
                    searchByColourRecycler()

                    createButton.setOnClickListener {
                        (activity as MainActivity).startGradientCreator()
                    }

                    menuButton.setOnClickListener {
                        showMenu()
                    }

                    searchButton.setOnClickListener {
                        if (Values.isSearchMode) {
                            Values.isSearchMode = false
                            endSearch()
                        } else {
                            Values.isSearchMode = true
                            startSearch(true)
                        }
                        showGradients()
                    }

                    touchBlockerDark.setOnClickListener {
                        hideMenu()
                    }
                } catch (e: Exception) {
                    Values.errorOccurred = true
                }
            } else {
                initiate()
            }
        }, 10)
    }

    private fun getHeights() {
        try {
            titleHolder.translationY = (((Values.screenHeight * (0.333)) / 2) - (titleHolder.measuredHeight / 2)).toFloat()
            buttonIcon.translationY = (((Values.screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat()
            createButtonWidth = createButton.measuredWidth
            bottomSheetPeekHeight = (Values.screenHeight * (0.667)).toInt()

            menuHeight = menu.measuredHeight
            menuWidth = menu.measuredWidth
            Log.e("SCREEN", "${Values.screenHeight}")
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse_frag.get_heights: ${e.localizedMessage}")
        }
    }

    private fun bottomSheet() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (bottomSheetPeekHeight != 0) {
                try {
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
                        }
                    })

                    gradientGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            //Log.e("INFO", "$dy")
                            (bottomSheetBehavior as LockableBottomSheet).swipeEnabled = Values.recyclerBrowseAtTop && !isDragging
                            scrollbarTranslationCalculator()
                            //Toast.makeText(context, "$dy", Toast.LENGTH_SHORT).show()
                        }
                    })
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        bottomSheetErrorLoop++
                        Log.e("ERR", "BottomSheet not available::Attempt $bottomSheetErrorLoop")
                        if (bottomSheetErrorLoop >= 10) {
                            Values.errorOccurred = true
                            Log.e("ERR", "A problem occurred and the app needs to be reset")
                        } else {
                            bottomSheet()
                        }
                    }, 50)
                }
            } else {
                bottomSheet()
            }
        }, 50)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun browseScrollBar(layoutManager: LinearLayoutManager) {
        browseScrollbarHeight = bottomSheet.measuredHeight - Calculations.convertToDP((activity as MainActivity), 50f)
        browseScrollbarArea = browseScrollbarHeight - browseScrollbar.measuredHeight
        UIElements.viewWidthAnimator(browseScrollbar, browseScrollbar.measuredWidth.toFloat(), Calculations.convertToDP((activity as MainActivity), 2.5f), 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(browseScrollbar, "translationX", Calculations.convertToDP((activity as MainActivity), 8f), 500, 0, DecelerateInterpolator(3f))
        browseScrollbarOffset = 0
        gradientGrid.setOnScrollChangeListener { _, _, _, _, _ ->
            browseScrollbarExtent = gradientGrid.computeVerticalScrollExtent()
            browseScrollbarRange = gradientGrid.computeVerticalScrollRange()
            if (browseScrollbarOffset != 0) {
                if (browseScrollbarOffset < 100) {
                    Values.browseRecyclerScrollPos = 0
                } else {
                    Values.browseRecyclerScrollPos = browseScrollbarOffset
                }
            }
            scrollbarTranslationCalculator()
        }

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            try {
                Vibration.lowFeedback((activity as MainActivity))
                UIElements.viewWidthAnimator(browseScrollbar, browseScrollbar.measuredWidth.toFloat(), Calculations.convertToDP((activity as MainActivity), 2.5f), 500, 0, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(browseScrollbar, "translationX", Calculations.convertToDP((activity as MainActivity), 8f), 500, 0, DecelerateInterpolator(3f))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.frag_browse.browse_scrollbar: ${e.localizedMessage}")
            }
        }

        browseScrollbarTrigger.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    Vibration.mediumFeedback((activity as MainActivity))
                    gradientGrid.suppressLayout(true)
                    gradientGrid.suppressLayout(false)
                    handler.removeCallbacks(runnable)
                    UIElements.viewWidthAnimator(browseScrollbar, browseScrollbar.measuredWidth.toFloat(), Calculations.convertToDP((activity as MainActivity), 5f), 500, 0, DecelerateInterpolator(3f))
                    UIElements.viewObjectAnimator(browseScrollbar, "translationX", 0f, 500, 0, DecelerateInterpolator(3f))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    isDragging = false
                    Vibration.mediumFeedback((activity as MainActivity))
                    handler.postDelayed(runnable, 1000)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    scrollbarExpandBottomSheet()
                    val y = motionEvent.y
                    val yIntStart = Calculations.convertToDP((activity as MainActivity), 47.5f)
                    val yIntEnd = Calculations.screenMeasure((activity as MainActivity), "height", (activity as MainActivity).window) - yIntStart
                    val yProgress = 0f.coerceAtLeast(100f.coerceAtMost((100 / (yIntEnd - yIntStart)) * (y - yIntStart)))
                    val yOffset = (yProgress / 100) * (browseScrollbarRange - browseScrollbarExtent)
                    layoutManager.scrollToPositionWithOffset(0, -yOffset.roundToInt())
                    Values.browseRecyclerScrollPos = yOffset.roundToInt()
                    scrollbarTranslationCalculator()
                    true
                }
                else -> true
            }
        }
    }

    private fun scrollbarExpandBottomSheet() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED && browseScrollbarOffset != 0) {
            Log.d("INFO", "Expanding")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED && browseScrollbarOffset == 0) {
            Log.d("INFO", "Collapsing")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun expandBottomSheetOnReload() {
        try {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED && Values.browseRecyclerScrollPos != 0) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED && Values.browseRecyclerScrollPos == 0) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.frag_browse.expand_bottom_sheet_on_reload: ${e.localizedMessage}")
        }
    }

    internal fun scrollbarTranslationCalculator() {
        browseScrollbarOffset = gradientGrid.computeVerticalScrollOffset()
        val percent = 100f * browseScrollbarOffset / (browseScrollbarRange - browseScrollbarExtent)
        browseScrollbar.translationY = (browseScrollbarArea / 100) * percent
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
        Handler(Looper.getMainLooper()).postDelayed({
            touchBlockerDark.visibility = View.GONE
            menuArrow.visibility = View.GONE
            menu.visibility = View.GONE
        }, 400)
    }

    fun updateColumnCount(count: Int, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            gradientGrid.layoutManager = GridLayoutManager(context, count)
        }, delay)
    }

    fun gradientGrid(context: Context, view: RecyclerView, gradientJSON: ArrayList<HashMap<String, String>>, onGradientListener: GradientRecyclerView.OnGradientListener, onGradientLongClickListener: GradientRecyclerView.OnGradientLongClickListener) {
        try {
            val gridLayoutManager = GridLayoutManager(context, 2)
            val gridLayoutAdapter = GradientRecyclerView(context, gradientJSON, onGradientListener, onGradientLongClickListener)
            //gridLayoutAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            view.setHasFixedSize(true)
            view.layoutManager = gridLayoutManager
            view.adapter = gridLayoutAdapter

            var x: Int
            var y = 0
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    x = y
                    y = gridLayoutManager.findFirstCompletelyVisibleItemPosition()

                    Log.e("INFO", "$x : $y")
                    //Log.e("INFO", "${gridLayoutManager.findFirstCompletelyVisibleItemPosition()}")
                    //Values.recyclerBrowseAtTop = gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
                    //Toast.makeText(context, "$dy", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid: ${e.localizedMessage}")
        }
    }

    fun showGradients() {
        try {
            UIElements.viewObjectAnimator(gradientGrid, "alpha", 0f, 150, 200, LinearInterpolator())

            Handler(Looper.getMainLooper()).postDelayed({
                val gradientArray = if (Values.isSearchMode) Values.searchList else Values.gradientList
                if (gradientGrid != null) {
                    UIElements.viewObjectAnimator(gradientGrid, "alpha", 1f, 0, 0, LinearInterpolator())

                    val gridLayoutManager = GridLayoutManager((activity as MainActivity), 2)
                    val gridLayoutAdapter = GradientRecyclerView((activity as MainActivity), gradientArray, this, this)
                    //Log.e("INFO", "${gradientArray}")
                    //gridLayoutAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
                    gradientGrid.setHasFixedSize(true)
                    gradientGrid.layoutManager = gridLayoutManager
                    gradientGrid.adapter = gridLayoutAdapter

                    var x = 0
                    var y = 0
                    gradientGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            Values.recyclerBrowseAtTop = (gradientGrid.layoutManager as GridLayoutManager).findFirstVisibleItemPosition() == 0
                        }
                    })

                    if (Values.isSearchMode) {
                        resultsText.text = getString(R.string.variable_gradients, Values.searchList.size)
                    } else {
                        resultsText.text = getString(R.string.variable_gradients, Values.gradientList.size)
                    }
                    gradientGrid.scheduleLayoutAnimation()
                    browseScrollBar(gridLayoutManager)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Log.e("Measure", "${Values.browseRecyclerScrollPos}")
                        gridLayoutManager.scrollToPositionWithOffset(0, -Values.browseRecyclerScrollPos)
                        expandBottomSheetOnReload()
                        scrollbarTranslationCalculator()
                    }, 0)

                } else {
                    showGradients()
                    Log.d("DEBUG", "Trying Dismiss Again")
                }

            }, 500)
        } catch (e: Exception) {
            Toast.makeText(context, "Error displaying gradients", Toast.LENGTH_SHORT).show()
        }

    }

    private fun browseMenuButtons() {
        try {
            browseMenu.setHasFixedSize(true)
            val buttonLayoutManager = LinearLayoutManager((activity as MainActivity), LinearLayoutManager.VERTICAL, false)
            val buttonAdapter = BrowseMenuRecyclerView((activity as MainActivity), HashMaps.browseMenuArray(), this)

            browseMenu.layoutManager = buttonLayoutManager
            browseMenu.adapter = buttonAdapter
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).postDelayed({
                browseMenuButtons()
            }, 50)

        }
    }

    override fun onGradientClick(position: Int, view: View) {
        if (!Values.animatingSharedElement) {
            Log.d("DEBUG", "Not Animating")
            Vibration.lowFeedback((activity as MainActivity))
            Values.currentGradientScreenView = view
            Values.animatingSharedElement = true
            Values.canDismissSharedElement = false
            (activity as MainActivity).sharedElement(position, view)
        } else {
            Log.d("DEBUG", "Animating")
        }
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
            val intArray = IntArray(2)
            view.gradient.getLocationOnScreen(intArray)

            val fm = fragmentManager as FragmentManager
            val longClickGradientDialog = DialogGradientInfo.newDialog(
                    if (Values.isSearchMode) {
                        ArrayList(Values.searchList[position]["gradientColours"]!!.replace("[", "").replace("]", "")
                                .split(",").map { it.trim() })
                    } else {
                        ArrayList(Values.gradientList[position]["gradientColours"]!!.replace("[", "").replace("]", "")
                                .split(",").map { it.trim() })
                    },
                    if (Values.isSearchMode) {
                        Values.searchList[position]["gradientName"]!!
                    } else {
                        Values.gradientList[position]["gradientName"]!!
                    },
                    if (Values.isSearchMode) {
                        Values.searchList[position]["gradientDescription"]!!
                    } else {
                        Values.gradientList[position]["gradientDescription"]!!
                    },
                    intArray
            )
            longClickGradientDialog.show(fm, "longClickGradientDialog")

            Handler(Looper.getMainLooper()).postDelayed({
                Vibration.mediumFeedback((activity as MainActivity))
            }, 150)
        }, 150)
    }

    override fun onButtonClick(position: Int, view: View) {
        when (position) {
            /*0 -> {
                hideMenu()
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent((activity as MainActivity), Feedback::class.java))
                    (activity as MainActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }*/
            0 -> {
                hideMenu()
                Handler(Looper.getMainLooper()).postDelayed({
                    (activity as MainActivity).startAbout()
                }, 150)
            }
            1 -> {
                hideMenu()
                Handler(Looper.getMainLooper()).postDelayed({
                    (activity as MainActivity).startSettings()
                }, 150)
            }
            2 -> {
                hideMenu()
                //gradientGrid.smoothScrollToPosition(gradientGrid.layoutManager.)
                Values.browseRecyclerScrollPos = 0
                gradientGrid.suppressLayout(true)
                Handler(Looper.getMainLooper()).postDelayed({
                    Connection.checkConnection(context, context)
                    (activity as MainActivity).connectionChecker()
                }, 50)
            }
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        //TODO("Not yet implemented")
    }

    /**
     * Search
     *
     *          Logic
     *
     * Search
     */

    fun startSearch(animated: Boolean) {
        Values.browseRecyclerScrollPos = 0
        val hideAnimationDur: Long = if (animated) 250 else 0
        UIElements.viewObjectAnimator(buttonIcon, "translationY", -buttonIcon.measuredHeight.toFloat(), hideAnimationDur * 2, 0, DecelerateInterpolator(3f))
        UIElements.setImageViewSRC(buttonIcon, R.drawable.icon_search, 0, hideAnimationDur * 2)
        UIElements.viewObjectAnimator(buttonIcon, "translationY", (((Values.screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat(),
                hideAnimationDur * 2, hideAnimationDur * 2, DecelerateInterpolator(3f))
        UIElements.setTextViewText(screenTitle, R.string.word_search, (hideAnimationDur * 1.5).toLong(), 0)
        UIElements.viewObjectAnimator(createButton, "translationY", Calculations.convertToDP((activity as MainActivity),
                84f), hideAnimationDur, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(menuButton, "translationY", Calculations.convertToDP((activity as MainActivity),
                84f), hideAnimationDur, 50, DecelerateInterpolator(3f))

        /** SearchByColour Reveal **/
        UIElements.setImageViewSRC(iconSearch, R.drawable.icon_close, hideAnimationDur, 0)
        if (searchByColourRecycler != null) {
            searchByColourRecycler.post {
                setSBCRecyclerWidth(animated)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (searchByColourRecycler != null) {
                        UIElements.viewObjectAnimator(searchByColourHolder, "alpha", 1f, 150, -hideAnimationDur + 250, LinearInterpolator())
                        UIElements.viewObjectAnimator(searchByColourHolder, "translationX",
                                -Calculations.convertToDP((activity as MainActivity), 58f), hideAnimationDur, -hideAnimationDur + 250, DecelerateInterpolator(3f))
                    } else {
                        Log.e("ERR", "Critical Error Caught: startSearch - @view == null")
                    }
                }, 500)
            }
        } else {
            startSearch(animated)
        }
    }

    fun endSearch() {
        val hideAnimationDur: Long = 250
        UIElements.viewObjectAnimator(buttonIcon, "translationY", -buttonIcon.measuredHeight.toFloat(), hideAnimationDur * 2, 0, DecelerateInterpolator(3f))
        UIElements.setImageViewSRC(buttonIcon, R.drawable.icon_browse, 0, hideAnimationDur * 2)
        UIElements.viewObjectAnimator(buttonIcon, "translationY", (((Values.screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat(),
                hideAnimationDur * 2, hideAnimationDur * 2, DecelerateInterpolator(3f))
        UIElements.setTextViewText(screenTitle, R.string.word_browse, (hideAnimationDur * 1.5).toLong(), 0)
        UIElements.viewObjectAnimator(createButton, "translationY", 0f, hideAnimationDur * 2, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(menuButton, "translationY", 0f, hideAnimationDur * 2, 50, DecelerateInterpolator(3f))

        /** SearchByColour Reveal **/
        UIElements.viewObjectAnimator(searchByColourHolder, "alpha", 0f, 150, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(searchByColourHolder, "translationX", 0f, hideAnimationDur, 0, DecelerateInterpolator(3f))
        UIElements.viewWidthAnimator(searchByColourHolder, searchByColourHolder.measuredWidth.toFloat(), Calculations.convertToDP((activity as MainActivity), 50f), hideAnimationDur * 2, 0, DecelerateInterpolator(3f))
        UIElements.setImageViewSRC(iconSearch, R.drawable.icon_search, hideAnimationDur, 0)
    }

    private fun searchByColourRecycler() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (searchByColourRecycler != null) {
                searchByColourRecycler.setHasFixedSize(true)
                val buttonLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val buttonAdapter = SearchColourRecyclerView(context, HashMaps.searchByColourButtons(), null, this)

                searchByColourRecycler.layoutManager = buttonLayoutManager
                searchByColourRecycler.adapter = buttonAdapter
            } else {
                searchByColourRecycler()
            }
        }, 50)
    }

    fun setSBCRecyclerWidth(animated: Boolean) {
        val hideAnimationDur: Long = if (animated) 250 else 0
        searchByColourRecycler.post {
            Handler(Looper.getMainLooper()).postDelayed({
                if (searchByColourHolder != null) {
                    UIElements.viewWidthAnimator(searchByColourHolder, searchByColourHolder.measuredWidth.toFloat(), (activity as MainActivity).getFragmentWidth() - Calculations.convertToDP((activity as MainActivity), 122f), hideAnimationDur * 2, -hideAnimationDur + 250, DecelerateInterpolator(3f))
                } else {
                    Log.e("ERR", "Critical Error Caught: setSBCRecyclerWidth - @view == null")
                }
            }, 500)
        }
    }

    fun gridToTop() {
        gradientGrid.smoothScrollToPosition(0)
    }

    fun areGradientsShowing(): Boolean {
        return gradientGrid.adapter != null
    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(context, "${Values.browseRecyclerScrollPos}", Toast.LENGTH_SHORT).show()
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
                    //Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
            }
            Values.saveValues((activity as MainActivity))
        }
    }

    override fun onButtonClick(position: Int, view: View, buttonColour: String) {
        /**
         * searchByColourButtons
         */
        gradientGrid.suppressLayout(true)
        Values.searchList.clear()

        Values.downloadingGradients = true
        Values.getFireStore().collection("gradientList")
                .whereArrayContains("gradientCategories", buttonColour)
                .orderBy("gradientTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    val gradientList = java.util.ArrayList<java.util.HashMap<String, String>>()
                    for (document in it) {
                        val item = java.util.HashMap<String, String>()
                        item["gradientName"] = document.data["gradientName"] as String
                        item["gradientColours"] = document.data["gradientColours"] as String
                        item["gradientDescription"] = document.data["gradientDescription"] as String

                        gradientList.add(item)
                        Values.searchList = gradientList
                    }
                    Log.d("DEBUG", "Firestore: Done")
                    Values.downloadingGradients = true
                    Values.connectionOffline = false
                    resultsText.text = getString(R.string.variable_gradients, Values.searchList.size)
                    showGradients()
                }
                .addOnFailureListener {
                    Log.e("INFO", "Firebase failure: $it")
                }
    }

}