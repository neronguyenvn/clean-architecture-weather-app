package com.example.weatherjourney.features.weather.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.domain.ConvertUseCase
import com.example.weatherjourney.core.model.search.SavedLocation
import com.example.weatherjourney.core.model.search.SuggestionLocation
import com.example.weatherjourney.core.model.unit.TemperatureUnit
import com.example.weatherjourney.features.weather.search.WeatherSearchEvent.ClickOnSavedLocation
import com.example.weatherjourney.features.weather.search.WeatherSearchEvent.ClickOnSuggestionLocation
import com.example.weatherjourney.features.weather.search.WeatherSearchEvent.DeleteSavedLocation
import com.example.weatherjourney.features.weather.search.WeatherSearchEvent.InputLocation
import com.example.weatherjourney.features.weather.search.WeatherSearchEvent.LongClickOnSavedLocation
import com.example.weatherjourney.features.weather.search.WeatherSearchEvent.Refresh
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
        val suggestionLocations: List<SuggestionLocation>,
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
        val savedLocations: List<SavedLocation?> = emptyList(),
        val selectedLocation: SavedLocation? = null,
        override val eventSink: (WeatherSearchEvent) -> Unit
    ) : WeatherSearchUiState()
}

data class WeatherSearchSimpleViewModelState(
    val input: String = "",
    val isLoading: Boolean = false,
    val savedLocation: SavedLocation? = null,
)


@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val convertUseCase: ConvertUseCase,
    gpsRepository: GpsRepository,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    private val _temperatureUnit = userDataRepository.userData
        .map { it.temperatureUnit }

    private val _simpleState = MutableStateFlow(WeatherSearchSimpleViewModelState())
    private val _suggestionLocations = MutableStateFlow<List<SuggestionLocation>>(emptyList())
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
                it.copy(savedLocation = event.location)
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
        _suggestionLocations,
        locationRepository.getLocationsWithWeatherStream(),
        _currentCoordinate,
    ) { tUnit, state, suggests, locations, coordinate ->

        val savedLocations = if (coordinate is Result.Success) {
            convertUseCase(locations, tUnit, coordinate.data)
        } else convertUseCase(locations, tUnit, null)

        when {
            suggests.isNotEmpty() -> WeatherSearchUiState.SuggestionLocationsFeed(
                input = state.input,
                suggestionLocations = suggests,
                eventSink = eventSink
            )

            state.input.isNotBlank() -> WeatherSearchUiState.NoResult(state.input, eventSink)

            else -> WeatherSearchUiState.SavedLocationsFeed(
                isLoading = state.isLoading,
                temperatureUnit = tUnit,
                hasLocateButton = savedLocations.any { it?.isCurrentLocation == true },
                savedLocations = savedLocations,
                selectedLocation = state.savedLocation,
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
            _suggestionLocations.value = _simpleState.value.input.let {
                if (it.length < REQUIRED_INPUT_LENGTH) {
                    return@let emptyList()
                }

                when (val result = locationRepository.getSuggestionLocations(it)) {
                    is Result.Success -> result.data
                    is Result.Error -> emptyList()
                }
            }
        }
    }
}
