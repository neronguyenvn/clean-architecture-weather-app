package com.example.weatherjourney.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.feature.details.DetailsUiState.*
import com.example.weatherjourney.feature.details.navigation.DetailsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val convertUnitUseCase: ConvertUnitUseCase,
    gpsRepository: GpsRepository,
    locationRepository: LocationRepository,
    userDataRepository: UserDataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _detailsRoute = savedStateHandle.toRoute<DetailsRoute>()
    private val _uiState = MutableStateFlow<DetailsUiState>(Loading)

    val uiState = _uiState.asStateFlow()
    val userData = userDataRepository.userData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = null
    )

    val locationWithWeather = when (_detailsRoute) {
        is DetailsRoute.CurrentLocationDetails -> error("Case not handled yet") // TODO
        is DetailsRoute.LocationDetails -> locationRepository.getLocationWithWeather(
            _detailsRoute.locationId
        )

        DetailsRoute.Placeholder -> TODO()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = null
    )

    fun refreshWeather() {
        viewModelScope.launch {
            when (_detailsRoute) {
                is DetailsRoute.CurrentLocationDetails -> error("Case not handled yet") // TODO
                is DetailsRoute.LocationDetails -> {
                    weatherRepository.refreshWeatherOfLocation(_detailsRoute.locationId)
                }

                DetailsRoute.Placeholder -> TODO()
            }
        }
    }
}

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data object Idle : DetailsUiState
}
