package com.example.weatherjourney.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.app.MainActivityUiState.Loading
import com.example.weatherjourney.app.MainActivityUiState.Success
import com.example.weatherjourney.app.navigation.WtnDestinations
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    locationRepository: LocationRepository,
) : ViewModel() {

    private val _locationPermissionResult = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<MainActivityUiState> = combine(
        locationRepository.getDisplayedLocationStream(),
        _locationPermissionResult
    ) { displayedLocation, hasPermission ->

        if (displayedLocation != null) {
            weatherRepository.refreshWeatherOfLocation(null)
            return@combine Success(WtnDestinations.INFO_ROUTE)
        }

        when (hasPermission) {
            false -> Success(WtnDestinations.SEARCH_ROUTE)
            true -> {
                weatherRepository.refreshWeatherOfCurrentLocation()
                Success(WtnDestinations.INFO_ROUTE)
            }

            null -> Loading
        }
    }
        .stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun updateLocationPermissionResult(value: Boolean) {
        _locationPermissionResult.value = value
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val startRoute: String) : MainActivityUiState
}
