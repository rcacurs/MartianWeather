/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.network.MarsWeatherData
import com.example.androiddevchallenge.network.Season
import com.example.androiddevchallenge.network.SensorData
import com.example.androiddevchallenge.network.generateFakeTemperature
import com.example.androiddevchallenge.network.generateFakeWindDirection
import com.example.androiddevchallenge.network.generateFakeWindSpeed
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.launch
import java.util.Date

@ExperimentalAnimationApi
@Composable
fun WeatherList(
    weatherData: List<MarsWeatherData>,
    context: Context?
) {
    val (previousList, setPreviousList) = remember { mutableStateOf(weatherData) }
    val (visible, setVisible) = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    /*
     *This check allows to animate list when the list is updated.
     *Also scroll state is reset to 0
     */
    if (weatherData === previousList && weatherData.isNotEmpty()) {
        setVisible(true)
    } else {
        setVisible(false)
        setPreviousList(weatherData)
        scope.launch {
            scrollState.scrollTo(0)
        }
    }
    AnimatedVisibility(
        initiallyVisible = false,
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)
        ),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(bottom = 8.dp)
        ) {
            for (item in weatherData) {
                WeatherCard(context, item)
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview("Weather Data List Preview", widthDp = 400, heightDp = 700)
@Composable
fun WeatherListPreview() {
    val data = listOf(
        MarsWeatherData(
            sol = 815,
            atmosphericPressure = SensorData(833.0, 120, 300.0, 400.0),
            atmosphericTemperature = generateFakeTemperature(),
            horizontalWindSpeed = generateFakeWindSpeed(),
            windDirection = generateFakeWindDirection(),
            season = Season.SUMMER,
            firstDate = Date(),
            lastDate = Date()
        ),
        MarsWeatherData(
            sol = 814,
            atmosphericPressure = SensorData(833.0, 120, 300.0, 400.0),
            atmosphericTemperature = generateFakeTemperature(),
            horizontalWindSpeed = generateFakeWindSpeed(),
            windDirection = generateFakeWindDirection(),
            season = Season.SUMMER,
            firstDate = Date(),
            lastDate = Date()
        )
    )
    MyTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colors.primary) {
            WeatherList(data, context = null)
        }
    }
}
