package com.example.weatherjourney.features.weather.presentation.info

import android.util.Log
import androidx.annotation.RestrictTo
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.domain.ConnectivityObserver
import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.data.mapper.toAllWeather
import com.example.weatherjourney.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.features.weather.domain.mapper.coordinate
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
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
import com.example.weatherjourney.util.isNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
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
    private val locationRepository: LocationRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModeWithMessageAndLoading(connectivityObserver) {

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    private var _appRoute = WeatherDestinations.INFO_ROUTE
    val appRoute get() = _appRoute

    private val _units = combine(
        appPreferences.temperatureUnitFlow,
        appPreferences.windSpeedUnitFlow,
        appPreferences.pressureUnitFlow,
        appPreferences.timeFormatUnitFlow,
    ) { tUnit, wpUnit, psUnit, tfUnit ->
        AllUnit(tUnit, wpUnit, psUnit, tfUnit)
    }.map {
        it.also { Log.d(TAG, "Units flow collected: $it") }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null,
    )

    private val _lastLocation = appPreferences.locationPreferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null,
    )

    private val _weatherAsync: MutableStateFlow<Async<AllWeather>> = MutableStateFlow(Async.Loading)
    private val _cityAddress = MutableStateFlow("")

    val uiState: StateFlow<WeatherInfoUiState> = combine(
        isLoading,
        userMessage,
        _weatherAsync,
        _units,
        _cityAddress,
    ) { isLoading, userMessage, weatherAsync, units, cityAddress ->
        when (weatherAsync) {
            Async.Loading -> WeatherInfoUiState(isLoading = true)
            is Async.Success -> {
                _isInitializing.value = false
                WeatherInfoUiState(
                    allUnit = units,
                    isLoading = isLoading,
                    userMessage = userMessage,
                    allWeather = weatherAsync.data.let { weatherUseCases.convertUnit(it, units) },
                    isCurrentLocation = _lastLocation.value?.isCurrentLocation ?: false,
                    cityAddress = cityAddress,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = WeatherInfoUiState(isLoading = true),
    )

    fun onFirstTimeLocationPermissionResult(isLocationPermissionGranted: Boolean) {
        Log.d(TAG, "Location permission: $isLocationPermissionGranted")
        if (isLocationPermissionGranted) {
            onRefresh()
        } else {
            navigateToSearch()
        }
    }

    suspend fun isFirstTimeRunApp(): Boolean = appPreferences.isFirstTimeRunApp()

    override fun onRefresh() = runSuspend({
        viewModelScope.launch {
            _lastLocation.filterNotNull().collect {
                handleLocation(it).let { location ->
                    Log.d(TAG, "Location flow collected: $location")
                    if (location != null) {
                        updateCityAddressAndWeather(location)
                        cancel()
                    }
                }
            }
        }
    })

    fun onNavigateFromSearch(coordinate: Coordinate) {
        Log.d(TAG, "onNavigateFromSearch($coordinate)")
        viewModelScope.launch {
            onRefresh()
            if (locationRepository.getLocation(coordinate)
                    .isNull() && _lastLocation.value?.isCurrentLocation != true
            ) {
                userMessage.value = UserMessage.AddingLocation
            }
        }
    }

    fun onSaveInfo(countryCode: String) {
        Log.d(TAG, "onSaveInfo($countryCode) called")
        viewModelScope.launch {
            _lastLocation.value?.let {
                locationRepository.saveLocation(
                    LocationEntity(
                        cityAddress = it.cityAddress,
                        latitude = it.coordinate.latitude,
                        longitude = it.coordinate.longitude,
                        timeZone = it.timeZone,
                        isCurrentLocation =
                        if (locationRepository.getCurrentLocation() != null) {
                            false
                        } else {
                            _lastLocation.value?.isCurrentLocation ?: false
                        },
                        countryCode = countryCode,
                    ),
                )
            }
            showSnackbarMessage(R.string.location_saved)
        }
    }

    private fun navigateToSearch() {
        _appRoute = WeatherDestinations.SEARCH_ROUTE
        _isInitializing.value = false
    }

    private suspend fun handleLocation(location: LocationPreferences): LocationPreferences? {
        if (!isFirstTimeRunApp() && location == LocationPreferences.getDefaultInstance()) {
            navigateToSearch()
            return null
        }

        return when (val isValidateSuccess = locationUseCases.validateCurrentLocation(location)) {
            is Result.Success -> if (isValidateSuccess.data) location else null
            is Result.Error -> {
                if (isValidateSuccess.exception is LocationException) {
                    navigateToSearch()
                } else {
                    _weatherAsync.value = Async.Success(AllWeather())
                    _isInitializing.value = false
                    handleErrorResult(isValidateSuccess)
                }
                null
            }
        }
    }

    private fun handleWeatherResult(
        result: Result<AllWeatherDto>,
        timeZone: String,
    ): Async<AllWeather> {
        return when (result) {
            is Result.Success -> Async.Success(result.data.toAllWeather(timeZone))
            is Result.Error -> {
                handleErrorResult(result)
                // If weather state is still Loading mean its first init and
                // has error, return empty AllWeather
                if (_weatherAsync.value is Async.Loading) {
                    Async.Success(AllWeather())
                } // If weather state already had some valid values so just keep it
                else {
                    _weatherAsync.value
                }
            }
        }
    }

    private suspend fun updateCityAddressAndWeather(location: LocationPreferences) {
        // Update city address
        _cityAddress.value = location.cityAddress
        // Update weather
        val weather = handleWeatherResult(
            weatherUseCases.getAllWeather(location.coordinate, location.timeZone),
            location.timeZone,
        )
        _weatherAsync.value = weather
        Log.d(TAG, "WeatherAsync flow collected: $weather")
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            appPreferences.setFirstTimeRunAppToFalse()
        }
    }
}
