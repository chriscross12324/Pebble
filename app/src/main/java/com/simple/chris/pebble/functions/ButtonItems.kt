package com.simple.chris.pebble.functions

import com.simple.chris.pebble.R
import java.io.Serializable

/**
 * Dialogs
 */
fun arrayBrowseMenu() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_about, R.drawable.icon_question),
        ButtonItem(R.string.word_settings, R.drawable.icon_settings),
        ButtonItem(R.string.word_refresh, R.drawable.icon_reload),
    )
}

fun arrayOnOff() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_on, R.drawable.icon_on),
        ButtonItem(R.string.word_off, R.drawable.icon_off),
    )
}

fun arrayTheme() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_light, R.drawable.icon_theme_light),
        ButtonItem(R.string.word_dark, R.drawable.icon_theme_dark),
        ButtonItem(R.string.word_darker, R.drawable.icon_theme_black),
    )
}

fun arrayNoConnection() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_retry, R.drawable.icon_reload),
        ButtonItem(R.string.word_cancel, R.drawable.icon_close),
    )
}

fun arraySetWallpaper() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.dual_home_screen, R.drawable.icon_home),
        ButtonItem(R.string.dual_lock_screen, R.drawable.icon_lock),
        ButtonItem(R.string.word_cancel, R.drawable.icon_close),
    )
}

fun arrayBack() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_back, R.drawable.icon_arrow_left),
    )
}

fun arrayClose() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_close, R.drawable.icon_close),
    )
}

fun arrayYesCancel() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_yes, R.drawable.icon_check),
        ButtonItem(R.string.word_cancel, R.drawable.icon_close),
    )
}

fun arrayAllowDeny() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_allow, R.drawable.icon_check),
        ButtonItem(R.string.word_deny, R.drawable.icon_close),
    )
}


/**
 * Screens
 */
fun arraySettings() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_theme, R.drawable.icon_brush),
        ButtonItem(R.string.word_vibration, R.drawable.icon_vibrate),
        ButtonItem(R.string.word_blur, R.drawable.icon_blur_on),
        ButtonItem(R.string.dual_split_screen, R.drawable.icon_split_screen),
    )
}

fun arrayAbout() : List<ButtonItem> {
    return listOf(
        ButtonItem(R.string.word_developer, R.drawable.icon_dev),
        ButtonItem(R.string.build_date, R.drawable.icon_build),
        ButtonItem(R.string.server_version, R.drawable.icon_server_version),
    )
}

fun arraySearchByColour() : List<ColourButtonItem> {
    return listOf(
        ColourButtonItem("#F44336", "red"),
        ColourButtonItem("#FF9800", "orange"),
        ColourButtonItem("#FFEB3B", "yellow"),
        ColourButtonItem("#4CAF50", "green"),
        ColourButtonItem("#2196F3", "blue"),
        ColourButtonItem("#9C27B0", "purple"),
        ColourButtonItem("#1c1c1c", "dark"),
        ColourButtonItem("#f5f5f5", "light"),
    )
}


class ButtonItem constructor(var buttonText: Int, var buttonIcon: Int) : Serializable

class ColourButtonItem constructor(var buttonHex: String, var buttonColour: String)