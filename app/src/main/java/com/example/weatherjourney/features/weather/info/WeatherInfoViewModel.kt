package com.example.weatherjourney.features.weather.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.common.util.UserMessage
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.model.weather
import com.example.weatherjourney.core.datastore.model.toAllUnit
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.core.domain.ValidateCurrentLocationUseCase
import com.example.weatherjourney.core.model.unit.AllUnit
import com.example.weatherjourney.core.model.weather.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class WeatherInfoUiState(
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val isCurrentLocation: Boolean = false,
    val cityAddress: String = "",
    val units: AllUnit? = null,
    val weather: Weather? = null,
)

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val userDataRepository: UserDataRepository,
    private val weatherRepository: WeatherRepository,
    private val convertUnitUseCase: ConvertUnitUseCase,
    private val validateCurrentLocationUseCase: ValidateCurrentLocationUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _units = userDataRepository.userData.map { userData ->
        userData.toAllUnit()
    }

    val uiState: StateFlow<WeatherInfoUiState> = combine(
        _isLoading,
        _units,
        locationRepository.getDisplayedLocationWithWeatherStream()
    ) { isLoading, units, locationWithWeather ->
        WeatherInfoUiState(
            isLoading = isLoading,
            cityAddress = locationWithWeather?.location?.cityAddress ?: "",
            units = units,
            weather = convertUnitUseCase(
                weather = locationWithWeather?.weather,
                units = units
            )
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = WeatherInfoUiState(),
        started = SharingStarted.WhileSubscribed(5000)
    )
}
