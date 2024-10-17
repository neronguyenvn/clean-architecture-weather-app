package com.example.weatherjourney.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.feature.details.WeatherInfoState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val convertUnitUseCase: ConvertUnitUseCase,
    gpsRepository: GpsRepository,
    locationRepository: LocationRepository,
    userDataRepository: UserDataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val detailsRoute = savedStateHandle.toRoute()

    private val _state = MutableStateFlow(Idle)
    val state = _state.asStateFlow()

    val locationWithWeather = savedStateHandle.getStateFlow()
}

sealed interface WeatherInfoState {

    data object Loading : WeatherInfoState

    data object Idle : WeatherInfoState
}
