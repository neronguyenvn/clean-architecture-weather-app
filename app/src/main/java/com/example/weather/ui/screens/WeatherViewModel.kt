package com.example.weather.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.LocationRepository
import com.example.weather.data.WeatherRepository
import com.example.weather.model.weather.AllWeather
import com.example.weather.model.weather.CurrentWeather
import com.example.weather.model.weather.DailyWeather
import com.example.weather.utils.REAL_LOADING_DELAY_TIME
import com.example.weather.utils.Result.Error
import com.example.weather.utils.Result.Success
import com.example.weather.utils.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherViewModel"

/**
 * UiState for Weather Home screen.
 */
data class WeatherUiState(
    val city: String = "",
    val current: CurrentWeather? = null,
    val listDaily: List<DailyWeather> = emptyList(),
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
    val uiState: StateFlow<WeatherUiState> = _uiState

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
        _uiState.update { uiState.value.copy(isLoading = true) }

        viewModelScope.launch {
            when (val coordinate = locationRepository.getCoordinateByCity(city)) {
                is Error -> {
                    // Delay 1 second to make the reload more real
                    delay(REAL_LOADING_DELAY_TIME)
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
        _uiState.update { uiState.value.copy(isLoading = true) }
        viewModelScope.launch(handler) {
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

    private fun updateWeatherState(weather: AllWeather) {
        val timezoneOffset = weather.timezoneOffset
        _uiState.update {
            uiState.value.copy(
                current = weather.current.toUiModel(timezoneOffset),
                listDaily = weather.daily.map { dailyItem -> dailyItem.toUiModel(timezoneOffset) }
            )
        }
    }
}
