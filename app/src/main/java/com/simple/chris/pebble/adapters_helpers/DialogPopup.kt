package com.simple.chris.pebble.adapters_helpers

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simple.chris.pebble.R
import com.simple.chris.pebble.activities.GradientCreator
import com.simple.chris.pebble.activities.MainActivity
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.dialog_popup.*
import java.io.Serializable

class DialogPopup : DialogFragment(), PopupDialogButtonRecycler.OnButtonListener {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_popup, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.window!!.setDimAmount(0f)
        Values.dialogPopup = this
        if (arguments!!.getString("dialogName") == "connecting") {
            testConnection()
        }

        if (Values.settingsSpecialEffects) {
            try {
                val rootView = activity!!.window.decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = activity!!.window.decorView.background

                blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(RenderScriptBlur(activity))
                        .setBlurRadius(20f)
                        .setOverlayColor(Color.parseColor(Values.dialogBackgroundTint))
            } catch (e: Exception) {
                Log.e("ERR", "pebble.gradient_long_click_dialog: ${e.localizedMessage}")
            }
        } else {
            backgroundDimmer.alpha = Values.dialogBackgroundDimmer
        }

        if (arguments!!.getInt("icon") != 0) {
            permissionIcon.setImageResource(arguments!!.getInt("icon"))
        } else {
            permissionIcon.visibility = View.INVISIBLE
            progressCircle.visibility = View.VISIBLE
        }

        if (arguments!!.getInt("title") != null) {
            permissionTitle.setText(arguments!!.getInt("title"))
        } else {
            permissionTitle.text = arguments!!.getString("titleString")
        }

        permissionDescription.setText(arguments!!.getInt("description"))

        holder.post {
            UIElements.viewObjectAnimator(holder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            UIElements.viewObjectAnimator(holder, "alpha", 1f, 100, 100, LinearInterpolator())
            UIElements.viewObjectAnimator(drawCaller, "scaleY", 2f, 60000, 0, LinearInterpolator())

            if (arguments!!.getSerializable("array") != null) {
                UIElements.viewObjectAnimator(popupButtonRecycler, "scaleX", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(popupButtonRecycler, "scaleY", 1f, 350, 100, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(popupButtonRecycler, "alpha", 1f, 150, 100, LinearInterpolator())
            }
        }

        blurView.setOnClickListener {
            if (arguments!!.getString("dialogName")!!.contains("setting") && (activity as Context).toString().contains("MainActivity")) {
                onDismiss(dialog!!)
            }
        }

        if (arguments!!.getSerializable("array") != null) {
            try {
                popupButtonRecycler.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                val adapter = PopupDialogButtonRecycler((activity as Context), arguments!!.getString("dialogName")!!, arguments!!.getSerializable("array")!! as ArrayList<HashMap<String, Int>>, this)

                popupButtonRecycler.layoutManager = layoutManager
                popupButtonRecycler.adapter = adapter
            } catch (e: Exception) {
                Log.e("ERR", "pebble.dialog_popup.on_view_created: ${e.localizedMessage}")
            }
        }
    }

    private fun testConnection() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.gradientList.isNotEmpty()) {
                onDismiss(Values.dialogPopup.dialog!!)
                Log.e("INFO", "Trying to dismiss")
            } else {
                testConnection()
                Log.e("INFO", "Looping")
            }
        }, 200)
    }

    override fun onDismiss(dialog: DialogInterface) {
        try {
            if (holder != null) {
                UIElements.viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

                UIElements.viewObjectAnimator(popupButtonRecycler, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(popupButtonRecycler, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                UIElements.viewObjectAnimator(popupButtonRecycler, "alpha", 0f, 150, 200, LinearInterpolator())

                Handler(Looper.getMainLooper()).postDelayed({
                    super.onDismiss(dialog)
                }, 450)
            }
        } catch (e: Exception) {
            onDismiss(Values.dialogPopup.dialog!!)
        }


    }

    companion object {
        fun newDialog(buttonArray: ArrayList<HashMap<String, Int>>?, dialogName: String, icon: Int?, title: Int?, titleString: String?, description: Int): DialogPopup {
            val frag = DialogPopup()
            val args = Bundle()
            args.putString("dialogName", dialogName)
            args.putInt("description", description)
            if (buttonArray != null) {
                args.putSerializable("array", buttonArray)
            }
            if (icon != null) {
                args.putInt("icon", icon)
            }
            if (title != null) {
                args.putInt("title", title)
            }
            if (titleString != null) {
                args.putString("titleString", titleString)
            }
            frag.arguments = args

            return frag
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        Values.dialogPopup = this
        when (activity) {
            (activity as MainActivity) -> (activity as MainActivity).popupDialogHandler(popupName, position)
            (activity as GradientCreator) -> Log.e("INFO", "GradientCreator")
        }
        //(activity as MainActivity).popupDialogHandler(popupName, position)
    }

}