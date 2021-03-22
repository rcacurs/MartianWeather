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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.network.SensorData
import com.example.androiddevchallenge.network.WindDirectionSensorData
import com.example.androiddevchallenge.ui.theme.deepOrangeLight

@ExperimentalAnimationApi
@Composable
fun WeatherDataField(label: String, value: SensorData?, unit: String, expanded: Boolean) {
    ConstraintLayout() {
        val (valueField, labelField, unitField, details) = createRefs()
        val displayValueAverage = if (value?.average == null) {
            "N/A"
        } else {
            "%.1f".format(value.average)
        }
        val displayValueMax = if (value?.max == null) {
            "N/A"
        } else {
            "%.1f".format(value.max)
        }
        val displayValueMin = if (value?.min == null) {
            "N/A"
        } else {
            "%.1f".format(value.min)
        }
        val displaySamples = if (value?.samples == null) {
            "N/A"
        } else {
            "%d".format(value.samples)
        }

        Text(
            text = displayValueAverage,
            style = MaterialTheme.typography.h4,
            color = deepOrangeLight,
            modifier = Modifier.constrainAs(valueField) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
            }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.subtitle2.copy(textAlign = TextAlign.Center),
            modifier = Modifier.constrainAs(labelField) {
                top.linkTo(valueField.bottom)
                start.linkTo(valueField.start)
                end.linkTo(valueField.end)
            }
        )
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
            // For detailed View
            modifier = Modifier.constrainAs(details) {
                top.linkTo(labelField.bottom)
                start.linkTo(labelField.start)
//                end.linkTo(labelField.end)
            }
        ) {
            Column() {
                Text(
                    text = "max: $displayValueMax",
                    style = MaterialTheme.typography.caption,
                )
                Text(
                    text = "min: $displayValueMin",
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = "samples: $displaySamples",
                    style = MaterialTheme.typography.caption
                )
            }
        }
        Text(
            text = unit,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(unitField) {
                top.linkTo(valueField.top)
                bottom.linkTo(valueField.bottom)
                start.linkTo(valueField.end, margin = 4.dp)
            }
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun WindDirectionWeatherDataField(label: String, value: WindDirectionSensorData?, expanded: Boolean) {
    ConstraintLayout {
        val (valueField, labelField, unitField, details) = createRefs()
        val displayWinDirection = if (value?.compassPoint == null) {
            "N/A"
        } else {
            value.compassPoint.name
        }
        val compassDegrees = if (value?.compassDegrees == null) {
            "N/A"
        } else {
            "%.1f".format(value.compassDegrees)
        }

        val displaySamples = if (value?.samples == null) {
            "N/A"
        } else {
            "%d".format(value.samples)
        }

        if (value?.compassDegrees == null) {
            Text(
                text = displayWinDirection,
                style = MaterialTheme.typography.h4,
                color = deepOrangeLight,
                modifier = Modifier.constrainAs(valueField) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
            )
        } else {
            Compass(
                modifier = Modifier.width(46.dp).height(46.dp).constrainAs(valueField) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                },
                color = deepOrangeLight,
                angle = value.compassDegrees.toFloat()
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.subtitle2.copy(textAlign = TextAlign.Center),
            modifier = Modifier.constrainAs(labelField) {
                top.linkTo(valueField.bottom)
                start.linkTo(valueField.start)
                end.linkTo(valueField.end)
            }
        )
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
            // For detailed View
            modifier = Modifier.constrainAs(details) {
                top.linkTo(labelField.bottom)
                start.linkTo(labelField.start)
            }
        ) {
            Column() {
                Text(
                    text = "degrees: $compassDegrees °",
                    style = MaterialTheme.typography.caption,
                )
                Text(
                    text = "direction: $displayWinDirection",
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = "samples: $displaySamples",
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}
