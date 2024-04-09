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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.simple.chris.pebble.ui.theme.googleSansFamily


@Composable
@Preview
fun GradientModule(
    modifier: Modifier = Modifier,
    gradientName: String = "Name",
    gradientColours: List<Color> = listOf(Color.Red, Color.Blue),
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(13.dp)
            .background(Color.Transparent)
    ) {
        val (gradientImageView, gradientTextView) = createRefs()

        GradientImageView(
            gradientColours = gradientColours,
            cornerRadius = 25f,
            modifier = modifier
                .height(170.dp)
                .fillMaxWidth()
                .constrainAs(gradientImageView) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )

        Text(
            text = gradientName,
            modifier
                .height(40.dp)
                .constrainAs(gradientTextView) {
                    top.linkTo(gradientImageView.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            maxLines = 2,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            overflow = TextOverflow.Ellipsis
        )
    }
}
