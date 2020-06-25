package com.simple.chris.pebble

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_popup.*

object UIElement {

    lateinit var popupDialog: Dialog

    fun popupDialog(context: Context, popupName: String, icon: Int?, title: Int?, titleString: String?, description: Int,
                    buttonArrayList: ArrayList<HashMap<String, Int>>?, decorView: View?, listener: PopupDialogButtonRecyclerAdapter.OnButtonListener?) {
        /**
         * Checks if popUpDialog is visible; hides it if it does
         */
        try {
            if (popupDialog.isShowing) {
                popupDialog.dismiss()
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.ui_element.popup_dialog: ${e.localizedMessage}")
        }

        /** Creates popupDialog **/
        popupDialog = Dialog(context, R.style.dialogStyle)
        popupDialog.setCancelable(false)
        popupDialog.setContentView(R.layout.dialog_popup)

        val dialogWindow: Window = popupDialog.window!!
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0.1f)
        dialogWindow.setGravity(Gravity.CENTER)

        /** Set popupDialog layout **/
        val dialogMain = popupDialog.holder
        val progressCircle = popupDialog.progressCircle
        val dialogIcon = popupDialog.permissionIcon
        val dialogTitle = popupDialog.permissionTitle
        val dialogDescription = popupDialog.permissionDescription
        val dialogRecycler = popupDialog.popupButtonRecycler

        if (icon != null) {
            dialogIcon.setImageResource(icon)
        } else {
            dialogIcon.visibility = View.INVISIBLE
            progressCircle.visibility = View.VISIBLE
        }

        if (title != null) {
            dialogTitle.setText(title)
        } else {
            dialogTitle.text = titleString
        }
        dialogDescription.setText(description)

        /** Set popupLayout recycler **/
        if (buttonArrayList != null && listener != null) {
            try {
                dialogRecycler.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val adapter = PopupDialogButtonRecyclerAdapter(context, popupName, buttonArrayList, listener)

                dialogRecycler.layoutManager = layoutManager
                dialogRecycler.adapter = adapter
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
        }

        popupDialog.show()

        /** Animate popupLayout in **/
        dialogMain.post {
            UIElements.viewObjectAnimator(dialogMain, "scaleX", 1f, 350, 100, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "scaleY", 1f, 350, 100, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(dialogMain, "alpha", 1f, 150, 100, LinearInterpolator())

            if (buttonArrayList != null) {
                UIElements.viewObjectAnimator(dialogRecycler, "scaleX", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "scaleY", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "alpha", 1f, 150, 100, LinearInterpolator())
            }
        }

        /** Create blurView **/
        if (decorView != null) {
            try {
                val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = decorView.background

                popupDialog.blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(context))
                        .setBlurRadius(20f)
                        .setHasFixedTransformationMatrix(true)
            } catch (e: Exception) {
                Log.e("ERR", "pebble.ui_elements.popup_dialog: ${e.localizedMessage}")
            }
        }
    }

    fun popupDialogHider() {
        /**
         * Checks if popupDialog is visible
         */
        try {
            if (popupDialog.isShowing) {
                val dialogMain = popupDialog.holder
                val dialogRecycler = popupDialog.popupButtonRecycler

                UIElements.viewObjectAnimator(dialogMain, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogMain, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogMain, "alpha", 0f, 150, 200, LinearInterpolator())

                UIElements.viewObjectAnimator(dialogRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(dialogRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

                Handler().postDelayed({
                      popupDialog.dismiss()
                }, 450)
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.ui_elements.popup_dialog_hider: ${e.localizedMessage}")
        }
    }

}