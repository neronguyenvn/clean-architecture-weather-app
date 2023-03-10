package com.example.weatherjourney.weather.presentation.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.presentation.BaseViewModel
import com.example.weatherjourney.util.ActionLabel
import com.example.weatherjourney.util.Async
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.data.mapper.coordinate
import com.example.weatherjourney.weather.data.mapper.toSavedCity
import com.example.weatherjourney.weather.domain.model.CityUiModel
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.weather.domain.repository.RefreshRepository
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherSearchViewModel"

private data class WeatherSearchViewModelState(
    val input: String = "",
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val savedCities: List<SavedCity> = emptyList(),
    val suggestionCities: List<SuggestionCity> = emptyList()
) {
    fun toUiState(): WeatherSearchUiState =
        if (input.isBlank()) {
            WeatherSearchUiState.ShowSaveCities(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                savedCities = savedCities
            )
        } else {
            WeatherSearchUiState.ShowSuggestionCities(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                suggestionCities = suggestionCities
            )
        }
}

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases,
    private val refreshRepository: RefreshRepository,
    preferences: PreferenceRepository
) : BaseViewModel() {

    init {
        _isLoading.value = true
        viewModelScope.launch {
            locationUseCases.validateCurrentLocation()
            _viewModelState.collect { state ->
                _uiState.update { state.toUiState() }
            }
        }
    }

    private val _temperatureUnit = preferences.temperatureUnitFlow
        .map {
            Log.d(TAG, "TemperatureUnit flow collected: $it")
            it
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TemperatureUnit.CELSIUS
        )

    private val _locations = locationUseCases.getLocationsStream().map {
        Log.d(TAG, "Locations flow collected: $it")
        it
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _savedCities = _locations.flatMapLatest { locations ->
        flow {
            val channel = Channel<SavedCity>()
            handleLocations(channel, locations)

            var savedCities = emptyList<SavedCity>()

            repeat(locations.size) {
                val city = channel.receive()
                savedCities = if (city.isCurrentLocation) {
                    savedCities.toMutableList().apply { add(0, city) }
                } else {
                    savedCities + city
                }

                Log.d(TAG, "SavedCities flow collected: $savedCities")
                emit(savedCities)
                if (it == locations.size - 1) {
                    _isLoading.value = false
                }
            }
        }
    }

    private val _input = MutableStateFlow("")

    private val _suggestionCities = _input
        .map {
            if (it.length < 2) {
                emptyList()
            } else {
                handleSuggestionCitiesResult(locationUseCases.getSuggestionCities(it))
            }
        }.map {
            Log.d(TAG, "SuggestCitiesAsync flow collected: $it")
            it
        }.onStart { Async.Loading }

    private val _viewModelState = combine(
        _input,
        _savedCities,
        _suggestionCities,
        _isLoading,
        _userMessage
    ) { input, savedCitiesAsync, suggestionCities, isLoading, userMessage ->

        WeatherSearchViewModelState(
            input = input,
            isLoading = isLoading,
            userMessage = userMessage,
            savedCities = weatherUseCases.convertUnit(savedCitiesAsync, _temperatureUnit.value),
            suggestionCities = suggestionCities
        )
    }.map {
        Log.d(TAG, "UiState flow collected: $it")
        it
    }

    private val _uiState =
        MutableStateFlow(WeatherSearchViewModelState(isLoading = true).toUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var tempSavedCity: CityUiModel

    fun onInputUpdate(input: String) {
        _input.value = input
    }

    fun onRefresh() = viewModelScope.launch {
        runSuspend(
            launch { _savedCities.first() },
            launch { delay(1500) }
        )
    }

    fun onDeleteLocation() = viewModelScope.launch {
        locationUseCases.deleteLocation(tempSavedCity.coordinate)
        _userMessage.value = UserMessage(UiText.StringResource(R.string.location_deleted))
    }

    fun onSavedCityLongClick(city: CityUiModel) {
        tempSavedCity = city

        _userMessage.value = UserMessage(
            message = UiText.StringResource(R.string.delete_location, listOf(city.cityAddress)),
            actionLabel = ActionLabel.DELETE
        )
    }

    private fun handleLocations(channel: Channel<SavedCity>, locations: List<LocationEntity>) {
        for (location in locations) {
            viewModelScope.launch {
                when (
                    val weather = weatherUseCases.getAllWeather(
                        location.coordinate,
                        location.timeZone
                    )
                ) {
                    is Result.Success -> {
                        val city = weather.data.toSavedCity(
                            location.cityAddress,
                            location.coordinate,
                            location.timeZone,
                            location.isCurrentLocation,
                            location.countryCode
                        )

                        channel.send(city)
                    }

                    is Result.Error -> {
                        val message = weather.toString()
                        showSnackbarMessage(UserMessage(UiText.DynamicString(message)))

                        refreshRepository.startListenWhenConnectivitySuccess()

                        if (listenSuccessNetworkJob != null) return@launch

                        listenSuccessNetworkJob = viewModelScope.launch {
                            refreshRepository.outputWorkInfo.collect { info ->
                                if (info.state.isFinished) {
                                    showSnackbarMessage(UserMessage(UiText.StringResource(R.string.restore_internet_connection)))
                                    onRefresh()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleSuggestionCitiesResult(result: Result<List<SuggestionCity>>): List<SuggestionCity> {
        return if (result is Result.Success) {
            result.data
        } else {
            val message = result.toString()
            showSnackbarMessage(UserMessage(UiText.DynamicString(message)))

            refreshRepository.startListenWhenConnectivitySuccess()

            if (listenSuccessNetworkJob != null) {
                emptyList<SuggestionCity>()
            }

            listenSuccessNetworkJob = viewModelScope.launch {
                refreshRepository.outputWorkInfo.collect { info ->
                    if (info.state.isFinished) {
                        showSnackbarMessage(UserMessage(UiText.StringResource(R.string.restore_internet_connection)))
                        onRefresh()
                    }
                }
            }

            emptyList()
        }
    }
}
