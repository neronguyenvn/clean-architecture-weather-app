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
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherInfoViewModel"

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases,
    private val preferences: PreferenceRepository
) : ViewModel() {

    var uiState by mutableStateOf(WeatherInfoUiState())
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    init {
        Log.d(TAG, "$TAG init")
    }

    fun onEvent(event: WeatherInfoEvent) {
        when (event) {
            is WeatherInfoEvent.OnAppInit -> viewModelScope.launch {
                val lastCoordinate = preferences.getLastCoordinate()
                val lastTimeZone = preferences.getLastTimeZone()
                val lastCityAddress = preferences.getLastCityAddress()

                when {
                    locationUseCases.validateLastInfo(
                        lastCoordinate,
                        lastTimeZone,
                        lastCityAddress
                    ) -> runSuspend(
                        launch { uiState = uiState.copy(cityAddress = lastCityAddress) },
                        getAndUpdateWeather(lastCoordinate, lastTimeZone, true)
                    )

                    event.isLocationPermissionGranted -> viewModelScope.launch {
                        val coordinate = locationUseCases.getCurrentCoordinate() as Result.Success
                        runSuspend(
                            viewModelScope.launch {
                                getAndUpdateCity(coordinate.data).join()
                                getAndUpdateWeather(
                                    coordinate.data,
                                    preferences.getLastTimeZone(),
                                    true
                                )
                            }
                        )
                    }

                    else -> {
                        _uiEvent.emit(UiEvent.StartWithSearchRoute)
                        _isInitializing.value = false
                    }
                }
            }

            is WeatherInfoEvent.OnRefresh -> viewModelScope.launch {
                runSuspend(
                    getAndUpdateWeather(
                        preferences.getLastCoordinate(),
                        preferences.getLastTimeZone(),
                        true
                    ),
                    launch {
                        delay(1500)
                    }
                )
            }

            is WeatherInfoEvent.OnFetchWeatherFromSearch -> {
                viewModelScope.launch {
                    uiState = uiState.copy(cityAddress = event.cityAddress)
                    runSuspend(getAndUpdateWeather(event.coordinate, event.timeZone, false))
                    if (locationUseCases.shouldSaveLocation(event.coordinate, event.cityAddress)) {
                        _uiEvent.emit(
                            UiEvent.ShowSnackbar(
                                message = UiText.StringResource(R.string.add_this_location),
                                actionLabel = R.string.add
                            )
                        )
                    }
                    preferences.saveCityAddress(event.cityAddress)
                }
            }

            is WeatherInfoEvent.OnCacheInfo -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowSnackbar(UiText.StringResource(R.string.location_saved)))
                    locationUseCases.saveLocation(
                        uiState.cityAddress,
                        preferences.getLastCoordinate(),
                        preferences.getLastTimeZone()
                    )
                }
            }
        }
    }

    private fun getAndUpdateCity(coordinate: Coordinate): Job {
        Log.d(TAG, "getAndUpdateCityByCoordinate() called")

        return viewModelScope.launch {
            when (val address = locationUseCases.getCityAddressAndSaveLocation(coordinate)) {
                is Result.Success -> uiState = uiState.copy(cityAddress = address.data)

                is Result.Error -> handleError(address)
            }
        }
    }

    private fun getAndUpdateWeather(
        coordinate: Coordinate,
        timeZone: String,
        forceCache: Boolean
    ): Job {
        Log.d(TAG, "getAndUpdateWeather() called")

        return viewModelScope.launch {
            when (val weather = weatherUseCases.getAllWeatherAndCacheLastInfo(coordinate, timeZone, forceCache)) {
                is Result.Success -> {
                    uiState = uiState.copy(
                        weatherState = WeatherState(
                            current = weather.data.toCurrentWeather(timeZone),
                            listDaily = weather.data.daily.toDailyWeather(timeZone),
                            listHourly = weather.data.hourly.toHourlyWeather(timeZone)
                        )
                    )
                }

                is Result.Error -> handleError(weather)
            }
        }
    }

    private suspend fun runSuspend(vararg jobs: Job) {
        uiState = uiState.copy(isLoading = true)
        jobs.forEach { it.join() }
        uiState = uiState.copy(isLoading = false)
        _isInitializing.value = false
    }

    private suspend fun handleError(error: Result.Error) {
        val message = error.toString()
        Log.e(TAG, message)
        _uiEvent.emit(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
    }
}
