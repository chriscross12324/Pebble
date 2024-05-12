/*
 * Copyright (c) 2024 Chris Coulthard, all rights reserved.
 *
 * File: MainActivity.kt
 * Created: 2024-04-10, 7:57 p.m.
 * Last Modified: 2024-04-10, 7:26 p.m.
 *
 * This software is provided for viewing purposes only. You are welcome to
 * view the source code, however, no modifications or contributions are
 * accepted at this time.
 */

package com.simple.chris.pebble.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.overscroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simple.chris.pebble.components.GradientImageView
import com.simple.chris.pebble.components.GradientModule
import com.simple.chris.pebble.functions.Values
import com.simple.chris.pebble.ui.screens.ui.theme.PebbleTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PebbleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListDetailsLayout()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview
@Composable
fun ListDetailsLayout() {
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    var selectedGradientItem: GradientItem? by rememberSaveable(stateSaver = GradientItem.Saver) {
        mutableStateOf(null)
    }
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                GradientGrid(
                    onGradientItemClick = { id ->
                        selectedGradientItem = id
                        //navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                selectedGradientItem?.let { gradientItem ->
                    DetailsPage(item = gradientItem)
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GradientGrid(
    modifier: Modifier = Modifier,
    onGradientItemClick: (GradientItem) -> Unit,
) {
    Card {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            contentPadding = PaddingValues(
                start = 25.dp,
                end = 25.dp,
                top = 40.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            flingBehavior = ScrollableDefaults.flingBehavior(),
        ) {
            Values.gradientList.forEachIndexed { id, gradientItem ->
                item {

                    var pressed by remember {
                        mutableStateOf(false)
                    }
                    val newScale: Float by animateFloatAsState(
                        targetValue = if (pressed) 0.9f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )

                    GradientModule(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        //scale.animateTo(1.1f, spring(stiffness = Spring.StiffnessLow))
                                        pressed = true
                                        //pressed = false

                                    },
                                )
                            }
                            .scale(newScale),
                        /*modifier = modifier.clickable {
                            onGradientItemClick(GradientItem(id))
                        },*/
                        gradientName = gradientItem.gradientName,
                        hexList = gradientItem.gradientHEXList
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsPage(
    modifier: Modifier = Modifier,
    item: GradientItem
) {
    GradientImageView(hexList = Values.gradientList[item.id].gradientHEXList, cornerRadius = 0f)
}

fun isScreenLarge(): Boolean {
    return false
}

class GradientItem(val id: Int) {
    companion object {
        val Saver: Saver<GradientItem?, Int> = Saver({ it?.id }, ::GradientItem)
    }
}