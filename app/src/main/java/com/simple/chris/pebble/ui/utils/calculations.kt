/*
 * Copyright (c) 2024 Chris Coulthard, all rights reserved.
 *
 * File: calculations.kt
 * Created: 2024-04-08, 11:32 p.m.
 * Last Modified: 2024-04-08, 11:32 p.m.
 *
 * This software is provided for viewing purposes only. You are welcome to
 * view the source code, however, no modifications or contributions are
 * accepted at this time.
 */

package com.simple.chris.pebble.ui.utils

import androidx.compose.ui.graphics.Color

fun hexToColour(hex: String): Color {
    return Color(android.graphics.Color.parseColor(hex))
}