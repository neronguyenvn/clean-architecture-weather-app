package com.example.weatherjourney.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.feature.home.HomeUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    locationRepository: LocationRepository,
    userDataRepository: UserDataRepository,
    private val convertUnitUseCase: ConvertUnitUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(Loading)
    val uiState = _state.asStateFlow()

    val locationsWithWeather = combine(
        userDataRepository.userData,
        locationRepository.getLocationsWithWeather(),
    ) { userData, locations ->
        locations.map { location ->
            location.weather?.let { weather ->
                location.copy(
                    weather = convertUnitUseCase(
                        weather = weather,
                        temperatureUnit = userData.temperatureUnit,
                        windSpeedUnit = userData.windSpeedUnit,
                        pressureUnit = userData.pressureUnit,
                        timeFormatUnit = userData.timeFormatUnit
                    )
                )
            } ?: location
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5_000)
    )
}

sealed interface HomeUiState {

    data object Idle : HomeUiState

    data object Loading : HomeUiState
}
