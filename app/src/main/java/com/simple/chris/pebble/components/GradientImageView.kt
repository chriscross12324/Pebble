/*
 * Copyright (c) 2024 Chris Coulthard, all rights reserved.
 *
 * File: GradientImageView.kt
 * Created: 2024-04-08, 10:51 p.m.
 * Last Modified: 2024-04-08, 10:51 p.m.
 *
 * This software is provided for viewing purposes only. You are welcome to
 * view the source code, however, no modifications or contributions are
 * accepted at this time.
 */

package com.simple.chris.pebble.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.simple.chris.pebble.functions.convertFloatToDP

@Composable
fun GradientImageView(
    modifier: Modifier = Modifier,
    gradientColours: List<Color>,
    cornerRadius: Float
) {
    var colours = gradientColours
    if (gradientColours.size >= 2) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(size = convertFloatToDP(LocalContext.current, cornerRadius)))
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColours,
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        )
    }
}