package com.example.weatherjourney.weather.presentation.info

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherejourney.LocationPreferences
import com.example.weatherjourney.R
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.presentation.BaseViewModel
import com.example.weatherjourney.presentation.WeatherDestinations
import com.example.weatherjourney.util.ActionLabel
import com.example.weatherjourney.util.Async
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.WhileUiSubscribed
import com.example.weatherjourney.weather.data.mapper.toAllWeather
import com.example.weatherjourney.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.weather.domain.mapper.toCoordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.weather.domain.repository.RefreshRepository
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherInfoViewModel"

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases,
    private val preferences: PreferenceRepository,
    refreshRepository: RefreshRepository
) : BaseViewModel(refreshRepository) {

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    private var _appRoute = WeatherDestinations.INFO_ROUTE
    val appRoute get() = _appRoute

    private val _temperatureUnit = preferences.temperatureUnitFlow
    private val _windSpeedUnit = preferences.windSpeedUnitFlow

    private val _units =
        _temperatureUnit.zip(_windSpeedUnit) { temperatureUnit, windSpeedUnit ->
            AllUnit(
                temperature = temperatureUnit,
                windSpeed = windSpeedUnit
            )
        }.map {
            it.also { Log.d(TAG, "Units flow collected: $it") }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _lastLocation = preferences.locationPreferencesFlow
        .map { location ->
            handleLocation(location).also { Log.d(TAG, "LastLocation flow collected: $it") }
        }.stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    private val _weatherAsync: MutableStateFlow<Async<AllWeather>> = MutableStateFlow(Async.Loading)
    private val _isCurrentLocation = MutableStateFlow(false)

    val uiState: StateFlow<WeatherInfoUiState> = combine(
        _isLoading,
        _userMessage,
        _weatherAsync,
        _units,
        _isCurrentLocation
    ) { isLoading, userMessage, weatherAsync, units, isCurrentLocation ->
        when (weatherAsync) {
            Async.Loading -> WeatherInfoUiState(isLoading = true)

            is Async.Success -> {
                _isInitializing.value = false
                WeatherInfoUiState(
                    allUnit = units,
                    isLoading = isLoading,
                    userMessage = userMessage,
                    allWeather = weatherAsync.data.let { weatherUseCases.convertUnit(it, units) },
                    isCurrentLocation = isCurrentLocation
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = WeatherInfoUiState(isLoading = true)
    )

    fun onActivityCreate(isLocationPermissionGranted: Boolean) {
        if (!isLocationPermissionGranted) {
            _appRoute = WeatherDestinations.SEARCH_ROUTE
            _isInitializing.value = false
        } else {
            onRefresh()
        }
    }

    override fun onRefresh() = onRefresh({
        _lastLocation.filterNotNull().first().let {
            val weather = handleWeatherResult(
                weatherUseCases.getAllWeather(it.coordinate.toCoordinate(), it.timeZone),
                it.cityAddress,
                it.timeZone
            )

            _weatherAsync.value = weather
            Log.d(TAG, "WeatherAsync flow collected: $weather")
        }
    })

    fun onNavigateFromSearch(cityAddress: String, coordinate: Coordinate, timeZone: String) {
        Log.d(TAG, "onNavigateFromSearch($cityAddress, $coordinate, $timeZone) called")
        _isLoading.value = true

        viewModelScope.launch {
            if (locationUseCases.shouldSaveLocation(coordinate)) {
                showSnackbarMessage(R.string.add_this_location, ActionLabel.ADD)
            }

            preferences.updateLocation(cityAddress, coordinate, timeZone)

            _lastLocation.filterNotNull()
                .first { it.cityAddress == cityAddress && it.timeZone == timeZone }
                .let {
                    onRefresh()
                    _isLoading.value = false
                }
        }
    }

    fun onSaveInfo(countryCode: String) {
        Log.d(TAG, "onSaveInfo($countryCode) called")
        viewModelScope.launch {
            _lastLocation.value?.let { locationUseCases.saveLocation(it, countryCode) }
            showSnackbarMessage(R.string.location_saved)
        }
    }

    private suspend fun handleLocation(location: LocationPreferences): LocationPreferences? {
        return when (location) {
            LocationPreferences.getDefaultInstance() -> {
                when (val result = locationUseCases.getAndSaveCurrentLocation()) {
                    is Result.Success -> _isCurrentLocation.value = true
                    is Result.Error -> handleErrorResult(result)
                }

                null
            }

            else -> {
                val isCurrentLocation =
                    locationUseCases.isCurrentLocation(location.coordinate.toCoordinate())
                Log.d(
                    TAG,
                    "${location.cityAddress} is ${if (!isCurrentLocation) "not" else ""} current location"
                )

                _isCurrentLocation.value = isCurrentLocation
                location
            }
        }
    }

    private fun handleWeatherResult(
        result: Result<AllWeatherDto>,
        cityAddress: String,
        timeZone: String
    ): Async<AllWeather> {
        return when (result) {
            is Result.Success -> Async.Success(result.data.toAllWeather(cityAddress, timeZone))
            is Result.Error -> {
                handleErrorResult(result)
                // Still can show city address cached without weather data
                Async.Success(AllWeather(cityAddress = cityAddress))
            }
        }
    }
}
