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
package com.example.androiddevchallenge

import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import com.example.androiddevchallenge.network.MarsWeatherData
import com.example.androiddevchallenge.network.Season
import com.example.androiddevchallenge.network.SensorData
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.deepOrangeLight
import com.example.androiddevchallenge.ui.theme.transparentBlack
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = transparentBlack.toArgb()
        window.navigationBarColor = transparentBlack.toArgb()

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val viewModel: MainViewModel by viewModels()
//        viewModel.refreshWeatherData()
        setContent {
            MyTheme {
                MyApp(this, viewModel)
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp(context: Context?, viewModel: MainViewModel) {
    // Surface(color = MaterialTheme.colors.background) {
    val weatherData: List<MarsWeatherData> by (viewModel.marsWeatherData)
        .observeAsState(mutableListOf<MarsWeatherData>())
    ProvideWindowInsets {
        Image(
            painterResource(R.drawable.mars_crop_1),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Scaffold(
            modifier = Modifier.background(Color.Transparent).systemBarsPadding(),
            backgroundColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Mars Weather ", color = deepOrangeLight)
                        Text("at Elysium Planitia", style = MaterialTheme.typography.caption)
                    },
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp
                )
            },
            content = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 8.dp)
                ) {

                    for (item in weatherData) {
                        WeatherCard(context, item)
                    }
                }
            }
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun WeatherCard(
    context: Context?,
    weatherData: MarsWeatherData = MarsWeatherData(
        sol = 815,
        atmosphericPressure = SensorData(833.0, 120, 300.0, 400.0),
        atmosphericTemperature = null,
        horizontalWindSpeed = null,
        season = Season.SUMMER,
        firstDate = Date(),
        lastDate = Date()
    )
) {
    val expanded = remember { mutableStateOf(false) }
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { expanded.value = !expanded.value }
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
                WeatherDataField("temperature", weatherData.atmosphericTemperature, "°F", expanded.value)
                WeatherDataField("wind speed", weatherData.horizontalWindSpeed, "m/s", expanded.value)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDataField("atm. pressure", weatherData.atmosphericPressure, "Pa", expanded.value)
                WeatherDataField("wind direction", null, "", expanded.value) // TODO implement weather direction data in data class
            }
        }
    }
}

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
                end.linkTo(labelField.end)
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

// @Preview("Light Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun LightPreview() {
//    MyTheme {
//        MyApp(null)
//    }
// }
//
// @Preview("Dark Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun DarkPreview() {
//    MyTheme(darkTheme = true) {
//        MyApp(null)
//    }
// }

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

fun getShortDateInstanceWithoutYears(): DateFormat {
    val sdf: SimpleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT) as SimpleDateFormat
    Log.d("Simple date pattern", "${sdf.toPattern()}")
    sdf.applyPattern(sdf.toPattern().replace("[^\\p{Alpha}]*y+[^\\p{Alpha}]*", ""))
    return sdf
}
