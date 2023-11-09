package com.simple.chris.pebble.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.simple.chris.pebble.R
import com.simple.chris.pebble.dialogs.DialogPopup
import com.simple.chris.pebble.recyclers.GradientCreatorRecycler
import com.simple.chris.pebble.databinding.ActivityGradientCreateBinding
import com.simple.chris.pebble.functions.*
import java.util.*
import kotlin.random.Random

class ActivityGradientCreator : AppCompatActivity(), GradientCreatorRecycler.OnButtonListener {
    private lateinit var binding: ActivityGradientCreateBinding

    lateinit var buttonAdapter: GradientCreatorRecycler
    //lateinit var mInterstitialAd: com.google.android.gms.ads.InterstitialAd
    var modeColourDelete = false
    var modeSubmitGradient = false
    var generatingGradient = false
    var colourMenuHolderHeight = 0
    var recyclerGradientColoursHeight = 0
    var adMobTryAgain = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        binding = ActivityGradientCreateBinding.inflate(layoutInflater)
        val view = binding.root;
        setContentView(view)
        Values.currentActivity = "GradientCreator"
        startCreator()
        setGradientDrawable()
        //adMob()
    }

    private fun startCreator() {
        /** Wait for UI to populate **/
        Handler(Looper.getMainLooper()).postDelayed({
            if (binding.recyclerGradientColours != null) {
                /** Set UI Elements **/
                buildColourRecycler()
                recyclerGradientColours()
                setViewStartingLocations()
                buttonFunctionality()
            } else {
                startCreator()
            }
        }, 50)
    }

    private fun buttonFunctionality() {
        binding.buttonBack.setOnClickListener {
            Vibration.lowFeedback(this)
            if (!modeSubmitGradient) {
                //Exit
                firstStepExitAnim(true)
            } else {
                //Back
                modeSubmitGradient = false
                secondStepAnimOut()
            }
        }

        binding.buttonNext.setOnClickListener {
            Vibration.lowFeedback(this)
            if (!modeSubmitGradient) {
                modeSubmitGradient = true
                secondStepAnimIn(true)
            } else {
                startSubmission()
            }
        }

        binding.buttonAddColour.setOnClickListener {
            binding.buttonRemoveColour.alpha = 1f
            if (Values.gradientCreatorColours.size < 6) {
                val startRNDM = Random
                Values.gradientCreatorColours.add(Values.gradientCreatorColours.size, "#" + Integer.toHexString(Color.rgb(startRNDM.nextInt(256), startRNDM.nextInt(256), startRNDM.nextInt(256))).substring(2))
                buildColourRecycler()
                setGradientDrawable()
                if (Values.gradientCreatorColours.size == 6) {
                    it.alpha = 0.5f
                }
            } else {
                Vibration.notification(this)
                it.alpha = 0.5f
            }
        }

        binding.buttonRemoveColour.setOnClickListener {
            if (modeColourDelete) {
                modeColourDelete = false
                binding.iconRemoveActive.visibility = View.INVISIBLE
                UIElements.viewVisibility(binding.notification, View.INVISIBLE, 250)
                UIElements.viewObjectAnimator(binding.notification, "translationY", 0f, 250, 0, DecelerateInterpolator(3f))
            } else {
                if (Values.gradientCreatorColours.size != 1) {
                    modeColourDelete = true
                    binding.iconRemoveActive.visibility = View.VISIBLE
                    UIElements.viewVisibility(binding.notification, View.VISIBLE, 0)
                    UIElements.viewObjectAnimator(binding.notification, "translationY", convertFloatToDP(this, -8f) - binding.notification.measuredHeight,
                            250, 0, DecelerateInterpolator(3f))
                } else {
                    Vibration.notification(this)
                    it.alpha = 0.5f
                }
            }
        }

        binding.buttonRandomGradient.setOnClickListener {
            if (!generatingGradient) {
                generatingGradient = true
                Vibration.lowFeedback(this)
                it.alpha = 0.5f
                binding.buttonAddColour.alpha = 0.5f
                binding.buttonRemoveColour.alpha = 0.5f
                val colourCount = Values.gradientCreatorColours.size
                Values.gradientCreatorColours.clear()

                UIElements.viewObjectAnimator(binding.recyclerGradientColours, "alpha", 0f, 250, 0, LinearInterpolator())
                UIElements.viewObjectAnimator(binding.gradientTransition, "alpha", 1f, 400, 0, LinearInterpolator())
                Handler(Looper.getMainLooper()).postDelayed({
                    repeat(colourCount) {
                        val startRNDM = Random
                        Values.gradientCreatorColours.add("#" + Integer.toHexString(Color.rgb(startRNDM.nextInt(256), startRNDM.nextInt(256), startRNDM.nextInt(256))).substring(2))
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        setGradientDrawable()
                        buildColourRecycler()
                        UIElements.viewObjectAnimator(binding.recyclerGradientColours, "alpha", 1f, 250, 100, LinearInterpolator())
                        UIElements.viewObjectAnimator(binding.gradientTransition, "alpha", 0f, 400, 0, LinearInterpolator())

                        Handler(Looper.getMainLooper()).postDelayed({
                            it.alpha = 1f
                            binding.buttonAddColour.alpha = 1f
                            binding.buttonRemoveColour.alpha = 1f
                            generatingGradient = false
                        }, 500)
                    }, 450)
                }, 500)
            }
        }

        binding.gradientCreatorGradientName.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                UIElement.hideSoftKeyboard(this)
                binding.gradientCreatorGradientName.clearFocus()
            }
            false
        }
    }

    internal fun buildColourRecycler() {
        /** Builds functionality of recyclerView **/
        buttonAdapter = GradientCreatorRecycler(this, Values.gradientCreatorColours, this)
        binding.recyclerGradientColours.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@ActivityGradientCreator, 3)
            adapter = buttonAdapter
        }
    }

    private fun recyclerGradientColours() {
        /** Touch Events **/
        val itemTouchHelper = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                Vibration.strongFeedback(this@ActivityGradientCreator)
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END)
                //Log.e("INFO", "Moving")
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                if (viewHolder.adapterPosition < target.adapterPosition) {
                    Log.e("INFO", "Moved up")
                    for (i in viewHolder.adapterPosition until target.adapterPosition) {
                        Collections.swap(Values.gradientCreatorColours, i, i + 1)
                    }
                } else {
                    Log.e("INFO", "Moved down")
                    for (i in viewHolder.adapterPosition downTo target.adapterPosition + 1) {
                        Collections.swap(Values.gradientCreatorColours, i, i - 1)
                    }
                }
                buttonAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                Vibration.lowFeedback(this@ActivityGradientCreator)
                setGradientDrawable()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                buildColourRecycler()
            }
        }
        val ith = ItemTouchHelper(itemTouchHelper)
        ith.attachToRecyclerView(binding.recyclerGradientColours)
    }

    private fun setViewStartingLocations() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (buttonAdapter != null) {
                binding.buttonBack.translationY = binding.buttonBack.measuredHeight + convertFloatToDP(this, 24f)
                binding.buttonNext.translationY = binding.buttonNext.measuredHeight + convertFloatToDP(this, 24f)
                binding.colourMenuHolder.translationY = binding.colourMenuHolder.measuredHeight + convertFloatToDP(this, 24f)
                UIElements.viewObjectAnimator(binding.gradientDescriptionHolder, "translationY", convertFloatToDP(this, 90f) + binding.gradientDescriptionHolder.height, 0, 0, LinearInterpolator())
                UIElements.viewObjectAnimator(binding.gradientNameHolder, "translationY", convertFloatToDP(this, 106f) + binding.gradientDescriptionHolder.height + binding.gradientNameHolder.height, 0, 0, LinearInterpolator())

                /**
                 * Sets prerequisites for textViews
                 */
                binding.gradientCreatorGradientName.setText(Values.gradientCreatorName)
                binding.gradientCreatorGradientDescription.setText(Values.gradientCreatorDescription)

                firstStepEnterAnim()
            } else {
                setViewStartingLocations()
            }
        }, 50)
    }

    private fun firstStepExitAnim(mainMenu: Boolean) {
        /**
         * Animates all views out for firstStep
         */
        UIElements.viewObjectAnimator(binding.colourMenuHolder, "translationY",
            binding.colourMenuHolder.measuredHeight.toFloat() + convertFloatToDP(this, 24f), 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonBack, "translationY", convertFloatToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonNext, "translationY", convertFloatToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))

        Handler(Looper.getMainLooper()).postDelayed({
            if (mainMenu) {
                onBackPressed()
                overridePendingTransition(0, 0)
            }
        }, 800)
    }

    private fun firstStepEnterAnim() {
        /** Sets views to VISIBLE **/
        binding.buttonBack.visibility = View.VISIBLE
        binding.colourMenuHolder.visibility = View.VISIBLE
        binding.buttonNext.visibility = View.VISIBLE

        /** Animates views **/
        UIElements.viewObjectAnimator(binding.buttonBack, "translationY", 0f, 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourMenuHolder, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonNext, "translationY", 0f, 700, 100, DecelerateInterpolator(3f))

        /** Sets imageView src **/
        binding.iconBack.setImageResource(R.drawable.icon_close)
        binding.iconNext.setImageResource(R.drawable.icon_arrow_right)
    }

    private fun secondStepAnimIn(animate: Boolean) {
        val duration = if (animate) 500 else 0

        UIElements.viewObjectAnimator(binding.colourMenuHolder, "translationY",
            binding.colourMenuHolder.measuredHeight + convertFloatToDP(this, 24f), 500, 0, DecelerateInterpolator(3f))
        UIElements.setImageViewSRC(binding.iconBack, R.drawable.icon_arrow_left, duration.toLong() / 2, 0)
        UIElements.setImageViewSRC(binding.iconNext, R.drawable.icon_upload, duration.toLong() / 2, 0)
        UIElements.viewObjectAnimator(binding.gradientDescriptionHolder, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.gradientNameHolder, "translationY", 0f, 700, 400, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonBack, "translationX", -(binding.buttonBack.measuredWidth + convertFloatToDP(this, 8f)), 250, 250, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonNext, "translationX", binding.buttonNext.measuredWidth + convertFloatToDP(this, 8f), 250, 250, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonBack, "translationX", 0f, 250, 750, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonNext, "translationX", 0f, 250, 750, DecelerateInterpolator(3f))
        UIElements.viewVisibility(binding.gradientDescriptionHolder, View.VISIBLE, 0)
        UIElements.viewVisibility(binding.gradientNameHolder, View.VISIBLE, 0)

        binding.gradientCreatorGradientDescription.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.gradientCreatorGradientDescription.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
    }

    private fun secondStepAnimOut() {
        UIElements.viewObjectAnimator(binding.colourMenuHolder, "translationY",
                0f, 750, 750, DecelerateInterpolator(3f))
        UIElements.setImageViewSRC(binding.iconBack, R.drawable.icon_close, 500, 0)
        UIElements.setImageViewSRC(binding.iconNext, R.drawable.icon_arrow_right, 500, 0)
        UIElements.viewObjectAnimator(binding.gradientDescriptionHolder, "translationY", convertFloatToDP(this, 90f) + binding.gradientDescriptionHolder.height, 700, 200, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.gradientNameHolder, "translationY", convertFloatToDP(this, 106f) + binding.gradientDescriptionHolder.height + binding.gradientNameHolder.height, 700, 250, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonBack, "translationX", -(binding.buttonBack.measuredWidth + convertFloatToDP(this, 8f)), 250, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonNext, "translationX", binding.buttonNext.measuredWidth + convertFloatToDP(this, 8f), 250, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonBack, "translationX", 0f, 250, 450, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.buttonNext, "translationX", 0f, 250, 450, DecelerateInterpolator(3f))
        UIElements.viewVisibility(binding.gradientDescriptionHolder, View.INVISIBLE, 950)
        UIElements.viewVisibility(binding.gradientNameHolder, View.INVISIBLE, 950)
    }

    internal fun setGradientDrawable() {
        /** Draw GradientDrawable **/
        UIElement.gradientDrawableNew(this, binding.gradientViewer, Values.gradientCreatorColours, 0f)
    }

    override fun onButtonClick(position: Int, view: View) {
        if (!modeColourDelete) {
            firstStepExitAnim(false)
            Handler(Looper.getMainLooper()).postDelayed({
                Values.editingColourAtPos = position
                startActivity(Intent(this, ActivityColourPicker::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 600)
        } else {
            /** Delete Colour **/
            if (Values.gradientCreatorColours.size != 1) {
                Values.gradientCreatorColours.removeAt(position)
                buildColourRecycler()
                if (Values.gradientCreatorColours.size < 6) {
                    binding.buttonAddColour.alpha = 1f
                }
                if (Values.gradientCreatorColours.size == 1) {
                    modeColourDelete = false
                    binding.iconRemoveActive.visibility = View.INVISIBLE
                    UIElements.viewVisibility(binding.notification, View.INVISIBLE, 250)
                    UIElements.viewObjectAnimator(binding.notification, "translationY", 0f,
                            250, 0, DecelerateInterpolator(3f))
                    binding.buttonRemoveColour.alpha = 0.5f
                }
            } else {
                modeColourDelete = false
                binding.iconRemoveActive.visibility = View.INVISIBLE
                UIElements.viewVisibility(binding.notification, View.INVISIBLE, 250)
                UIElements.viewObjectAnimator(binding.notification, "translationY", 0f,
                        250, 0, DecelerateInterpolator(3f))
                binding.buttonRemoveColour.alpha = 0.5f
            }
            setGradientDrawable()
        }
    }

    private fun startSubmission() {
        /** Check if Gradient has a name **/
        if (Connection.isOnline(this)) {
            if (binding.gradientCreatorGradientName.text.toString().trim().replace(" ", "") != "") {
                submitGradient()
                /**Not working; Google Ads**/

                /*if (Values.gradientCreatorColours.size > 2) {
                    Values.dialogPopup = DialogPopup.newDialog(HashMaps.watchAdArrayList(), "submitPremiumGradient", R.drawable.icon_money, R.string.dual_premium_gradient,
                            null, R.string.question_premium_gradient, null)
                    Values.dialogPopup.show(supportFragmentManager, "submitPremiumGradient")
                } else {
                    submitGradient()
                }*/

            } else {
                Values.dialogPopup = DialogPopup.newDialog(
                    arrayBack(), "submitMissingInfo", R.drawable.icon_warning, R.string.dual_missing_info,
                        null, R.string.sentence_missing_gradient_name, null)
                Values.dialogPopup.show(supportFragmentManager, "submitMissingInfo")
            }
        } else {
            Values.dialogPopup = DialogPopup.newDialog(
                arrayNoConnection(), "submitNoConnection", R.drawable.icon_warning, R.string.dual_no_connection,
                    null, R.string.sentence_needs_internet_connection, null)
            Values.dialogPopup.show(supportFragmentManager, "submitNoConnection")
        }
    }

    internal fun submitGradient() {
        Values.dialogPopup = DialogPopup.newDialog(null, "submittingGradient", null,
                R.string.word_submitting, null, R.string.sentence_uploading_gradient, null)
        Values.dialogPopup.show(supportFragmentManager, "submittingGradient")
        val gradient = hashMapOf(
                "gradientName" to binding.gradientCreatorGradientName.text.toString(),
                "gradientDescription" to binding.gradientCreatorGradientDescription.text.toString(),
                "gradientColours" to Values.gradientCreatorColours.toString().replace(" ", ""),
                "gradientCategories" to arrayListOf("empty"),
                "appVersion" to "V3",
                "gradientTimestamp" to FieldValue.serverTimestamp(),
                "gradientBeingViewed" to false
        )

        val db = Firebase.firestore
        db.collection("gradientList").document()
                .set(gradient)
                .addOnCompleteListener {
                    Values.dialogPopup.dismiss()
                    Values.dialogPopup = DialogPopup.newDialog(
                        arrayClose(), "submitGradientSubmitted", R.drawable.icon_check, R.string.word_submit,
                            null, R.string.sentence_gradient_submitted, null)
                    Values.dialogPopup.show(supportFragmentManager, "submitGradientSubmitted")
                    Values.justSubmitted = true
                }
                .addOnFailureListener {
                    Values.dialogPopup.dismiss()
                    Values.dialogPopup = DialogPopup.newDialog(
                        arrayBack(), "submitGradientFailed", R.drawable.icon_warning, R.string.word_failed,
                            null, null, this.getString(R.string.sentence_server_error, it.localizedMessage))
                    Values.dialogPopup.show(supportFragmentManager, "submitGradientFailed")
                }
    }

    /**
     * Make sure all gradient names include 'submit' or the dialog system WILL crash
     */
    fun popupDialogHandler(dialogName: String, position: Int) {
        val fm = supportFragmentManager
        when (dialogName) {
            "submitMissingInfo" -> {
                Values.dialogPopup.dismiss()
            }
            /*"submitPremiumGradient" -> {
                when (position) {
                    0 -> {
                        Values.dialogPopup.dismiss()
                        Values.dialogPopup = DialogPopup.newDialog(null, "submitLoadingAd", null,
                                R.string.dual_ad_loading, null, R.string.sentence_ad_loading, null)
                        Values.dialogPopup.show(fm, "submitLoadingAd")
                        //mInterstitialAd.loadAd(AdRequest.Builder().build())
                    }
                    1 -> {
                        Values.dialogPopup.dismiss()
                    }
                }
            }*/
            "submitGradientSubmitted" -> {
                binding.colourMenuHolder.visibility = View.INVISIBLE
                secondStepAnimOut()
                Handler(Looper.getMainLooper()).postDelayed({
                    firstStepExitAnim(true)
                }, 500)
            }
            "submitNoConnection" -> {
                when (position) {
                    0 -> {
                        startSubmission()
                    }
                    1 -> {
                    }
                }
            }
        }
        Values.saveValues(this)
    }

    /*private fun adMob() {
        MobileAds.initialize(this) { Values.adMobInitialized = true }
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3224190453693148/3343829588"

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Values.adLoading = false
                mInterstitialAd.show()
                Values.dialogPopup.dismiss()
                submitGradient()
            }

            override fun onAdFailedToLoad(p0: LoadAdError?) {
                Values.dialogPopup.dismiss()
                submitGradient()
                Log.e("ERR", "pebble.gradient_create.admob: $p0")
            }

            override fun onAdClosed() {
            }
        }

    }*/

    override fun onResume() {
        super.onResume()
        /** Check if appSettings unloaded **/
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, ActivityStarting::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        /** Check if resuming from colourPicker **/
        if (Values.currentActivity == "ColourPicker") {
            Values.currentActivity = "GradientCreator"
            buildColourRecycler()
            recyclerGradientColours()
            firstStepEnterAnim()
            setGradientDrawable()
        }
    }
}