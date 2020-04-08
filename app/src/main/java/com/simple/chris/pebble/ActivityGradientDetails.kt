package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.simple.chris.pebble.UIElements.constraintLayoutObjectAnimator
import kotlinx.android.synthetic.main.activity_gradient_details.*
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.constraintLayoutElevationAnimator
import com.simple.chris.pebble.UIElements.linearLayoutObjectAnimator
import java.util.jar.Manifest
import kotlin.math.roundToInt

class ActivityGradientDetails : AppCompatActivity() {

    private lateinit var gradientNameString: String
    private lateinit var gradientDescriptionString: String

    private var startColourInt: Int = 0
    private var middleColourInt: Int = 0
    private var endColourInt: Int = 0
    private var detailsHolderHeight = 0

    private var detailsHolderExpanded = false
    private var copiedAnimationPlaying = false

    private lateinit var gradientDrawable: GradientDrawable
    private lateinit var saveGradientDrawable: GradientDrawable

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_gradient_details)
        postponeEnterTransition()

        //Sets
        gradientNameString = intent.getStringExtra("gradientName") as String
        gradientDescriptionString = intent.getStringExtra("description") as String
        startColourInt = Color.parseColor(intent.getStringExtra("startColour"))
        endColourInt = Color.parseColor(intent.getStringExtra("endColour"))

        gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColourInt, endColourInt)
        )
        gradientDrawable.cornerRadius = convertToDP(this, 20f)
        gradientViewStatic.background = gradientDrawable
        createGradients()

        gradientViewStatic.transitionName = gradientNameString
        if (Calculations.isAndroidPOrGreater()) {
            gradientViewStatic.outlineSpotShadowColor = endColourInt
        }

        if (gradientDescriptionString == "") {
            gradientDescription.visibility = View.GONE
        }

        gradientViewStatic.post {
            startPostponedEnterTransition()
            Values.currentActivity = "GradientDetails"
            buttons()
            preViewPlacements()
            getCenterPixel()
            animateGradient()
            pushHoldPopup()
            detailsHolderFixer()
        }

        gradientName.text = gradientNameString.replace("\n", " ")
        gradientDescription.text = gradientDescriptionString
        gradientStartHex.text = intent.getStringExtra("startColour")
        gradientEndHex.text = intent.getStringExtra("endColour")

        val gradientDrawableStartCircle = GradientDrawable()
        gradientDrawableStartCircle.shape = GradientDrawable.OVAL
        gradientDrawableStartCircle.setStroke(8, startColourInt)
        val gradientDrawableEndCircle = GradientDrawable()
        gradientDrawableEndCircle.shape = GradientDrawable.OVAL
        gradientDrawableEndCircle.setStroke(8, endColourInt)
        startHexCircle.background = gradientDrawableStartCircle
        endHexCircle.background = gradientDrawableEndCircle
    }

    private fun preViewPlacements() {
        detailsHolder.translationY = (90 * resources.displayMetrics.density) + detailsHolder.height
        actionsHolder.translationY = (74 * resources.displayMetrics.density) + detailsHolder.height
        copiedNotification.translationY = (-60 * resources.displayMetrics.density)

        UIElements.constraintLayoutVisibility(detailsHolder, View.VISIBLE, 500)
        UIElements.constraintLayoutVisibility(actionsHolder, View.VISIBLE, 500)
        constraintLayoutObjectAnimator(actionsHolder, "translationY",
                0f, 700,
                550, DecelerateInterpolator(3f))
        constraintLayoutObjectAnimator(detailsHolder, "translationY",
                0f, 700,
                500, DecelerateInterpolator(3f))
        detailsHolderHeight = detailsHolder.height
    }

    private fun getCenterPixel() {
        val halfWidth = (resources.displayMetrics.widthPixels) / 2
        val halfHeight = (resources.displayMetrics.heightPixels) / 2

        val mutableBitmap = Bitmap.createBitmap(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColourInt, endColourInt)
        )
        gradientDrawable.setBounds(0, 0, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        gradientDrawable.draw(canvas)
        val pixel = mutableBitmap.getPixel(halfWidth, halfHeight)

        val redValue = Color.red(pixel)
        val greenValue = Color.green(pixel)
        val blueValue = Color.blue(pixel)

        middleColourInt = Color.parseColor(String.format("#%02x%02x%02x", redValue, greenValue, blueValue))
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

                constraintLayoutObjectAnimator(copiedNotification, "translationY", 0f, 500,
                        0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(copiedNotification, "translationY",
                        convertToDP(this, -60f), 500,
                        2000, DecelerateInterpolator(3f))
                UIElements.constraintLayoutVisibility(copiedNotification, View.INVISIBLE, 2500)

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

                constraintLayoutObjectAnimator(copiedNotification, "translationY", 0f, 500,
                        0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(copiedNotification, "translationY",
                        convertToDP(this, -60f), 500,
                        2000, DecelerateInterpolator(3f))
                UIElements.constraintLayoutVisibility(copiedNotification, View.INVISIBLE, 2500)

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
                constraintLayoutObjectAnimator(detailsHolder, "translationY", convertToDP(this, -66f), 500, 0, DecelerateInterpolator(3f))
                constraintLayoutElevationAnimator(saveGradientButton, 0f, convertToDP(this, 12f), 500, 100, DecelerateInterpolator())
                constraintLayoutElevationAnimator(setWallpaperButton, 0f, convertToDP(this, 12f), 500, 100, DecelerateInterpolator())
                saveGradientButton.visibility = View.VISIBLE
                setWallpaperButton.visibility = View.VISIBLE
                Handler().postDelayed({
                    detailsHolderExpanded = true
                }, 500)

            } else {
                constraintLayoutObjectAnimator(detailsHolder, "translationY", convertToDP(this, 0f), 500, 0, DecelerateInterpolator(3f))
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
            //createBitmap(saveGradientDrawable, Calculations.screenMeasure(this, "width"), Calculations.screenMeasure(this, "height"))
            if (!Values.storagePermissionGiven(this)) {
                showStoragePermissionDialog()
                Log.e("INFO", Values.storagePermissionGiven(this).toString())
            }

        }

        setWallpaperButton.setOnClickListener {
            showSetWallpaperDialog()
        }
    }

    private fun pushHoldPopup() {
        if (!Values.detailsPushHoldPopupClosed) {
            val pushHoldDialog = Dialog(this, R.style.dialogStyle)
            pushHoldDialog.setCancelable(false)
            pushHoldDialog.setContentView(R.layout.dialog_push_hold)

            val dismissButton: LinearLayout = pushHoldDialog.findViewById(R.id.dismissPopup)
            dismissButton.setOnClickListener {
                Values.detailsPushHoldPopupClosed = true
                pushHoldDialog.dismiss()
            }

            val window = pushHoldDialog.window
            window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setDimAmount(0.5f)

            Handler().postDelayed({
                pushHoldDialog.show()
            }, 1500)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun animateGradient() {
        gradientViewStatic.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //Hide UI
                constraintLayoutObjectAnimator(detailsHolder, "translationY", 100f, 300, 50, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(detailsHolder, "alpha", 0f, 100, 50, LinearInterpolator())
                constraintLayoutObjectAnimator(actionsHolder, "translationY", 100f, 300, 0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(actionsHolder, "alpha", 0f, 100, 0, LinearInterpolator())

                constraintLayoutElevationAnimator(saveGradientButton, convertToDP(this, 12f), 0f, 100, 0, DecelerateInterpolator())
                constraintLayoutElevationAnimator(setWallpaperButton, convertToDP(this, 12f), 0f, 100, 0, DecelerateInterpolator())
                Handler().postDelayed({
                    saveGradientButton.visibility = View.GONE
                    setWallpaperButton.visibility = View.GONE
                }, 100)
                detailsHolderExpanded = false
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                //Show UI
                constraintLayoutObjectAnimator(detailsHolder, "translationY", 0f, 300, 0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(detailsHolder, "alpha", 1f, 100, 0, LinearInterpolator())
                constraintLayoutObjectAnimator(actionsHolder, "translationY", 0f, 300, 50, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(actionsHolder, "alpha", 1f, 100, 50, LinearInterpolator())
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
                    constraintLayoutObjectAnimator(detailsHolder, "translationY", 0f, 300, 0, DecelerateInterpolator(3f))
                    constraintLayoutObjectAnimator(detailsHolder, "alpha", 1f, 100, 0, LinearInterpolator())
                    detailsHolderFixer()
                }

                handler.postDelayed(this, 2000)
            }
        }, 2000)
    }

    private fun createBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        return mutableBitmap
    }

    private fun showSetWallpaperDialog() {
        val dialog = Dialog(this, R.style.dialogStyle)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_wallpaper)

        val buttonYes: LinearLayout = dialog.findViewById(R.id.yes)
        buttonYes.setOnClickListener {
            val wallpaperManager = WallpaperManager.getInstance(this)

            try {
                wallpaperManager.setBitmap(createBitmap(saveGradientDrawable, Calculations.screenMeasure(this, "width"), Calculations.screenMeasure(this, "height")))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.activity_gradient_details.buttons.setWallpaper: ${e.localizedMessage}")
                showWallpaperFailedDialog()
            }
            dialog.dismiss()
        }
        val buttonNo: LinearLayout = dialog.findViewById(R.id.no)
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }

        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setDimAmount(0.5f)

        dialog.show()
    }

    private fun showWallpaperFailedDialog() {
        val dialog = Dialog(this, R.style.dialogStyle)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_wallpaper_failed)

        val dismissPopup: LinearLayout = dialog.findViewById(R.id.dismissPopup)
        dismissPopup.setOnClickListener {
            dialog.dismiss()
        }

        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setDimAmount(0.5f)

        dialog.show()
    }

    private fun showStoragePermissionDialog() {
        val dialog = Dialog(this, R.style.dialogStyle)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_storage_access)

        val dismissPopup: LinearLayout = dialog.findViewById(R.id.dismissPopup)
        dismissPopup.setOnClickListener {
            dialog.dismiss()

            //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setDimAmount(0.5f)

        dialog.show()
    }

    private fun createGradients() {
        saveGradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColourInt, endColourInt)
        )
    }

    override fun onBackPressed() {
        constraintLayoutObjectAnimator(detailsHolder, "translationY",
                (90 * resources.displayMetrics.density + detailsHolder.height).roundToInt().toFloat(), 250,
                50, DecelerateInterpolator()
        )
        constraintLayoutObjectAnimator(actionsHolder, "translationY",
                (74 * resources.displayMetrics.density + detailsHolder.height).roundToInt().toFloat(), 250,
                0, DecelerateInterpolator()
        )
        UIElements.constraintLayoutVisibility(detailsHolder, View.INVISIBLE, 250)
        UIElements.constraintLayoutVisibility(actionsHolder, View.INVISIBLE, 250)
        UIElements.constraintLayoutVisibility(copiedNotification, View.INVISIBLE, 250)
        Handler().postDelayed({
            saveGradientButton.visibility = View.GONE
            setWallpaperButton.visibility = View.GONE
        }, 100)

        Vibration.mediumFeedback(this)

        Handler().postDelayed({
            super@ActivityGradientDetails.onBackPressed()
        }, 250)
    }
}