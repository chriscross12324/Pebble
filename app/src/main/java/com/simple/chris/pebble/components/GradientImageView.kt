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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.simple.chris.pebble.functions.convertFloatToDP
import com.simple.chris.pebble.ui.utils.hexToColour

@Composable
fun GradientImageView(
    modifier: Modifier = Modifier.fillMaxHeight().fillMaxWidth(),
    hexList: List<String>,
    cornerRadius: Float
) {
    //Convert HEX to Color
    var colourList = hexList.map { hexToColour(it) }

    //Add additional colour if only one (1) colour present
    if (colourList.size < 2) {
        colourList = listOf(colourList[0], colourList[0])
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(size = convertFloatToDP(LocalContext.current, cornerRadius)))
            .background(
                brush = Brush.linearGradient(
                    colors = colourList,
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    )
}