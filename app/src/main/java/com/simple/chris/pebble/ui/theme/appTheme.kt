/*
 * Copyright (c) 2024 Chris Coulthard, all rights reserved.
 *
 * File: appTheme.kt
 * Created: 2024-04-08, 11:17 p.m.
 * Last Modified: 2024-04-08, 11:17 p.m.
 *
 * This software is provided for viewing purposes only. You are welcome to
 * view the source code, however, no modifications or contributions are
 * accepted at this time.
 */

package com.simple.chris.pebble.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.simple.chris.pebble.ui.utils.hexToColour

enum class Theme {
    Light,
    Dark,
    Darker
}

object ThemeLightColours {
    val colourBackground = staticCompositionLocalOf { hexToColour("#FFFFFFFF") }
    val colourForeground = staticCompositionLocalOf { hexToColour("#FFF2F2F2") }
    val colourFirstText = staticCompositionLocalOf { hexToColour("#FF121212") }
    val colourSecondText = staticCompositionLocalOf { hexToColour("#BF121212") }
    val colourThirdText = staticCompositionLocalOf { hexToColour("#80121212") }
}

object ThemeDarkColours {
    val colourBackground = staticCompositionLocalOf { hexToColour("#FF0C0C0C") }
    val colourForeground = staticCompositionLocalOf { hexToColour("#FF171717") }
    val colourFirstText = staticCompositionLocalOf { hexToColour("#FFFFFFFF") }
    val colourSecondText = staticCompositionLocalOf { hexToColour("#BFFFFFFF") }
    val colourThirdText = staticCompositionLocalOf { hexToColour("#80FFFFFF") }
}

object ThemeDarkerColours {
    val colourBackground = staticCompositionLocalOf { hexToColour("#FF000000") }
    val colourForeground = staticCompositionLocalOf { hexToColour("#FF0A0A0A") }
    val colourFirstText = staticCompositionLocalOf { hexToColour("#FFFFFFFF") }
    val colourSecondText = staticCompositionLocalOf { hexToColour("#BFFFFFFF") }
    val colourThirdText = staticCompositionLocalOf { hexToColour("#80FFFFFF") }
}

@Composable
fun AppTheme (
    theme: Theme,
    content: @Composable () -> Unit
) {
    val colours = when (theme) {
        Theme.Light -> ThemeLightColours
        Theme.Dark -> ThemeDarkColours
        Theme.Darker -> ThemeDarkColours
    }

    MaterialTheme {
        content()
    }
}

//private fun getColourBackground (theme: Theme): Color