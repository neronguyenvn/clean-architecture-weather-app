package com.example.weatherjourney.features.weather.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.domain.ConvertUseCase
import com.example.weatherjourney.core.model.search.SavedLocation
import com.example.weatherjourney.core.model.search.SuggestionLocation
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

data class WeatherSearchUiState(
    val simpleState: WeatherSearchSimpleState = WeatherSearchSimpleState(),
    val savedLocations: List<SavedLocation?> = emptyList(),
    val suggestionLocations: List<SuggestionLocation> = emptyList(),
    val eventSink: (WeatherSearchEvent) -> Unit = {}
)

data class WeatherSearchSimpleState(
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

    private val _simpleState = MutableStateFlow(WeatherSearchSimpleState())
    private val _suggestionLocations = MutableStateFlow<List<SuggestionLocation>>(emptyList())
    private val _currentCoordinate = gpsRepository.getCurrentCoordinateStream()

    val uiState = combine(
        _temperatureUnit,
        _simpleState,
        _suggestionLocations,
        locationRepository.getAllLocationWithWeatherStream(),
        _currentCoordinate,
    ) { tUnit, state, suggests, locations, coordinate ->

        WeatherSearchUiState(
            simpleState = state,
            suggestionLocations = suggests,
            savedLocations = if (coordinate is Result.Success) {
                convertUseCase(locations, tUnit, coordinate.data)
            } else convertUseCase(locations, tUnit, null)
        ) { event ->
            when (event) {
                Refresh -> viewModelScope.launch {
                    runCatching { weatherRepository.refreshWeatherOfLocations() }
                }

                is InputLocation -> {
                    _simpleState.update { it.copy(input = event.value) }
                    refreshSuggestionLocations()
                }

                is LongClickOnSavedLocation -> _simpleState.update {
                    it.copy(savedLocation = event.location)
                }

                DeleteSavedLocation -> viewModelScope.launch {
                    state.savedLocation?.id?.let { locationRepository.deleteLocation(it) }
                }

                is ClickOnSavedLocation -> viewModelScope.launch {
                    locationRepository.makeLocationDisplayed(event.location.id)
                }

                is ClickOnSuggestionLocation -> viewModelScope.launch {
                    locationRepository.saveLocation(event.location)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = WeatherSearchUiState(),
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
