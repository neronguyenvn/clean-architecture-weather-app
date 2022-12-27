package com.example.weather.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.WeatherRepository
import com.example.weather.utils.DateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class WeatherUiState(
    val city: String = "",
    val date: String = "",
    val temp: Int = 0,
    val weather: String = ""
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    @Inject
    lateinit var dateFormat: DateFormat

    fun updateCity(value: String) {
        _uiState.update {
            it.copy(city = value)
        }
    }

    fun getWeather(city: String) {
        viewModelScope.launch {
            val weather = repository.getWeather(city).current
            _uiState.update {
                it.copy(
                    date = dateFormat.convertUnixTimeToDate(weather.dt),
                    temp = weather.temp.roundToInt(),
                    weather = weather.weather.first().main
                )
            }
        }
    }
}
