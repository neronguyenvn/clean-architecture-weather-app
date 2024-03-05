package com.example.weatherjourney.features.weather.info

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.app.navigation.WtnDestinations
import com.example.weatherjourney.core.common.util.Async
import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.common.util.ViewModeWithMessageAndLoading
import com.example.weatherjourney.core.common.util.WhileUiSubscribed
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.NetworkMonitor
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.datastore.model.toAllUnit
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.core.domain.ValidateCurrentLocationUseCase
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.weather.AllWeather
import com.example.weatherjourney.core.network.model.NetworkAllWeather
import com.example.weatherjourney.core.network.model.toAllWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherInfoViewModel"

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val userDataRepository: UserDataRepository,
    private val weatherRepository: WeatherRepository,
    private val convertUnitUseCase: ConvertUnitUseCase,
    private val validateCurrentLocationUseCase: ValidateCurrentLocationUseCase,
    networkMonitor: NetworkMonitor,
) : ViewModeWithMessageAndLoading(networkMonitor) {

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    private var _appRoute = WtnDestinations.INFO_ROUTE
    val appRoute get() = _appRoute

    private val _units = userDataRepository.userData.map { userData ->
        userData.toAllUnit()
    }.map {
        it.also { Log.d(TAG, "Units flow collected: $it") }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
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
                    allWeather = convertUnitUseCase(weatherAsync.data, units),
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

    override fun onRefresh() = runSuspend({
        viewModelScope.launch {
/*            _lastLocation.filterNotNull().collect {
                handleLocation(it).let { location ->
                    Log.d(TAG, "Location flow collected: $location")
                    if (location != null) {
                        updateCityAddressAndWeather(location)
                        cancel()
                    }
                }
            }*/
        }
    })

    fun onNavigateFromSearch(coordinate: Coordinate) {
        Log.d(TAG, "onNavigateFromSearch($coordinate)")
        viewModelScope.launch {
            onRefresh()
            /*            if (locationRepository.getLocation(coordinate)
                                .isNull() && _lastLocation.value?.isCurrentLocation != true
                        ) {
                            userMessage.value = UserMessage.AddingLocation
                        }*/
        }
    }

    fun onSaveInfo(countryCode: String) {
        Log.d(TAG, "onSaveInfo($countryCode) called")
        viewModelScope.launch {
/*            _lastLocation.value?.let {
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
            }*/
            showSnackbarMessage(R.string.location_saved)
        }
    }

    private fun navigateToSearch() {
        _appRoute = WtnDestinations.SEARCH_ROUTE
        _isInitializing.value = false
    }

    /*    private suspend fun handleLocation(location: LocationPreferences): LocationPreferences? {
            if (!isFirstTimeRunApp() && location == LocationPreferences.getDefaultInstance()) {
                navigateToSearch()
                return null
            }

            return when (val isValidateSuccess = validateCurrentLocationUseCase(location)) {
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
        }*/

    private fun handleWeatherResult(
        result: Result<NetworkAllWeather>,
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

    /*    private suspend fun updateCityAddressAndWeather(location: LocationPreferences) {
            // Update city address
            _cityAddress.value = location.cityAddress
            // Update weather
            val weather = handleWeatherResult(
                weatherRepository.getAllWeather(location.coordinate, location.timeZone),
                location.timeZone,
            )
            _weatherAsync.value = weather
            Log.d(TAG, "WeatherAsync flow collected: $weather")
        }*/
}
