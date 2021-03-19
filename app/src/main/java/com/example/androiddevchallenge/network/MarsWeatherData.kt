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

//    val jsonObject = JSONObject(jsonString)
//
//    val sols = jsonObject.getJSONArray("sol_keys")
//    val result = List<MarsWeatherData>(sols.length()) {
//        val sol = sols.getString(it)
//        parseSolWeatherData(jsonObject.getJSONObject(sol), sol.toInt())
//    }
//    return result
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
        if (at == null) null else parseSensorData(at),
        if (hws == null) null else parseSensorData(hws),
        if (pre == null) null else parseSensorData(pre),

        season,
        start,
        end
    )
}
