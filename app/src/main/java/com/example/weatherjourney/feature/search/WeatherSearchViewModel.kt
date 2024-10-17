package com.example.weatherjourney.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.core.model.search.Location
import com.example.weatherjourney.core.model.search.LocationWithWeather
import com.example.weatherjourney.core.model.unit.TemperatureUnit
import com.example.weatherjourney.feature.search.WeatherSearchEvent.ClickOnSavedLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.ClickOnSuggestionLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.DeleteSavedLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.InputLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.LongClickOnSavedLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.Refresh
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val REQUIRED_INPUT_LENGTH = 3

sealed class WeatherSearchUiState {

    abstract val eventSink: (WeatherSearchEvent) -> Unit

    data class SuggestionLocationsFeed(
        val input: String,
        val locations: List<Location>,
        override val eventSink: (WeatherSearchEvent) -> Unit
    ) : WeatherSearchUiState()

    data class NoResult(
        val input: String,
        override val eventSink: (WeatherSearchEvent) -> Unit
    ) : WeatherSearchUiState()

    data class SavedLocationsFeed(
        val isLoading: Boolean = false,
        val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
        val hasLocateButton: Boolean = false,
        val locationWithWeathers: List<LocationWithWeather?> = emptyList(),
        val selectedLocation: LocationWithWeather? = null,
        override val eventSink: (WeatherSearchEvent) -> Unit
    ) : WeatherSearchUiState()
}

data class WeatherSearchSimpleViewModelState(
    val input: String = "",
    val isLoading: Boolean = false,
    val locationWithWeather: LocationWithWeather? = null,
)


@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val convertUnitUseCase: ConvertUnitUseCase,
    gpsRepository: GpsRepository,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    private val _temperatureUnit = userDataRepository.userData
        .map { it.temperatureUnit }

    private val _simpleState = MutableStateFlow(WeatherSearchSimpleViewModelState())
    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    private val _currentCoordinate = gpsRepository.getCurrentCoordinateStream()

    private val eventSink: (WeatherSearchEvent) -> Unit = { event ->
        when (event) {
            is Refresh -> viewModelScope.launch {
                _simpleState.update { it.copy(isLoading = true) }
                runCatching { weatherRepository.refreshWeatherOfLocations() }
                _simpleState.update { it.copy(isLoading = false) }
            }

            is InputLocation -> {
                _simpleState.update { it.copy(input = event.value) }
                refreshSuggestionLocations()
            }

            is LongClickOnSavedLocation -> _simpleState.update {
                it.copy(locationWithWeather = event.location)
            }

            is DeleteSavedLocation -> viewModelScope.launch {
                locationRepository.deleteLocation(event.location.id)
            }

            is ClickOnSavedLocation -> viewModelScope.launch {
                locationRepository.makeLocationDisplayed(event.location.id)
            }

            is ClickOnSuggestionLocation -> viewModelScope.launch {
                locationRepository.saveLocation(event.location)
            }
        }
    }


    val uiState = combine(
        _temperatureUnit,
        _simpleState,
        _locations,
        locationRepository.getLocationsWithWeatherStream(),
        _currentCoordinate,
    ) { tUnit, state, suggests, locations, coordinate ->

        val savedLocations = if (coordinate is Result.Success) {
            convertUnitUseCase(locations, tUnit, coordinate.data)
        } else convertUnitUseCase(locations, tUnit, null)

        when {
            suggests.isNotEmpty() -> WeatherSearchUiState.SuggestionLocationsFeed(
                input = state.input,
                locations = suggests,
                eventSink = eventSink
            )

            state.input.isNotBlank() -> WeatherSearchUiState.NoResult(state.input, eventSink)

            else -> WeatherSearchUiState.SavedLocationsFeed(
                isLoading = state.isLoading,
                temperatureUnit = tUnit,
                hasLocateButton = savedLocations.any { it?.isCurrentLocation == true },
                locationWithWeathers = savedLocations,
                selectedLocation = state.locationWithWeather,
                eventSink = eventSink
            )
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = WeatherSearchUiState.SavedLocationsFeed(eventSink = eventSink),
        started = SharingStarted.WhileSubscribed(5000)
    )

    /*    fun onPermissionActionResult(isGranted: Boolean, shouldDelay: Boolean = false) {
                    if (!isGranted) return

                    viewModelScope.launch {
                        isLoading.value = true
                        // Delay to wait for the location service turn on
                        if (shouldDelay) delay(DELAY_TIME)
                        validateCurrentLocationUseCase()
                        onRefresh()
                    }
        }*/

    private fun refreshSuggestionLocations() {
        viewModelScope.launch {
            _locations.value = _simpleState.value.input.let {
                if (it.length < REQUIRED_INPUT_LENGTH) {
                    return@let emptyList()
                }

                when (val result = locationRepository.getLocationsByAddress(it)) {
                    is Result.Success -> result.data
                    is Result.Error -> emptyList()
                }
            }
        }
    }
}
