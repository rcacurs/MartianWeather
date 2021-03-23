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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import java.util.Date

@ExperimentalAnimationApi
@Composable
fun WeatherCard(
    context: Context?,
    weatherData: MarsWeatherData = MarsWeatherData(
        sol = 815,
        atmosphericPressure = SensorData(833.0, 120, 300.0, 400.0),
        atmosphericTemperature = generateFakeTemperature(),
        horizontalWindSpeed = generateFakeWindSpeed(),
        windDirection = generateFakeWindDirection(),
        season = Season.SUMMER,
        firstDate = Date(),
        lastDate = Date()
    ),
    temperatureInCelsius: Boolean = true
) {
    var nudge = remember { mutableStateOf(true) }
    val expanded = remember { mutableStateOf(false) }
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { expanded.value = !expanded.value; nudge.value = true }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateBlock(context, weatherData, expanded.value, modifier = Modifier.weight(1f))
            Divider(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
                    .height(100.dp)
                    .width(1.dp),
                color = MaterialTheme.colors.onSurface
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                WeatherDataField(
                    label = "temperature",
                    value = if (temperatureInCelsius) {
                        weatherData.atmosphericTemperature
                    } else {
                        weatherData.atmosphericTemperature?.toCelsius()
                    },
                    unit = if (temperatureInCelsius) "°C" else "°F",
                    expanded = expanded.value
                )
                WeatherDataField(
                    label = "wind speed",
                    value = weatherData.horizontalWindSpeed,
                    unit = "m/s",
                    expanded = expanded.value
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDataField(
                    label = "atm. pressure",
                    value = weatherData.atmosphericPressure,
                    unit = "Pa",
                    expanded = expanded.value
                )
                WindDirectionWeatherDataField(
                    label = "wind direction",
                    value = weatherData.windDirection,
                    expanded = expanded.value,
                    nudge = nudge
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview("Weather Card Preview", widthDp = 400, heightDp = 360)
@Composable
fun CardPreview() {
    MyTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colors.primary) {
            WeatherCard(context = null)
        }
    }
}
