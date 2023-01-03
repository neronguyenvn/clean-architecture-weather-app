package com.example.weather.ui.screens

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.R
import com.example.weather.data.LocationRepository
import com.example.weather.data.WeatherRepository
import com.example.weather.model.weather.AllWeather
import com.example.weather.model.weather.CurrentWeather
import com.example.weather.model.weather.DailyWeather
import com.example.weather.utils.DATE_PATTERN
import com.example.weather.utils.Result.Error
import com.example.weather.utils.Result.Success
import com.example.weather.utils.toDateString
import com.example.weather.utils.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "WeatherViewModel"

/**
 * UiState for Weather Home screen.
 */
data class WeatherUiState(
    val city: String = "",
    val date: String = "",
    val temp: String = "",
    val weather: String = "",
    val listDaily: List<DailyWeather> = emptyList(),
    @DrawableRes val bgImg: Int = R.drawable.day_rain,
    val shouldDoLocationAction: Boolean = true,
    val isLoading: Boolean = false,
    val error: String = ""
)

/**
 * ViewModel for Weather Home screen.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Single update state method exposed for Ui components.
     */
    fun updateUiState(state: WeatherUiState) {
        _uiState.update { state }
    }

    /**
     * Get All Weather by send Repository a CityName.
     */
    fun getAllWeather(city: String) {
        Log.d(TAG, "getAllWeather() called")
        viewModelScope.launch {
            _uiState.update { uiState.value.copy(isLoading = true) }
            when (val coordinate = locationRepository.getCoordinateByCity(city)) {
                is Error -> {
                    // Delay 1 second to make the reload more real
                    delay(1000)
                    updateErrorState(coordinate.exception)
                }
                is Success -> when (val weather = weatherRepository.getWeather(coordinate.data)) {
                    is Error -> updateErrorState(weather.exception)
                    is Success -> updateWeatherState(weather.data)
                }
            }
            _uiState.update { uiState.value.copy(isLoading = false) }
        }
    }

    /**
     * Get All Weather by send Repository the Current Location received from Repository.
     */
    fun getCurrentCoordinateAllWeather() {
        Log.d(TAG, "getCurrentCoordinateAllWeather() called")
        val handler = CoroutineExceptionHandler { _, ex ->
            updateErrorState(ex as Exception, true)
        }
        viewModelScope.launch(handler) {
            _uiState.update { uiState.value.copy(isLoading = true) }
            val coordinate = locationRepository.getCurrentCoordinate()
            val job = launch {
                when (val city = locationRepository.getCityByCoordinate(coordinate)) {
                    is Error -> throw city.exception
                    is Success -> _uiState.update { uiState.value.copy(city = city.data) }
                }
            }
            when (val weather = weatherRepository.getWeather(coordinate)) {
                is Error -> throw weather.exception
                is Success -> updateWeatherState(weather.data)
            }
            job.join()
            _uiState.update { uiState.value.copy(isLoading = false) }
        }
    }

    private fun updateErrorState(ex: Exception, shouldStopLoading: Boolean = false) {
        _uiState.update {
            uiState.value.copy(
                error = ex.message ?: "Something went wrong",
                isLoading = shouldStopLoading
            )
        }
    }

    private fun updateWeatherState(allWeather: AllWeather) {
        val current = allWeather.current
        _uiState.update {
            it.copy(
                date = current.timestamp.toDateString(allWeather.timezoneOffset, DATE_PATTERN),
                temp = current.temp.roundToInt().toString(),
                weather = current.weatherItem.first().weatherDescription,
                listDaily = allWeather.daily.map { dailyItem ->
                    dailyItem.toUiModel(allWeather.timezoneOffset)
                },
                bgImg = selectBackgroundImage(current)
            )
        }
    }

    private fun selectBackgroundImage(current: CurrentWeather): Int {
        current.apply {
            val weatherDescription = weatherItem.first().weatherDescription

            return if (sunriseTimestamp != null && sunsetTimestamp != null) {
                if (timestamp in sunriseTimestamp..sunsetTimestamp) {
                    when (weatherDescription) {
                        "Thunderstorm", "Drizzle", "Rain" -> R.drawable.day_rain
                        "Snow" -> R.drawable.day_snow
                        "Clear" -> R.drawable.day_clearsky
                        "Cloud" -> R.drawable.day_cloudy
                        else -> R.drawable.day_other_atmosphere
                    }
                } else {
                    when (weatherDescription) {
                        "Thunderstorm", "Drizzle", "Rain" -> R.drawable.night_rain
                        "Snow" -> R.drawable.night_snow
                        "Clear" -> R.drawable.night_clearsky
                        "Clouds" -> R.drawable.night_cloudy
                        else -> R.drawable.night_other_atmosphere
                    }
                }
            } else {
                when (weatherDescription) {
                    "Clouds" -> R.drawable.night_cloudy
                    "Snow" -> R.drawable.night_snow
                    else -> R.drawable.night_snow
                }
            }
        }
    }
}
