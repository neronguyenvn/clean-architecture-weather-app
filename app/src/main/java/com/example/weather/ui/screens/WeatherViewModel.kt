package com.example.weather.ui.screens

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.R
import com.example.weather.data.WeatherRepository
import com.example.weather.model.weather.CurrentWeather
import com.example.weather.model.weather.DailyWeather
import com.example.weather.model.weather.Weather
import com.example.weather.model.weather.asModel
import com.example.weather.utils.DATE_PATTERN
import com.example.weather.utils.toDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class WeatherUiState(
    val city: String = "",
    val date: String = "",
    val temp: Int = 0,
    val weather: String = "",
    val listDaily: List<DailyWeather> = emptyList(),
    @DrawableRes val bgImg: Int = R.drawable.day_rain
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState = _uiState.asStateFlow()

    fun updateCity(value: String) {
        _uiState.update { it.copy(city = value) }
    }

    fun getWeather(city: String) {
        viewModelScope.launch {
            val weather = repository.getWeather(city)
            updateWeatherState(weather)
        }
    }

    fun getCurrentLocationWeather() {
        viewModelScope.launch {
            repository.currentCityWeather.collect {
                updateWeatherState(it)
            }
        }
    }

    private fun updateWeatherState(weather: Weather) {
        val current = weather.current
        _uiState.update {
            it.copy(
                date = current.timestamp.toDateString(DATE_PATTERN),
                temp = current.temp.roundToInt(),
                weather = current.weatherItem.first().main,
                listDaily = weather.daily.map { daily -> daily.asModel(current.timestamp) },
                bgImg = selectBackgroundImage(current)
            )
        }
    }

    private fun selectBackgroundImage(
        current: CurrentWeather
    ): Int {
        current.apply {
            return if (timestamp in sunriseTimestamp..sunsetTimestamp) {
                when (weatherItem.first().main) {
                    "Thunderstorm", "Drizzle", "Rain" -> R.drawable.day_rain
                    "Snow" -> R.drawable.day_snow
                    "Clear" -> R.drawable.day_clearsky
                    "Cloud" -> R.drawable.day_cloudy
                    else -> R.drawable.day_other_atmosphere
                }
            } else {
                when (weatherItem.first().main) {
                    "Thunderstorm", "Drizzle", "Rain" -> R.drawable.night_rain
                    "Snow" -> R.drawable.night_snow
                    "Clear" -> R.drawable.night_clearsky
                    "Clouds" -> R.drawable.night_cloudy
                    else -> R.drawable.night_other_atmosphere
                }
            }
        }
    }
}
