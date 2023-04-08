package com.example.weatherjourney.features.weather.presentation.info

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.domain.ConnectivityObserver
import com.example.weatherjourney.features.weather.data.mapper.toAllWeather
import com.example.weatherjourney.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.features.weather.domain.mapper.coordinate
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.features.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.features.weather.domain.usecase.WeatherUseCases
import com.example.weatherjourney.locationpreferences.LocationPreferences
import com.example.weatherjourney.presentation.ViewModeWithMessageAndLoading
import com.example.weatherjourney.presentation.WeatherDestinations
import com.example.weatherjourney.util.Async
import com.example.weatherjourney.util.LocationException
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.util.WhileUiSubscribed
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
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherInfoViewModel"

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases,
    private val appPreferences: AppPreferences,
    connectivityObserver: ConnectivityObserver
) : ViewModeWithMessageAndLoading(connectivityObserver) {

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    private var _appRoute = WeatherDestinations.INFO_ROUTE
    val appRoute get() = _appRoute

    private val _units = combine(
        appPreferences.temperatureUnitFlow,
        appPreferences.windSpeedUnitFlow,
        appPreferences.pressureUnitFlow,
        appPreferences.timeFormatUnitFlow
    ) { tUnit, wpUnit, psUnit, tfUnit ->
        AllUnit(tUnit, wpUnit, psUnit, tfUnit)
    }.map {
        it.also { Log.d(TAG, "Units flow collected: $it") }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    private val _lastLocation = appPreferences.locationPreferencesFlow
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

    fun onAppFirstTimeStart(isLocationPermissionGranted: Boolean) {
        Log.d(TAG, "Location permission: $isLocationPermissionGranted")
        if (!isLocationPermissionGranted) {
            onNavigateToSearch()
        } else {
            onRefresh()
        }
    }

    fun onAppStart() {
        onRefresh()
    }

    suspend fun onFirstTimeCheck(): Boolean {
        if (appPreferences.getIsFirstTime()) {
            viewModelScope.launch { appPreferences.saveIsFirstTimeIntoFalse() }
            return true
        }

        return false
    }

    override fun onRefresh() = runSuspend({
        _lastLocation.filterNotNull().first().let {
            val weather = handleWeatherResult(
                weatherUseCases.getAllWeather(it.coordinate, it.timeZone),
                it.cityAddress,
                it.timeZone
            )

            _weatherAsync.value = weather
            Log.d(TAG, "WeatherAsync flow collected: $weather")
        }
    })

    fun onNavigateToSearch() {
        _appRoute = WeatherDestinations.SEARCH_ROUTE
        _isInitializing.value = false
    }

    fun onNavigateFromSearch(cityAddress: String, coordinate: Coordinate, timeZone: String) {
        Log.d(TAG, "onNavigateFromSearch($cityAddress, $coordinate, $timeZone) called")
        _isLoading.value = true

        viewModelScope.launch {
            if (locationUseCases.shouldSaveLocation(coordinate)) {
                _userMessage.value = UserMessage.AddingLocation
            }

            appPreferences.updateLocation(cityAddress, coordinate, timeZone)

            _lastLocation.filterNotNull()
                .first { it.coordinate == coordinate }
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
        when (val validateResult = locationUseCases.validateCurrentLocation(location)) {
            is Result.Success -> _isCurrentLocation.value = validateResult.data
            is Result.Error -> {
                if (validateResult.exception is LocationException) {
                    onNavigateToSearch()
                } else {
                    handleErrorResult(validateResult)
                }
            }
        }

        return location.takeUnless { it == LocationPreferences.getDefaultInstance() }
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
