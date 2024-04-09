/*
 * Copyright (c) 2024 Chris Coulthard, all rights reserved.
 *
 * File: fontFamilies.kt
 * Created: 2024-04-08, 11:10 p.m.
 * Last Modified: 2024-04-08, 11:10 p.m.
 *
 * This software is provided for viewing purposes only. You are welcome to
 * view the source code, however, no modifications or contributions are
 * accepted at this time.
 */

package com.simple.chris.pebble.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.simple.chris.pebble.R

val googleSansFamily = FontFamily(
    Font(R.font.googlesans_medium, FontWeight.Medium),
    Font(R.font.googlesans_bold, FontWeight.Bold)
)