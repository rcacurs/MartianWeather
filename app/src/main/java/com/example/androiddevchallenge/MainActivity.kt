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
import androidx.compose.foundation.Image
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.androiddevchallenge.ui.theme.transparentBlack
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = transparentBlack.toArgb()

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
@Composable
fun MyApp(context: Context?, viewModel: MainViewModel) {
    // Surface(color = MaterialTheme.colors.background) {
    val weatherData: List<MarsWeatherData> by (viewModel.marsWeatherData)
        .observeAsState(mutableListOf<MarsWeatherData>())
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painterResource(R.drawable.mars_crop_1),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column {
            Divider(
                modifier = Modifier.height(24.dp),
                color = Color(0x00000000)
            )
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 56.dp)
            ) {

                for (item in weatherData) {
                    WeatherCard(context, item)
                }
            }
        }
    }
}

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
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateBlock(context, weatherData, modifier = Modifier.weight(1f))
            Divider(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .height(100.dp)
                    .width(1.dp),
                color = MaterialTheme.colors.onSurface
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                WeatherDataField("temperature", weatherData.atmosphericTemperature, "°F")
                WeatherDataField("wind speed", weatherData.horizontalWindSpeed, "m/s")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDataField("atm. pressure", weatherData.atmosphericPressure, "Pa")
                WeatherDataField("wind direction", null, "") // TODO implement weather direction data in data class
            }
        }
    }
}

@Composable
fun DateBlock(
    context: Context?,
    weatherData: MarsWeatherData,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("Sol ${weatherData.sol}", style = MaterialTheme.typography.h4)
        val date = DateUtils.formatDateTime(context, weatherData.firstDate.time, DateUtils.FORMAT_NO_YEAR)
        Text(date, style = MaterialTheme.typography.h5)
    }
}

@Composable
fun WeatherDataField(label: String, value: SensorData?, unit: String) {
    ConstraintLayout {
        val (valueField, labelField, unitField) = createRefs()
        val displayValueAverage = if (value?.average == null) {
            "N/A"
        } else {
            "%.1f".format(value?.average)
        }
        Text(
            text = displayValueAverage,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.constrainAs(valueField) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
            }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.body2.copy(textAlign = TextAlign.Center),
            modifier = Modifier.constrainAs(labelField) {
                top.linkTo(valueField.bottom)
                start.linkTo(valueField.start)
                end.linkTo(valueField.end)
            }
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.body2,
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
