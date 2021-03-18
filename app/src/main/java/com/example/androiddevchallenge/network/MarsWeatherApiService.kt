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

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URI = "https://api.nasa.gov/"
private const val API_KEY = "KF6sns8q7AKha5nJxEyduG5p31u3uPau52KnaLV8"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URI)
    .build()

interface MarsWeatherApiService {
    @GET("insight_weather/?api_key=$API_KEY&feedtype=json&ver=1.0")
    suspend fun getMarsWeatherData(): String
}

object MarsWeatherApi {
    val retrofitService: MarsWeatherApiService by lazy {
        retrofit.create(MarsWeatherApiService::class.java)
    }
}
