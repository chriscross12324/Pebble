package com.simple.chris.pebble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
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
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.constraintLayoutElevationAnimator
import com.simple.chris.pebble.UIElements.gradientDrawable
import com.simple.chris.pebble.UIElements.viewObjectAnimator
import kotlinx.android.synthetic.main.activity_gradient_details.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class GradientDetailsActivity : AppCompatActivity(), PopupDialogButtonRecyclerAdapter.OnButtonListener {

    private lateinit var gradientNameString: String
    private lateinit var gradientDescriptionString: String

    private var startColourInt: Int = 0
    private var endColourInt: Int = 0
    private var detailsHolderHeight = 0

    private var detailsHolderExpanded = false
    private var copiedAnimationPlaying = false

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_gradient_details)
        Values.currentActivity = "GradientDetailsActivity"
        postponeEnterTransition()

        //Sets
        gradientNameString = intent.getStringExtra("gradientName") as String
        gradientDescriptionString = intent.getStringExtra("description") as String
        startColourInt = Color.parseColor(intent.getStringExtra("startColour"))
        endColourInt = Color.parseColor(intent.getStringExtra("endColour"))

        gradientDrawable(this, true, gradientViewStatic, startColourInt, endColourInt, 20f)
        gradientViewStatic.transitionName = gradientNameString

        if (gradientDescriptionString == "") {
            gradientDescription.visibility = View.GONE
        }

        gradientViewStatic.post {
            startPostponedEnterTransition()
            Values.currentActivity = "GradientDetails"
            buttons()
            preViewPlacements()
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
        gradientDrawableStartCircle.setStroke(convertToDP(this, 3f).roundToInt(), startColourInt)
        val gradientDrawableEndCircle = GradientDrawable()
        gradientDrawableEndCircle.shape = GradientDrawable.OVAL
        gradientDrawableEndCircle.setStroke(convertToDP(this, 3f).roundToInt(), endColourInt)
        startHexCircle.background = gradientDrawableStartCircle
        endHexCircle.background = gradientDrawableEndCircle
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

                viewObjectAnimator(copiedNotification, "translationY", 0f, 500,
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

                viewObjectAnimator(copiedNotification, "translationY", 0f, 500,
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
                UIElement.popupDialog(this, "readWritePermission", R.drawable.icon_storage, R.string.dialog_title_eng_permission_storage, null, R.string.dialog_body_eng_permission_storage, AppHashMaps.readWritePermissionArrayList(), window.decorView, this)
            } else {
                UIElement.popupDialog(this, "saveGradient", R.drawable.icon_save, R.string.dialog_title_eng_save_gradient, null, R.string.dialog_body_eng_save_gradient,
                        AppHashMaps.saveGradientArrayList(), window.decorView, this)
            }
        }

        setWallpaperButton.setOnClickListener {
            UIElement.popupDialog(this, "setWallpaper", R.drawable.icon_wallpaper, R.string.dialog_title_eng_set_wallpaper, null, R.string.dialog_body_eng_set_wallpaper, AppHashMaps.setWallpaperArrayList(), window.decorView, this)
        }
    }

    private val pushHoldListener = View.OnClickListener {
        Values.hintPushHoldDismissed = true
        UIElements.oneButtonHider(this)
    }

    private fun pushHoldPopup() {
        if (!Values.hintPushHoldDismissed) {
            Handler().postDelayed({
                UIElements.oneButtonDialog(this, R.drawable.icon_view, R.string.pushHoldTitle, R.string.PushHoldGradient, R.string.text_eng_ok, pushHoldListener)
            }, 1500)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun animateGradient() {
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

    private val codeCopyListener = View.OnClickListener {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse(Environment.getExternalStorageDirectory().path + File.pathSeparator + "Pebble" + File.pathSeparator)
        intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR)
        startActivity(Intent.createChooser(intent, "Open folder"))
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

        Handler().postDelayed({
            super@GradientDetailsActivity.onBackPressed()
        }, 250)
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "setWallpaper" -> {
                when (position) {
                    0 -> {
                        val wallpaperManager = WallpaperManager.getInstance(this)

                        try {
                            wallpaperManager.setBitmap(createBitmap(gradientDrawable(this, false, null, startColourInt, endColourInt, 0f) as Drawable,
                                    Calculations.screenMeasure(this, "width"), Calculations.screenMeasure(this, "height")))
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.activity_gradient_details.buttons.setWallpaper: ${e.localizedMessage}")
                        }
                    }
                    1 -> {

                    }
                    2 -> {
                        UIElement.popupDialogHider()
                    }
                }
            }
            "readWritePermission" -> {
                UIElement.popupDialogHider()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
            "saveGradient" -> {
                when (position) {
                    0 -> {
                        /**
                         * Makes directory if doesn't exist
                         */
                        val savePath = getExternalFilesDir(null)!!.absolutePath
                        val saveDir = File(savePath + File.separator + "Pebble" + File.separator)
                        saveDir.mkdirs()

                        try {
                            /** Makes the file to populate **/
                            val file = File(saveDir, "$gradientNameString.png".replace(" ", "_").toLowerCase())
                            val fileOutputStream = FileOutputStream(file)

                            /** Creates the gradient Bitmap to populate the file above **/
                            createBitmap(gradientDrawable(this, false, null, startColourInt, endColourInt, 0f) as Drawable,
                                    Calculations.screenMeasure(this, "width"), Calculations.screenMeasure(this, "height")).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

                            fileOutputStream.flush()
                            fileOutputStream.close()

                            Log.e("INFO", "Successfully Saved to $saveDir")
                            UIElement.popupDialog(this, "gradientSaved", R.drawable.icon_check, R.string.dialog_title_eng_gradient_saved, null,
                                    R.string.dialog_body_eng_gradient_saved, AppHashMaps.gradientSavedArrayList(), window.decorView, this)

                        } catch (e: java.lang.Exception) {
                            Log.e("INFO", "Failed to save due to: ${e.localizedMessage}")
                        }
                    }
                    1 -> UIElement.popupDialogHider()
                }
            }
            "gradientSaved" -> {
                UIElement.popupDialogHider()
            }
        }
    }
}