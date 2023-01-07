package com.example.weather.ui.screens

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.R
import com.example.weather.data.LocationRepository
import com.example.weather.data.PreferenceRepository
import com.example.weather.data.WeatherRepository
import com.example.weather.model.geocoding.Coordinate
import com.example.weather.model.utils.Result.Error
import com.example.weather.model.utils.Result.Success
import com.example.weather.model.weather.AllWeather
import com.example.weather.model.weather.CurrentWeather
import com.example.weather.model.weather.DailyWeather
import com.example.weather.utils.WhileUiSubscribed
import com.example.weather.utils.isValid
import com.example.weather.utils.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.net.UnknownHostException
import java.util.concurrent.ExecutionException
import javax.inject.Inject

/**
 * UiState for Weather Home screen.
 */
data class WeatherUiState(
    val weatherState: WeatherState = WeatherState(),
    val isLoading: Boolean = false,
    @StringRes val userMessage: Int? = null
)

data class WeatherState(
    val city: String = "",
    val current: CurrentWeather? = null,
    val listDaily: List<DailyWeather> = emptyList(),
    val shouldFetchCurrentLocationWeather: Boolean = false
)

private const val TAG = "WeatherViewModel"

/**
 * ViewModel for Weather Home screen.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<WeatherUiState> =
        combine(_weatherState, _isLoading, _userMessage) { weatherState, isLoading, message ->
            Log.d(TAG, isLoading.toString())
            WeatherUiState(weatherState, isLoading, message)
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = WeatherUiState()
        )

    fun updateCity(value: String) {
        _weatherState.update { _weatherState.value.copy(city = value) }
    }

    fun updateShouldFetchCurrentLocationWeather(value: Boolean) {
        _weatherState.update { _weatherState.value.copy(shouldFetchCurrentLocationWeather = value) }
    }

    suspend fun tryFetchLastResultWeather(): Boolean {
        val coordinate = preferenceRepository.fetchInitialPreferences()
        return if (coordinate.isValid()) {
            getAndUpdateCityByCoordinate(coordinate)
            fetchAllWeather(coordinate)
            true
        } else {
            false
        }
    }

    /**
     * Get All Weather by send Repository a CityName.
     */
    fun getAllWeather(city: String) {
        Log.d(TAG, "getALlWeather() called")
        _isLoading.value = true
        viewModelScope.launch {
            when (val coordinate = locationRepository.getCoordinateByCity(city)) {
                is Success -> fetchAllWeather(coordinate.data)
                is Error -> showSnackbarMessage(handleError(coordinate))
            }
        }
    }

    /**
     * Get All Weather by send Repository the Current Location received from Repository.
     */
    fun fetchLocationAllWeather() {
        Log.d(TAG, "fetchLocationAllWeather() called")
        _isLoading.value = true
        viewModelScope.launch {
            when (val coordinate = locationRepository.getCurrentCoordinate()) {
                is Success -> {
                    getAndUpdateCityByCoordinate(coordinate.data)
                    fetchAllWeather(coordinate.data)
                }
                is Error -> showSnackbarMessage(handleError(coordinate))
            }
        }
    }

    private fun fetchAllWeather(coordinate: Coordinate) {
        viewModelScope.launch {
            when (val weather = weatherRepository.fetchAllWeather(coordinate)) {
                is Success -> {
                    updateWeatherState(weather.data)
                    updateCachedCoordinate(coordinate)
                }
                is Error -> showSnackbarMessage(handleError(weather))
            }
        }
    }

    private fun getAndUpdateCityByCoordinate(coordinate: Coordinate) {
        viewModelScope.launch {
            when (val city = locationRepository.getCityByCoordinate(coordinate)) {
                is Success -> updateCity(city.data)
                is Error -> showSnackbarMessage(handleError(city))
            }
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
        _isLoading.value = false
    }

    private fun updateWeatherState(weather: AllWeather) {
        val timezoneOffset = weather.timezoneOffset
        _weatherState.update {
            _weatherState.value.copy(
                current = weather.current.toUiModel(timezoneOffset),
                listDaily = weather.daily.map { dailyItem -> dailyItem.toUiModel(timezoneOffset) }
            )
        }
        _isLoading.value = false
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    private fun handleError(result: Error): Int = when (result.exception) {
        is UnknownHostException -> R.string.no_internet_error
        is SerializationException -> R.string.malformed_json_error
        is HttpException -> R.string.empty_location_error
        is NoSuchElementException -> R.string.invalid_location_error
        is ExecutionException -> R.string.location_permission_error
        else -> R.string.unknown_error
    }

    private fun updateCachedCoordinate(coordinate: Coordinate) {
        viewModelScope.launch {
            preferenceRepository.saveCoordinate(coordinate)
        }
    }
}
