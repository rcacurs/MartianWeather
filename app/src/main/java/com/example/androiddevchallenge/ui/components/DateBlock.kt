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
import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.androiddevchallenge.network.MarsWeatherData
import com.example.androiddevchallenge.network.Season
import com.example.androiddevchallenge.ui.theme.deepOrangeLight
import java.text.DateFormat

@ExperimentalAnimationApi
@Composable
fun DateBlock(
    context: Context?,
    weatherData: MarsWeatherData,
    expanded: Boolean,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            "Sol ${weatherData.sol}",
            color = deepOrangeLight,
            style = MaterialTheme.typography.h4
        )
        val season = when (weatherData.season) {
            Season.SUMMER -> "summer"
            Season.FALL -> "fall"
            Season.WINTER -> "winter"
            Season.SPRING -> "spring"
            else -> "Unknown"
        }
        Text(
            text = season,
            style = MaterialTheme.typography.subtitle1,
            color = deepOrangeLight
        )
        val date = DateUtils.formatDateTime(context, weatherData.firstDate.time, DateUtils.FORMAT_NO_YEAR)
        Text(date, style = MaterialTheme.typography.h5)

        val dateStringStart = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(weatherData.firstDate)
        val dateStringEnd = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(weatherData.lastDate)

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
            // For detailed View
        ) {
            Column {
                Text(
                    text = "period start:\n$dateStringStart",
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = "period end:\n$dateStringEnd",
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}
