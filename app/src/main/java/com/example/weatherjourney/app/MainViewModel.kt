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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    locationRepository: LocationRepository,
) : ViewModel() {

    private val _hasLocationPermission = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<MainActivityUiState> = _hasLocationPermission.map { has ->

        val displayedLocation = locationRepository.getDisplayedLocationStream().firstOrNull()
        if (displayedLocation != null) {
            kotlin.runCatching { weatherRepository.refreshWeatherOfLocation(null) }
            return@map Success(WtnDestinations.INFO_ROUTE)
        }

        when (has) {
            null -> Loading
            false -> Success(WtnDestinations.SEARCH_ROUTE)
            true -> {
                kotlin.runCatching { weatherRepository.refreshWeatherOfCurrentLocation() }
                Success(WtnDestinations.INFO_ROUTE)
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun updateLocationPermissionResult(value: Boolean) {
        _hasLocationPermission.value = value
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val startRoute: String) : MainActivityUiState
}
