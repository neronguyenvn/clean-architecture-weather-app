package com.example.weatherjourney.weather.presentation.info

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.weather.data.mapper.toCurrentWeather
import com.example.weatherjourney.weather.data.mapper.toDailyWeather
import com.example.weatherjourney.weather.data.mapper.toHourlyWeather
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.data.source.remote.dto.AllWeather
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.weather.util.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherInfoViewModel"

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    var uiState by mutableStateOf(WeatherInfoUiState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val isLastWeatherInfoLoading = MutableStateFlow(true)

    init {
        Log.d(TAG, "$TAG init")
    }

    fun onEvent(event: WeatherInfoEvent) {
        when (event) {
            is WeatherInfoEvent.OnRefresh -> viewModelScope.launch {
                fetchWeather(preferenceRepository.getLastCoordinate())
            }

            is WeatherInfoEvent.OnAppInit -> initApp(event.isLocationPermissionGranted)
            is WeatherInfoEvent.OnFetchWeatherFromSearch -> {
                viewModelScope.launch {
                    event.apply {
                        uiState = uiState.copy(city = city)
                        fetchWeather(coordinate).join()
                        if (!checkIsLocationSaved(coordinate)) {
                            _uiEvent.send(
                                UiEvent.ShowSnackbar(
                                    message = UiText.StringResource(R.string.add_this_location),
                                    actionLabel = R.string.add
                                )
                            )
                            _uiEvent.send(
                                UiEvent.ShowSnackbar(
                                    message = UiText.StringResource(R.string.add_this_location),
                                    actionLabel = R.string.add
                                )
                            )
                        }
                    }
                }
            }

            is WeatherInfoEvent.OnCacheInfo -> saveLocation()
        }
    }

    private fun saveLocation() {
        viewModelScope.launch {
            val coordinate = preferenceRepository.getLastCoordinate()
            locationRepository.saveLocation(uiState.city, coordinate)
            _uiEvent.send(UiEvent.ShowSnackbar(UiText.StringResource(R.string.location_saved)))
            _uiEvent.send(UiEvent.ShowSnackbar(UiText.StringResource(R.string.location_saved)))
        }
    }

    private suspend fun checkIsLocationSaved(coordinate: Coordinate): Boolean {
        val result = viewModelScope.async {
            locationRepository.checkIsLocationSaved(coordinate)
        }
        return result.await()
    }

    private fun initApp(isLocationPermissionGranted: Boolean) {
        viewModelScope.launch {
            val lastCoordinate = preferenceRepository.getLastCoordinate()

            if (lastCoordinate.isValid()) {
                getAndUpdateCityByCoordinate(lastCoordinate)
                fetchWeather(lastCoordinate)
            } else {
                if (isLocationPermissionGranted) {
                    fetchCurrentLocationWeather()
                } else {
                    viewModelScope.launch {
                        _uiEvent.send(UiEvent.StartWithSearchRoute)
                        isLastWeatherInfoLoading.value = false
                    }
                }
            }
        }
    }

    private fun fetchCurrentLocationWeather() {
        Log.d(TAG, "fetchLocationAllWeather() called")
        viewModelScope.launch {
            when (val coordinate = locationRepository.getCurrentCoordinate()) {
                is Result.Success -> {
                    getAndUpdateCityByCoordinate(coordinate.data)
                    fetchWeather(coordinate.data)
                }

                is Result.Error -> {
                    val message = coordinate.toString()
                    _uiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                }
            }
            isLastWeatherInfoLoading.update { false }
        }
    }

    private fun getAndUpdateCityByCoordinate(coordinate: Coordinate) {
        viewModelScope.launch {
            when (val city = locationRepository.getCityByCoordinate(coordinate)) {
                is Result.Success -> uiState = uiState.copy(city = city.data)
                is Result.Error -> {
                    val message = city.toString()
                    _uiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                }
            }
        }
    }

    private fun fetchWeather(coordinate: Coordinate): Job {
        Log.d(TAG, "fetchWeather() called")
        return viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            when (val weather = weatherRepository.fetchAllWeather(coordinate)) {
                is Result.Success -> {
                    updateWeatherState(weather.data)
                    updateCachedCoordinate(coordinate)
                }

                is Result.Error -> {
                    val message = weather.toString()
                    _uiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                }
            }
            isLastWeatherInfoLoading.update { false }
        }
    }

    private fun updateWeatherState(weather: AllWeather) {
        val timezoneOffset = weather.timezoneOffset
        uiState =
            uiState.copy(
                weatherState = WeatherInfo(
                    current = weather.current.toCurrentWeather(
                        timezoneOffset,
                        weather.hourly.first().precipitationChance
                    ),
                    listDaily = weather.daily.map { it.toDailyWeather(timezoneOffset) },
                    listHourly = weather.hourly.map { it.toHourlyWeather(timezoneOffset) }
                ),
                isLoading = false
            )
    }

    private fun updateCachedCoordinate(coordinate: Coordinate) {
        viewModelScope.launch {
            preferenceRepository.saveCoordinate(coordinate.toUnifiedCoordinate())
        }
    }
}
