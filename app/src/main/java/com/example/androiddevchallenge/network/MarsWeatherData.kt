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
package com.example.androiddevchallenge.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") // used to parse date from the request

enum class Season {
    WINTER,
    SPRING,
    SUMMER,
    FALL,
    UNKNOWN
}

data class SensorData(
    val average: Double, // av
    val samples: Int, // ct
    val min: Double, // mn
    val max: Double // mx
)

data class MarsWeatherData(
    val sol: Int = 0, // Martian day id
    val atmosphericTemperature: SensorData?, // in Fahrenheit
    val horizontalWindSpeed: SensorData?, // in m/s
    val atmosphericPressure: SensorData?, // in Pa
    val season: Season,
    val firstDate: Date, // date of first measurement for period
    val lastDate: Date // date of last measurement for period
)

suspend fun parseWeatherDataList(jsonString: String): List<MarsWeatherData> {
    return withContext(Dispatchers.Default) {
        val jsonObject = JSONObject(jsonString)
        val sols = jsonObject.getJSONArray("sol_keys")

        // returns this List of MarsWeatherData objects
        val weatherData = List<MarsWeatherData>(sols.length()) {
            val sol = sols.getString(it)
            parseSolWeatherData(jsonObject.getJSONObject(sol), sol.toInt())
        }
        weatherData.sortedByDescending { item -> item.sol } // sort reversed
    }
}

fun parseSensorData(jsonObject: JSONObject): SensorData {
    return SensorData(
        jsonObject.getDouble("av"),
        jsonObject.getInt("ct"),
        jsonObject.getDouble("mn"),
        jsonObject.getDouble("mx")
    )
}

fun parseSolWeatherData(solJson: JSONObject, sol: Int): MarsWeatherData {
    Log.d("Parsing weather data", "for sol - $sol")
    val at = solJson.optJSONObject("AT")
    val hws = solJson.optJSONObject("HWS")
    val pre = solJson.optJSONObject("PRE")

    val start = df.parse(solJson.getString("First_UTC").replace("Z", "-0000"))
    val end = df.parse(solJson.getString("Last_UTC").replace("Z", "-0000"))

    val season = when (solJson.getString("Season")) {
        "winter" -> Season.WINTER
        "spring" -> Season.SPRING
        "summer" -> Season.SUMMER
        "fall" -> Season.FALL
        else -> Season.UNKNOWN
    }

    return MarsWeatherData(
        sol,
        if (at == null) generateFakeTemperature() else parseSensorData(at),
        if (hws == null) generateFakeWindSpeed() else parseSensorData(hws),
        if (pre == null) null else parseSensorData(pre),

        season,
        start,
        end
    )
}

// Just used for vizualization testing if weather data is not available from the probe
fun generateFakeTemperature(): SensorData {
    return SensorData(
        average = Random.nextDouble(-66.7, -58.3),
        samples = Random.nextInt(300784, 326642),
        max = Random.nextDouble(-16.7, 20.0),
        min = Random.nextDouble(-100.0, -75.0)
    )
}

fun generateFakeWindSpeed(): SensorData {
    return SensorData(
        average = Random.nextDouble(4.0, 7.0),
        samples = Random.nextInt(120000, 154146),
        max = Random.nextDouble(15.0, 30.0),
        min = Random.nextDouble(2.0, 3.0)
    )
}
