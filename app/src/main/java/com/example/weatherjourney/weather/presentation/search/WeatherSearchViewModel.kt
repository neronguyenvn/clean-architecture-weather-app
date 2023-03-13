package com.example.weatherjourney.weather.presentation.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.presentation.BaseViewModel
import com.example.weatherjourney.util.ActionLabel
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.data.mapper.coordinate
import com.example.weatherjourney.weather.data.mapper.toSavedCity
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.domain.model.WeatherType
import com.example.weatherjourney.weather.domain.repository.RefreshRepository
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherSearchViewModel"
private const val REQUIRED_INPUT_LENGTH = 2

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases,
    refreshRepository: RefreshRepository,
    preferences: PreferenceRepository
) : BaseViewModel(refreshRepository) {

    init {
        _isLoading.value = true
        viewModelScope.launch {
            locationUseCases.validateCurrentLocation()
            onRefresh()
            _viewModelState.collect { state ->
                _uiState.update {
                    state.toUiState().also {
                        if (it is WeatherSearchState.ShowSuggestionCities) {
                            refreshSuggestionCities()
                        }
                    }
                }
            }
        }
    }

    private val _temperatureUnit = preferences.temperatureUnitFlow
        .map {
            it.also { Log.d(TAG, "TemperatureUnit flow collected: $it") }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _locations = locationUseCases.getLocationsStream().map {
        it.also { Log.d(TAG, "Locations flow collected: $it") }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    private val _input = MutableStateFlow("")
    private val _savedCities = MutableStateFlow<List<SavedCity>>(emptyList())
    private val _suggestionCities = MutableStateFlow<List<SuggestionCity>>(emptyList())

    private val _viewModelState = combine(
        _input,
        _savedCities,
        _suggestionCities,
        _isLoading,
        _userMessage
    ) { input, savedCities, suggestionCities, isLoading, userMessage ->

        WeatherSearchViewModelState(
            input = input,
            isLoading = isLoading,
            userMessage = userMessage,
            savedCities = weatherUseCases.convertUnit(savedCities, _temperatureUnit.value),
            suggestionCities = suggestionCities
        )
    }

    private val _uiState =
        MutableStateFlow(WeatherSearchViewModelState(isLoading = true).toUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var tempSavedCity: SavedCity

    fun onInputUpdate(input: String) {
        _input.value = input
    }

    override fun onRefresh() = onRefresh({
        val channel = Channel<SavedCity?>()
        val locations = _locations.first { it.isNotEmpty() }
        handleLocations(channel, locations)

        val savedCities = ArrayDeque<SavedCity>()
        var currentCity: SavedCity? = null

        locations.forEach { _ ->
            val city = channel.receive()

            city?.let {
                if (city.isCurrentLocation) {
                    currentCity = city
                } else {
                    savedCities.add(city)
                }
            }
        }

        savedCities.sortBy { it.id }
        _savedCities.value =
            if (currentCity == null) savedCities else savedCities.apply { addFirst(currentCity!!) }

        _isLoading.value = false
        Log.d(TAG, "SavedCities flow collected: $savedCities")
    })

    fun onDeleteLocation() {
        _savedCities.update { it.toMutableList().apply { remove(tempSavedCity) } }
        showSnackbarMessage(R.string.location_deleted)
        viewModelScope.launch { locationUseCases.deleteLocation(tempSavedCity.coordinate) }
    }

    fun onSavedCityLongClick(city: SavedCity) {
        if (city.isCurrentLocation) return
        tempSavedCity = city
        showSnackbarMessage(R.string.delete_location, ActionLabel.DELETE, city.cityAddress)
    }

    private fun handleLocations(channel: Channel<SavedCity?>, locations: List<LocationEntity>) {
        locations.forEach { location ->
            viewModelScope.launch {
                when (
                    val weather =
                        weatherUseCases.getAllWeather(location.coordinate, location.timeZone)
                ) {
                    is Result.Success -> {
                        val city = weather.data.hourly.let {
                            location.toSavedCity(
                                it.temperatures[0],
                                WeatherType.fromWMO(it.weatherCodes[0])
                            )
                        }

                        channel.send(city)
                    }

                    is Result.Error -> {
                        channel.send(null)
                        handleErrorResult(weather)
                    }
                }
            }
        }
    }

    private fun handleSuggestionCitiesResult(result: Result<List<SuggestionCity>>): List<SuggestionCity> {
        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> {
                handleErrorResult(result, false) { refreshSuggestionCities() }
                emptyList()
            }
        }
    }

    private fun refreshSuggestionCities() {
        viewModelScope.launch {
            _suggestionCities.value = _input.value.let {
                if (it.length < REQUIRED_INPUT_LENGTH) {
                    emptyList()
                } else {
                    handleSuggestionCitiesResult(locationUseCases.getSuggestionCities(it))
                }
            }
        }
    }
}
