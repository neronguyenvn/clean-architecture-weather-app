package com.example.weatherjourney.features.weather.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.common.util.Result.Success
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.model.coordinate
import com.example.weatherjourney.core.database.model.weather
import com.example.weatherjourney.core.datastore.model.toAllUnit
import com.example.weatherjourney.core.domain.ConvertUseCase
import com.example.weatherjourney.core.model.info.Weather
import com.example.weatherjourney.core.model.unit.AllUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherInfoUiState(
    val isLoading: Boolean = false,
    val isCurrentLocation: Boolean = false,
    val address: String = "",
    val units: AllUnit? = null,
    val weather: Weather? = null,
    val eventSink: (WeatherInfoEvent) -> Unit = {}
)

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val convertUseCase: ConvertUseCase,
    gpsRepository: GpsRepository,
    locationRepository: LocationRepository,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _units = userDataRepository.userData.map { userData ->
        userData.toAllUnit()
    }

    private val _currentCoordinate = gpsRepository.getCurrentCoordinateStream()

    val uiState: StateFlow<WeatherInfoUiState> = combine(
        _isLoading,
        _units,
        locationRepository.getDisplayedLocationWithWeatherStream(),
        _currentCoordinate
    ) { isLoading, units, locationWithWeather, coordinate ->

        val isCurrentLocation = if (coordinate is Success) {
            locationWithWeather?.location?.coordinate == coordinate.data
        } else false

        WeatherInfoUiState(
            isLoading = isLoading,
            address = locationWithWeather?.location?.address ?: "",
            units = units,
            weather = convertUseCase(
                weather = locationWithWeather?.weather,
                units = units
            ),
            isCurrentLocation = isCurrentLocation,
        ) { event ->
            when (event) {
                WeatherInfoEvent.Refresh -> viewModelScope.launch {
                    _isLoading.value = true
                    if (isCurrentLocation) {
                        weatherRepository.refreshWeatherOfCurrentLocation()
                    } else {
                        weatherRepository.refreshWeatherOfLocation(null)
                    }
                    _isLoading.value = false
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = WeatherInfoUiState(),
        started = SharingStarted.WhileSubscribed(5000)
    )
}
