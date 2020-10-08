package com.simple.chris.pebble.activities

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SettingsRecyclerView
import com.simple.chris.pebble.functions.HashMaps
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.Values
import kotlinx.android.synthetic.main.activity_settings_new.*
import kotlinx.android.synthetic.main.small_screen.*

class SettingsFrag : Fragment(), SettingsRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.small_screen, container, false)
        root.post {
            (activity as MainActivity).showSmallScreen(root.measuredHeight.toFloat())
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ssIcon.setImageResource(R.drawable.icon_settings)
        ssTitle.setText(R.string.word_settings)
        ssDescription.setText(R.string.sentence_customize_pebble)
        createRecycler()
    }

    private fun createRecycler() {
        ssRecycler.setHasFixedSize(true)
        val buttonLayoutManager = LinearLayoutManager((activity as MainActivity), LinearLayoutManager.HORIZONTAL, false)
        val buttonAdapter = SettingsRecyclerView((activity as MainActivity), HashMaps.settingsArray(), this)

        ssRecycler.layoutManager = buttonLayoutManager
        ssRecycler.adapter = buttonAdapter
    }



    override fun onButtonClick(position: Int, view: View) {
        when (position) {
            0 -> {
                //Theme
                UIElement.popupDialog((activity as MainActivity), "settingTheme", R.drawable.icon_brush, R.string.word_theme, null, R.string.question_setting_theme,
                        HashMaps.lightDarkDarker(), (activity as MainActivity).window.decorView, this)
            }
            1 -> {
                //Vibration
                UIElement.popupDialog((activity as MainActivity), "settingVibration", R.drawable.icon_vibrate_on, R.string.word_vibration, null, R.string.question_setting_vibration,
                        HashMaps.onOff(), (activity as MainActivity).window.decorView, this)
            }
            2 -> {
                //Special Effects
                UIElement.popupDialog((activity as MainActivity), "settingSpecialEffects", R.drawable.icon_blur_on, R.string.dual_special_effects, null, R.string.question_setting_effects,
                        HashMaps.onOff(), (activity as MainActivity).window.decorView, this)
            }
            3 -> {
                //Cellular Data
                UIElement.popupDialog((activity as MainActivity), "settingNetwork", R.drawable.icon_cell_wifi, R.string.word_network, null, R.string.question_setting_network,
                        HashMaps.onOffAsk(), (activity as MainActivity).window.decorView, this)
            }
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "settingTheme" -> {
                val current = Values.settingThemes
                when (position) {
                    0 -> {
                        //Light
                        Values.settingThemes = "light"
                    }
                    1 -> {
                        //Dark
                        Values.settingThemes = "dark"
                    }
                    2 -> {
                        //Darker
                        Values.settingThemes = "darker"
                    }
                }
                UIElement.popupDialogHider()
                Handler().postDelayed({
                    if (current != Values.settingThemes) {
                        (activity as MainActivity).hideSmallScreen()
                        (activity as MainActivity).refreshTheme()
                    }
                }, 450)
            }
            "settingVibration" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingVibrations = true
                    }
                    1 -> {
                        //Off
                        Values.settingVibrations = false
                    }
                }
                UIElement.popupDialogHider()
            }
            "settingSpecialEffects" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingsSpecialEffects = true
                    }
                    1 -> {
                        //Off
                        Values.settingsSpecialEffects = false
                    }
                }
                UIElement.popupDialogHider()
                //UIElement.setWallpaper((activity as MainActivity), wallpaperImageViewer, wallpaperImageAlpha)
            }
            "settingNetwork" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.useMobileData = "on"
                    }
                    1 -> {
                        //Off
                        Values.useMobileData = "off"
                    }
                    2 -> {
                        //Ask Every-time
                        Values.useMobileData = "ask"
                    }
                }
                UIElement.popupDialogHider()
            }
        }
    }

}