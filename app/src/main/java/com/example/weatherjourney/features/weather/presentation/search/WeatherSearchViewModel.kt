package com.example.weatherjourney.features.weather.presentation.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.constants.DELAY_TIME
import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.domain.ConnectivityObserver
import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.data.mapper.coordinate
import com.example.weatherjourney.features.weather.data.mapper.toSavedCity
import com.example.weatherjourney.features.weather.domain.model.CityUiModel
import com.example.weatherjourney.features.weather.domain.model.SavedCity
import com.example.weatherjourney.features.weather.domain.model.SuggestionCity
import com.example.weatherjourney.features.weather.domain.model.WeatherType
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import com.example.weatherjourney.features.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.features.weather.domain.usecase.WeatherUseCases
import com.example.weatherjourney.presentation.ViewModeWithMessageAndLoading
import com.example.weatherjourney.util.Async
import com.example.weatherjourney.util.LocationException
import com.example.weatherjourney.util.LocationException.LocationPermissionDeniedException
import com.example.weatherjourney.util.LocationException.LocationServiceDisabledException
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.util.UserMessage.RequestingLocationPermission
import com.example.weatherjourney.util.UserMessage.RequestingTurnOnLocationService
import com.example.weatherjourney.util.isNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private val locationRepository: LocationRepository,
    connectivityObserver: ConnectivityObserver,
    private val appPreferences: AppPreferences,
) : ViewModeWithMessageAndLoading(connectivityObserver) {

    private val _temperatureUnit = appPreferences.temperatureUnitFlow
        .map {
            it.also { Log.d(TAG, "TemperatureUnit flow collected: $it") }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null,
        )


    private val _locations = locationRepository.getLocationsStream().map {
        it.also { Log.d(TAG, "Locations flow collected: $it") }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

    private val _input = MutableStateFlow("")
    private val _savedCitiesAsync = MutableStateFlow<Async<List<SavedCity>>>(Async.Loading)
    private val _suggestionCities = MutableStateFlow<List<SuggestionCity>>(emptyList())

    private val _viewModelState = combine(
        _input,
        _savedCitiesAsync,
        _suggestionCities,
        isLoading,
        userMessage,
    ) { input, savedCitiesAsync, suggestionCities, isLoading, userMessage ->

        when (savedCitiesAsync) {
            Async.Loading -> WeatherSearchViewModelState(isLoading = true)
            is Async.Success -> WeatherSearchViewModelState(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                suggestionCities = suggestionCities,
                savedCities = weatherUseCases.convertUnit(
                    savedCitiesAsync.data, _temperatureUnit.value
                ),
            )
        }
    }

    private val _uiState =
        MutableStateFlow(WeatherSearchViewModelState(isLoading = true).toUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val validateResult = locationUseCases.validateCurrentLocation()
            if (validateResult is Result.Error) {
                when (validateResult.exception) {
                    is LocationException -> locationRepository.apply {
                        getCurrentLocation()?.let { deleteLocation(it) }
                    }

                    else -> {
                        _savedCitiesAsync.value = Async.Success(emptyList())
                        handleErrorResult(validateResult)
                    }
                }
            }

            launch {
                _viewModelState.collect { _uiState.value = it.toUiState() }
            }

            onRefresh()
        }
    }

    private lateinit var tempSavedCity: SavedCity

    fun onInputUpdate(input: String) {
        _input.value = input
    }

    override fun onRefresh() = runSuspend({
        val channel = Channel<SavedCity?>()
        val locations = withTimeoutOrNull(DELAY_TIME) { _locations.first { it.isNotEmpty() } }
            ?: return@runSuspend
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
            if (currentCity.isNull()) savedCities else savedCities.apply { addFirst(currentCity!!) }

        isLoading.value = false
        Log.d(TAG, "SavedCities flow collected: $savedCities")
    })

    fun onDeleteLocation() {
        _savedCities.update { it.toMutableList().apply { remove(tempSavedCity) } }
        viewModelScope.launch {
            locationRepository.apply {
                deleteLocation(getLocation(tempSavedCity.coordinate)!!)
            }
            showSnackbarMessage(R.string.location_deleted)
        }
    }

    fun onSavedCityLongClick(city: SavedCity) {
        if (city.isCurrentLocation) return
        tempSavedCity = city
        userMessage.value = UserMessage.DeletingLocation(city.cityAddress)
    }

    fun onLocationFieldClick() {
        viewModelScope.launch {
            when (val result = locationUseCases.validateCurrentLocation()) {
                is Result.Success -> onRefresh()
                is Result.Error -> {
                    Log.d(TAG, result.exception.toString())
                    when (result.exception) {
                        is LocationPermissionDeniedException ->
                            userMessage.value = RequestingLocationPermission

                        is LocationServiceDisabledException ->
                            userMessage.value = RequestingTurnOnLocationService
                    }
                }
            }
        }
    }

    fun onPermissionActionResult(isGranted: Boolean, shouldDelay: Boolean = false) {
        if (!isGranted) return

        viewModelScope.launch {
            isLoading.value = true
            // Delay to wait for the location service turn on
            if (shouldDelay) delay(DELAY_TIME)
            locationUseCases.validateCurrentLocation()
            onRefresh()
        }
    }

    fun onItemClick(city: CityUiModel, onItemClick: (CityUiModel) -> Unit) = viewModelScope.launch {
        when (city) {
            is SavedCity -> {
                appPreferences.updateLocation(
                    cityAddress = city.cityAddress,
                    coordinate = city.coordinate,
                    timeZone = city.timeZone,
                    isCurrentLocation = city.isCurrentLocation,
                )
            }

            is SuggestionCity -> {
                appPreferences.updateLocation(
                    cityAddress = city.cityAddress,
                    coordinate = city.coordinate,
                    timeZone = city.timeZone,
                    isCurrentLocation = null,
                )
            }
        }

        onItemClick(city)
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
                                WeatherType.fromWMO(it.weatherCodes[0]),
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
                handleErrorResult(result, false)
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
                    handleSuggestionCitiesResult(locationRepository.getSuggestionCities(it))
                }
            }
        }
    }
}
