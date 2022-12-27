package com.example.weather.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    var searchText by mutableStateOf("")
        private set

    var weather by mutableStateOf("Rain")
        private set

    fun updateSearchText(value: String) {
        searchText = value
    }

    fun getWeather(city: String) {
        viewModelScope.launch {
            weather = repository.getWeather(city)
        }
    }
}
