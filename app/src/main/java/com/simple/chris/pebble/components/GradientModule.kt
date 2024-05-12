/*
 * Copyright (c) 2024 Chris Coulthard, all rights reserved.
 *
 * File: GradientModule.kt
 * Created: 2024-04-07, 2:17 p.m.
 * Last Modified: 2024-04-07, 2:17 p.m.
 *
 * This software is provided for viewing purposes only. You are welcome to
 * view the source code, however, no modifications or contributions are
 * accepted at this time.
 */

package com.simple.chris.pebble.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simple.chris.pebble.ui.theme.googleSansFamily


@Composable
@Preview
fun GradientModule(
    modifier: Modifier = Modifier.fillMaxWidth(),
    gradientName: String = "ERR",
    hexList: List<String> = listOf("#000000"),
) {

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GradientImageView(
            hexList = hexList,
            cornerRadius = 25f,
            modifier = modifier
                .height(170.dp)
                .fillMaxWidth()
        )

        Text(
            text = gradientName,
            modifier
                .height(40.dp)
                .padding(start = 24.dp, end = 24.dp, top = 8.dp)
                ,
            maxLines = 2,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
