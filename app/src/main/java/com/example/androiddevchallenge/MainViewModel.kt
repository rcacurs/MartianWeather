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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.network.MarsWeatherApi
import com.example.androiddevchallenge.network.MarsWeatherData
import com.example.androiddevchallenge.network.parseWeatherDataList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"
class MainViewModel : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _marsWeatherData = MutableLiveData<List<MarsWeatherData>>(mutableListOf<MarsWeatherData>())
    val marsWeatherData: LiveData<List<MarsWeatherData>>
        get() = _marsWeatherData

    private val _connectionErrorFlag = MutableLiveData<Boolean> (false)
    val connectionErrorFlag: LiveData<Boolean>
        get() = _connectionErrorFlag

    private val _connecting = MutableLiveData<Boolean>(false)
    val connecting: LiveData<Boolean>
        get() = _connecting

    init {
        refreshWeatherData()
    }

    fun refreshWeatherData() {
        _connectionErrorFlag.value = false
        Log.d(TAG, "Refreshing Mars Weather Data...")
        uiScope.launch {
            _connecting.value = true
            try {
                val data = MarsWeatherApi.retrofitService.getMarsWeatherData()
                _marsWeatherData.value = parseWeatherDataList(data)
            } catch (exception: Exception) {
                _connectionErrorFlag.value = true
                Log.d(TAG, "Connection issue")
            }
            Log.d(TAG, "Got Weather data!")
            _connecting.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
