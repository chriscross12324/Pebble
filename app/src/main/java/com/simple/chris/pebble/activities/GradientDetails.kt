package com.simple.chris.pebble.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.simple.chris.pebble.*
import com.simple.chris.pebble.functions.Calculations.convertToDP
import com.simple.chris.pebble.functions.UIElement.gradientDrawable
import com.simple.chris.pebble.functions.UIElements.constraintLayoutElevationAnimator
import com.simple.chris.pebble.functions.UIElements.viewObjectAnimator
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_gradient_details.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class GradientDetails : AppCompatActivity(), PopupDialogButtonRecycler.OnButtonListener {

    private lateinit var gradientNameString: String
    private lateinit var gradientDescriptionString: String

    private var startColourInt: Int = 0
    private var endColourInt: Int = 0
    private var detailsHolderHeight = 0

    private var detailsHolderExpanded = false
    private var copiedAnimationPlaying = false

    private var savedFileName = ""

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_gradient_details)
        Values.currentActivity = "GradientDetailsActivity"
        postponeEnterTransition()

        //Sets
        gradientNameString = intent.getStringExtra("gradientName") as String
        gradientDescriptionString = intent.getStringExtra("gradientDescription") as String
        try {
            val gradientColours = intent.getStringExtra("gradientColours")!!.replace("[", "").replace("]", "").split(",").map { it.trim() }
            val nl = ArrayList<String>(gradientColours)
            UIElement.gradientDrawableNew(this, gradientViewStatic, nl, Values.gradientCornerRadius)
        } catch (e: Exception) {
            UIElement.popupDialog(this, "error", R.drawable.icon_attention, R.string.word_error, null,R.string.serious_error, HashMaps.okArray(), window.decorView, this)
        }

        gradientViewStatic.transitionName = gradientNameString

        if (gradientDescriptionString == "") {
            gradientDescription.visibility = View.GONE
        }

        gradientViewStatic.post {
            startPostponedEnterTransition()
            Values.currentActivity = "GradientDetails"
            buttons()
            preViewPlacements()
            hideUI()
            pushHoldPopup()
            detailsHolderFixer()
        }

        gradientName.text = gradientNameString.replace("\n", " ")
        gradientDescription.text = gradientDescriptionString
    }

    private fun preViewPlacements() {
        detailsHolder.translationY = (90 * resources.displayMetrics.density) + detailsHolder.height
        actionsHolder.translationY = (74 * resources.displayMetrics.density) + detailsHolder.height
        copiedNotification.translationY = (-60 * resources.displayMetrics.density)

        UIElements.viewVisibility(detailsHolder, View.VISIBLE, 500)
        UIElements.viewVisibility(actionsHolder, View.VISIBLE, 500)
        viewObjectAnimator(actionsHolder, "translationY",
                0f, 700,
                550, DecelerateInterpolator(3f))
        viewObjectAnimator(detailsHolder, "translationY",
                0f, 700,
                500, DecelerateInterpolator(3f))
        detailsHolderHeight = detailsHolder.height
    }

    private fun buttons() {
        val clipboardManager: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        startHex.setOnLongClickListener {
            val clipData = ClipData.newPlainText("startHex", intent.getStringExtra("startColour"))
            clipboardManager.setPrimaryClip(clipData)
            copiedNotification.visibility = View.VISIBLE

            if (!copiedAnimationPlaying) {
                copiedAnimationPlaying = true
                Vibration.notification(this)

                viewObjectAnimator(copiedNotification, "translationY", Calculations.cutoutHeight(window).toFloat(), 500,
                        0, DecelerateInterpolator(3f))
                viewObjectAnimator(copiedNotification, "translationY",
                        convertToDP(this, -60f), 500,
                        2000, DecelerateInterpolator(3f))
                UIElements.viewVisibility(copiedNotification, View.INVISIBLE, 2500)

                Handler().postDelayed({
                    copiedAnimationPlaying = false
                }, 2500)
            }
            false
        }

        endHex.setOnLongClickListener {
            val clipData = ClipData.newPlainText("endHex", intent.getStringExtra("endColour"))
            clipboardManager.setPrimaryClip(clipData)
            copiedNotification.visibility = View.VISIBLE

            if (!copiedAnimationPlaying) {
                copiedAnimationPlaying = true
                Vibration.notification(this)

                viewObjectAnimator(copiedNotification, "translationY", Calculations.cutoutHeight(window).toFloat(), 500,
                        0, DecelerateInterpolator(3f))
                viewObjectAnimator(copiedNotification, "translationY",
                        convertToDP(this, -60f), 500,
                        2000, DecelerateInterpolator(3f))
                UIElements.viewVisibility(copiedNotification, View.INVISIBLE, 2500)

                Handler().postDelayed({
                    copiedAnimationPlaying = false
                }, 2500)
            }
            false
        }

        backButton.setOnClickListener {
            //imageViewObjectAnimator(gradientViewAnimated, "alpha", 0f, 0, 0, LinearInterpolator())
            Handler().postDelayed({
                onBackPressed()
            }, 0)

        }

        hideButton.setOnClickListener {
            if (!detailsHolderExpanded) {
                viewObjectAnimator(detailsHolder, "translationY", convertToDP(this, -66f), 500, 0, DecelerateInterpolator(3f))
                constraintLayoutElevationAnimator(saveGradientButton, 0f, convertToDP(this, 12f), 500, 100, DecelerateInterpolator())
                constraintLayoutElevationAnimator(setWallpaperButton, 0f, convertToDP(this, 12f), 500, 100, DecelerateInterpolator())
                saveGradientButton.visibility = View.VISIBLE
                setWallpaperButton.visibility = View.VISIBLE
                Handler().postDelayed({
                    detailsHolderExpanded = true
                }, 500)

            } else {
                viewObjectAnimator(detailsHolder, "translationY", convertToDP(this, 0f), 500, 0, DecelerateInterpolator(3f))
                constraintLayoutElevationAnimator(saveGradientButton, convertToDP(this, 12f), 0f, 500, 0, DecelerateInterpolator())
                constraintLayoutElevationAnimator(setWallpaperButton, convertToDP(this, 12f), 0f, 500, 0, DecelerateInterpolator())
                Handler().postDelayed({
                    if (detailsHolder.translationY == convertToDP(this, 0f)) {
                        saveGradientButton.visibility = View.GONE
                        setWallpaperButton.visibility = View.GONE
                    }
                    detailsHolderExpanded = false
                }, 500)
            }
        }

        saveGradientButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                UIElement.popupDialog(this, "readWritePermission", R.drawable.icon_storage, R.string.word_storage, null, R.string.sentence_needs_storage_permission, HashMaps.readWritePermissionArrayList(), window.decorView, this)
            } else {
                UIElement.popupDialog(this, "saveGradient", R.drawable.icon_save, R.string.dual_save_gradient, null, R.string.question_save_gradient,
                        HashMaps.saveGradientArrayList(), window.decorView, this)
            }
        }

        setWallpaperButton.setOnClickListener {
            UIElement.popupDialog(this, "setWallpaper", R.drawable.icon_wallpaper, R.string.dual_set_wallpaper, null, R.string.question_set_wallpaper, HashMaps.setWallpaperArrayList(), window.decorView, this)
        }
    }

    private val pushHoldListener = View.OnClickListener {
        Values.hintPushHoldDismissed = true
        UIElements.oneButtonHider(this)
    }

    private fun pushHoldPopup() {
        if (!Values.hintPushHoldDismissed) {
            Handler().postDelayed({
                UIElements.oneButtonDialog(this, R.drawable.icon_view, R.string.dual_push_hold, R.string.sentence_push_hold, R.string.word_ok, pushHoldListener)
            }, 1500)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideUI() {
        gradientViewStatic.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //Hide UI
                viewObjectAnimator(detailsHolder, "translationY", 100f, 300, 50, DecelerateInterpolator(3f))
                viewObjectAnimator(detailsHolder, "alpha", 0f, 100, 50, LinearInterpolator())
                viewObjectAnimator(actionsHolder, "translationY", 100f, 300, 0, DecelerateInterpolator(3f))
                viewObjectAnimator(actionsHolder, "alpha", 0f, 100, 0, LinearInterpolator())

                constraintLayoutElevationAnimator(saveGradientButton, convertToDP(this, 12f), 0f, 100, 0, DecelerateInterpolator())
                constraintLayoutElevationAnimator(setWallpaperButton, convertToDP(this, 12f), 0f, 100, 0, DecelerateInterpolator())
                Handler().postDelayed({
                    saveGradientButton.visibility = View.GONE
                    setWallpaperButton.visibility = View.GONE
                }, 100)
                detailsHolderExpanded = false
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                //Show UI
                viewObjectAnimator(detailsHolder, "translationY", 0f, 300, 0, DecelerateInterpolator(3f))
                viewObjectAnimator(detailsHolder, "alpha", 1f, 100, 0, LinearInterpolator())
                viewObjectAnimator(actionsHolder, "translationY", 0f, 300, 50, DecelerateInterpolator(3f))
                viewObjectAnimator(actionsHolder, "alpha", 1f, 100, 50, LinearInterpolator())
            }
            true
        }
    }

    private fun detailsHolderFixer() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            @SuppressLint("SyntheticAccessor")
            override fun run() {
                if (actionsHolder.alpha == 1f && detailsHolder.alpha == 0f) {
                    viewObjectAnimator(detailsHolder, "translationY", 0f, 300, 0, DecelerateInterpolator(3f))
                    viewObjectAnimator(detailsHolder, "alpha", 1f, 100, 0, LinearInterpolator())
                    //detailsHolderFixer()
                }

                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun createBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        return mutableBitmap
    }

    override fun onBackPressed() {
        viewObjectAnimator(detailsHolder, "translationY",
                (90 * resources.displayMetrics.density + detailsHolder.height).roundToInt().toFloat(), 250,
                50, DecelerateInterpolator()
        )
        viewObjectAnimator(actionsHolder, "translationY",
                (74 * resources.displayMetrics.density + detailsHolder.height).roundToInt().toFloat(), 250,
                0, DecelerateInterpolator()
        )
        UIElements.viewVisibility(detailsHolder, View.INVISIBLE, 250)
        UIElements.viewVisibility(actionsHolder, View.INVISIBLE, 250)
        UIElements.viewVisibility(copiedNotification, View.INVISIBLE, 250)
        Handler().postDelayed({
            saveGradientButton.visibility = View.GONE
            setWallpaperButton.visibility = View.GONE
        }, 100)

        Vibration.mediumFeedback(this)
        UIElement.dialogsToShow.clear()
        UIElement.popupDialogHider()

        Handler().postDelayed({
            super@GradientDetails.onBackPressed()
        }, 250)
    }

    override fun onResume() {
        super.onResume()

        /**
         * Checks if app settings unloaded during pause
         */
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "setWallpaper" -> {
                val wallpaperManager = WallpaperManager.getInstance(this)
                UIElement.popupDialogHider()
                when (position) {
                    0 -> {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wallpaperManager.setBitmap(createBitmap(gradientDrawable(this, null, startColourInt, endColourInt, 0f) as Drawable,
                                        Calculations.screenMeasure(this, "width", window), Calculations.screenMeasure(this, "height", window)), null, true, WallpaperManager.FLAG_SYSTEM)
                                UIElement.popupDialog(this, "wallpaperSet", R.drawable.icon_check, R.string.dual_wallpaper_set, null,
                                        R.string.sentence_enjoy_your_wallpaper, HashMaps.BAClose(), window.decorView, this)
                            } else {
                                UIElement.popupDialog(this, "outdatedAndroid", R.drawable.icon_warning, R.string.dual_outdated_android, null,
                                        R.string.question_outdated_android, HashMaps.arrayYesCancel(), window.decorView, this)
                            }
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.activity_gradient_details.buttons.setWallpaper: ${e.localizedMessage}")
                        }
                    }
                    1 -> {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wallpaperManager.setBitmap(createBitmap(UIElement.gradientDrawable(this, null, startColourInt, endColourInt, 0f) as Drawable,
                                        Calculations.screenMeasure(this, "width", window), Calculations.screenMeasure(this, "height", window)), null, true, WallpaperManager.FLAG_LOCK)
                                UIElement.popupDialog(this, "wallpaperSet", R.drawable.icon_check, R.string.dual_wallpaper_set, null,
                                        R.string.sentence_enjoy_your_wallpaper, HashMaps.BAClose(), window.decorView, this)
                            } else {
                                UIElement.popupDialog(this, "outdatedAndroid", R.drawable.icon_warning, R.string.dual_outdated_android, null,
                                        R.string.question_outdated_android, HashMaps.arrayYesCancel(), window.decorView, this)
                            }
                        } catch (e: Exception) {

                        }
                    }
                    2 -> {
                    }
                }
            }
            "readWritePermission" -> {
                UIElement.popupDialogHider()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
            "saveGradient" -> {
                UIElement.popupDialogHider()
                when (position) {
                    0 -> {
                        /**
                         * Makes directory if doesn't exist
                         */
                        val savePath = getExternalFilesDir(null)!!.absolutePath
                        val saveDir = File(savePath + File.separator + "Pebble" + File.separator)
                        saveDir.mkdirs()

                        val pebbleDir = File(Environment.getExternalStorageDirectory(), "Pebble")
                        if (!pebbleDir.exists()) {
                            if (!pebbleDir.mkdirs()) {
                                Toast.makeText(this, "Error: Failed to make directory, did you grant permission to access Storage?", Toast.LENGTH_SHORT).show()
                            }
                        }

                        try {
                            /** Makes the file to populate **/
                            savedFileName = "$gradientNameString.png".replace(" ", "_").toLowerCase()
                            val file = File(pebbleDir, savedFileName)
                            val fileOutputStream = FileOutputStream(file)

                            /** Creates the gradient Bitmap to populate the file above **/
                            createBitmap(gradientDrawable(this, null, startColourInt, endColourInt, 0f) as Drawable,
                                    Calculations.screenMeasure(this, "largest", window), Calculations.screenMeasure(this, "largest", window)).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

                            fileOutputStream.flush()
                            fileOutputStream.close()

                            Log.e("INFO", "Successfully Saved to $pebbleDir")
                            UIElement.popupDialog(this, "gradientSaved", R.drawable.icon_check, R.string.dual_gradient_saved, null,
                                    R.string.sentence_gradient_successfully_saved, HashMaps.gradientSavedArrayList(), window.decorView, this)

                        } catch (e: java.lang.Exception) {
                            Log.e("INFO", "Failed to save due to: ${e.localizedMessage}")
                        }
                    }
                }
            }
            "gradientSaved" -> {
                UIElement.popupDialogHider()
                when (position) {
                    0 -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        //intent.type = "application/pdf"
                        try {
                            val pebbleDir = File(Environment.getExternalStorageDirectory(), "Pebble")
                            intent.setDataAndType(Uri.parse("/storage/emulated/0/Pebble/$savedFileName"), "image/png")
                            startActivity(Intent.createChooser(intent, "View Gradient"))
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.gradient_details_activity.on_button_click_popup.gradient_saved: ${e.localizedMessage}")
                        }
                    }
                }
            }
            "wallpaperSet" -> {
                UIElement.popupDialogHider()
            }
            "outdatedAndroid" -> {
                when (position) {
                    0 -> {
                        val wallpaperManager = WallpaperManager.getInstance(this)
                        wallpaperManager.setBitmap(createBitmap(gradientDrawable(this, null, startColourInt, endColourInt, 0f) as Drawable,
                                Calculations.screenMeasure(this, "width", window), Calculations.screenMeasure(this, "height", window)))
                        UIElement.popupDialog(this, "wallpaperSet", R.drawable.icon_check, R.string.dual_wallpaper_set, null,
                                R.string.sentence_enjoy_your_wallpaper, HashMaps.BAClose(), window.decorView, this)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                    }
                }
            }
            "error" -> {
                UIElement.popupDialogHider()
                Handler().postDelayed({
                    onBackPressed()
                }, 450)
            }
        }
    }
}