package com.example.weatherjourney.features.weather.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.NetworkMonitor
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.core.domain.ValidateCurrentLocationUseCase
import com.example.weatherjourney.core.model.location.CityUiModel
import com.example.weatherjourney.core.model.location.CityWithWeather
import com.example.weatherjourney.core.model.location.SuggestionCity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherSearchViewModel"
private const val REQUIRED_INPUT_LENGTH = 3

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val userDataRepository: UserDataRepository,
    private val locationRepository: LocationRepository,
    private val convertUnitUseCase: ConvertUnitUseCase,
    private val validateCurrentLocationUseCase: ValidateCurrentLocationUseCase,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _temperatureUnit = userDataRepository.userData
        .map { it.temperatureUnit }

    private val _input = MutableStateFlow("")
    private val _suggestionLocations = MutableStateFlow<List<SuggestionCity>>(emptyList())

    val uiState = combine(
        _input,
        _suggestionLocations,
        locationRepository.getAllLocationWithWeatherStream(),
        _temperatureUnit,
    ) { input, suggestionLocations, locationsWithWeather, tUnit ->

        WeatherSearchViewModelState(
            input = input,
            suggestionLocations = suggestionLocations,
            savedCities = convertUnitUseCase(locationsWithWeather, tUnit),
        ).toUiState()
    }.stateIn(
        scope = viewModelScope,
        initialValue = WeatherSearchViewModelState().toUiState(),
        started = SharingStarted.WhileSubscribed(5000)
    )


    private lateinit var tempSavedCity: CityWithWeather

    fun onInputUpdate(input: String) {
        _input.value = input
        refreshSuggestionCities()
    }


    fun onDeleteLocation() {
        viewModelScope.launch {
/*            locationRepository.apply {
                deleteLocation(getLocation(tempSavedCity.coordinate)!!)
            }

            _savedCitiesAsync.update {
                Async.Success((it as Async.Success).data.toMutableList()
                    .apply { remove(tempSavedCity) })
            }

            showSnackbarMessage(R.string.location_deleted)*/
        }
    }

    fun onSavedCityLongClick(city: CityWithWeather) {
        /*        if (city.isCurrentLocation) return
                tempSavedCity = city
                userMessage.value = UserMessage.DeletingLocation(city.cityAddress)*/
    }

    fun onLocationFieldClick() {
        /*        viewModelScope.launch {
                    when (val result = validateCurrentLocationUseCase()) {
                        is Result.Success -> onRefresh()
                        is Result.Error -> {
                            Log.d(TAG, result.exception.toString())
                            when (result.exception) {
                                is LocationPermissionDeniedException -> userMessage.value =
                                    RequestingLocationPermission

                                is LocationServiceDisabledException -> userMessage.value =
                                    RequestingTurnOnLocationService
                            }
                        }
                    }
                }*/
    }

    fun onPermissionActionResult(isGranted: Boolean, shouldDelay: Boolean = false) {
        /*        if (!isGranted) return

                viewModelScope.launch {
                    isLoading.value = true
                    // Delay to wait for the location service turn on
                    if (shouldDelay) delay(DELAY_TIME)
                    validateCurrentLocationUseCase()
                    onRefresh()
                }*/
    }

    fun onItemClick(city: CityUiModel, onItemClick: (CityUiModel) -> Unit) = viewModelScope.launch {
/*        when (city) {
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

        onItemClick(city)*/
    }

    private fun refreshSuggestionCities() {
        viewModelScope.launch {
            _suggestionLocations.value = _input.value.let {
                if (it.length < REQUIRED_INPUT_LENGTH) {
                    emptyList()
                } else {
                    handleSuggestionCitiesResult(locationRepository.getSuggestionCities(it))
                }
            }
        }
    }


    private fun handleSuggestionCitiesResult(result: Result<List<SuggestionCity>>): List<SuggestionCity> {
        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> {
                emptyList()
            }
        }
    }
}
