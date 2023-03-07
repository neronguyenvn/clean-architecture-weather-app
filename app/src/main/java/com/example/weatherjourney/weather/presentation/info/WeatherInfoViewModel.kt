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
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.util.UserMessage
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherInfoViewModel"

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases,
    private val preferences: PreferenceRepository,
    private val refreshRepository: RefreshRepository
) : BaseViewModel() {

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    private var _appRoute = WeatherDestinations.INFO_ROUTE
    val appRoute get() = _appRoute

    private val _temperatureUnit = preferences.temperatureUnitFlow
    private val _windSpeedUnit = preferences.windSpeedUnitFlow

    private val _units =
        combine(_temperatureUnit, _windSpeedUnit) { temperatureUnit, windSpeedUnit ->
            AllUnit(
                temperature = temperatureUnit,
                windSpeed = windSpeedUnit
            )
        }.distinctUntilChanged().map {
            Log.d(TAG, "Labels flow collected: $it")
            it
        }.shareIn(
            viewModelScope,
            SharingStarted.Eagerly,
            replay = 1
        )

    private val _lastLocation = preferences.locationPreferencesFlow
        .map {
            handleLocation(it).also { result ->
                Log.d(TAG, "Location Async flow collected: $result")
            }
        }.stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    private val _weatherAsync = _lastLocation.map { location ->
        when (location) {
            null -> Async.Loading
            else -> {
                location.let {
                    handleWeatherResult(
                        weatherUseCases.getAllWeather(it.coordinate.toCoordinate(), it.timeZone),
                        it.cityAddress,
                        it.timeZone
                    )
                }
            }
        }
    }.map {
        Log.d(TAG, "WeatherAsync flow collected: $it")
        it
    }.onStart { Async.Loading }

    val uiState: StateFlow<WeatherInfoUiState> = combine(
        _isLoading,
        _userMessage,
        _weatherAsync,
        _units
    ) { isLoading, userMessage, weatherAsync, units ->
        when (weatherAsync) {
            Async.Loading -> {
                WeatherInfoUiState(isLoading = true)
            }

            is Async.Success -> {
                _isInitializing.value = false
                WeatherInfoUiState(
                    allUnit = units,
                    isLoading = isLoading,
                    userMessage = userMessage,
                    allWeather = weatherAsync.data?.let { weatherUseCases.convertUnit(it, units) }
                        ?: AllWeather()
                )
            }
        }
    }.map {
        Log.d(TAG, "UiState flow collected: $it")
        it
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = WeatherInfoUiState(isLoading = true)
    )

    fun onActivityCreate(isLocationPermissionGranted: Boolean) {
        if (!isLocationPermissionGranted) {
            _appRoute = WeatherDestinations.SEARCH_ROUTE
            _isInitializing.value = false
        } else {
            viewModelScope.launch { uiState.first() }
        }
    }

    fun onRefresh(isDelay: Boolean) = viewModelScope.launch {
        Log.d(TAG, "onRefresh($isDelay) called")
        runSuspend(
            launch { _weatherAsync.first() },
            if (isDelay) launch { delay(1500) } else launch { }
        )
    }

    fun onNavigateFromSearch(cityAddress: String, coordinate: Coordinate, timeZone: String) {
        Log.d(TAG, "onNavigateFromSearch($cityAddress, $coordinate, $timeZone) called")
        if (!locationUseCases.validateLocation(cityAddress, coordinate, timeZone)) return

        viewModelScope.launch {
            if (locationUseCases.shouldSaveLocation(coordinate)) {
                _userMessage.value = UserMessage(
                    message = UiText.StringResource(R.string.add_this_location),
                    actionLabel = ActionLabel.ADD
                )
            }
            preferences.updateLocation(cityAddress, coordinate, timeZone)
        }
    }

    fun onSaveInfo(countryCode: String) {
        Log.d(TAG, "onSaveInfo() called")
        viewModelScope.launch {
            _lastLocation.value?.let { locationUseCases.saveLocation(it, countryCode) }
            _userMessage.value = UserMessage(UiText.StringResource(R.string.location_saved))
        }
    }

    private suspend fun handleLocation(location: LocationPreferences): LocationPreferences? =
        when (location) {
            LocationPreferences.getDefaultInstance() -> {
                val result = locationUseCases.getAndSaveCurrentLocation()
                if (result is Result.Error) {
                    _userMessage.update { it?.copy(message = UiText.DynamicString(result.toString())) }
                }
                null
            }

            else -> {
                if (locationUseCases.validateLocation(
                        location.cityAddress,
                        location.coordinate.toCoordinate(),
                        location.timeZone
                    )
                ) {
                    location
                } else {
                    null
                }
            }
        }

    private suspend fun handleWeatherResult(
        result: Result<AllWeatherDto?>,
        cityAddress: String,
        timeZone: String
    ): Async<AllWeather?> {
        if (result == Result.Success(null)) {
            return Async.Success(null)
        }

        return if (result is Result.Success) {
            Async.Success(result.data?.toAllWeather(cityAddress, timeZone))
        } else {
            val message = result.toString()
            showSnackbarMessage(UserMessage(UiText.DynamicString(message)))

            refreshRepository.startListenWhenConnectivitySuccess()

            if (listenSuccessNetworkJob != null) {
                Async.Success(null)
            }

            listenSuccessNetworkJob = viewModelScope.launch {
                refreshRepository.outputWorkInfo.collect { info ->
                    if (info.state.isFinished) {
                        showSnackbarMessage(UserMessage(UiText.StringResource(R.string.restore_internet_connection)))
                        onRefresh(true)
                    }
                }
            }

            Async.Success(null)
        }
    }
}
