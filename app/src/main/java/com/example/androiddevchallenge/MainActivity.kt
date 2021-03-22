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
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.MutableLiveData
import com.example.androiddevchallenge.network.MarsWeatherData
import com.example.androiddevchallenge.network.Season
import com.example.androiddevchallenge.network.SensorData
import com.example.androiddevchallenge.network.generateFakeTemperature
import com.example.androiddevchallenge.network.generateFakeWindDirection
import com.example.androiddevchallenge.network.generateFakeWindSpeed
import com.example.androiddevchallenge.ui.components.LoadingIndicator
import com.example.androiddevchallenge.ui.components.WeatherList
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.deepOrangeLight
import com.example.androiddevchallenge.ui.theme.transparentBlack
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.systemBarsPadding
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
fun MyApp(context: Context?, viewModel: MainViewModel?) {
    val weatherLiveData = viewModel?.marsWeatherData
        ?: MutableLiveData(
            listOf(
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
        )
    val connectingLiveData = viewModel?.connecting
        ?: MutableLiveData(true)
    val weatherData: List<MarsWeatherData> by (weatherLiveData)
        .observeAsState(mutableListOf())

    val connectionProblemLiveData = viewModel?.connectionErrorFlag
        ?: MutableLiveData(false)
    val connectionProblemFlag: Boolean by (connectionProblemLiveData).observeAsState(false)

    val connecting: Boolean by (connectingLiveData).observeAsState(false)

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
                Column {
                    LoadingIndicator(connecting)

                    if (connectionProblemFlag) {
                        Snackbar(
                            action = {
                                Button(
                                    onClick = { viewModel?.refreshWeatherData() }
                                ) {
                                    Text("Refresh")
                                }
                            },
                            elevation = 0.dp,
                            modifier = Modifier.padding(8.dp),
                            backgroundColor = MaterialTheme.colors.surface
                        ) {
                            Text(text = "Problem connecting to servers!", color = deepOrangeLight)
                        }
                    }

                    WeatherList(
                        weatherData,
                        context,
                        connecting
                    ) { viewModel?.refreshWeatherData() }
                }
            }
        )
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 400, heightDp = 700)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp(null, null)
    }
}
//
// @Preview("Dark Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun DarkPreview() {
//    MyTheme(darkTheme = true) {
//        MyApp(null)
//    }
// }
