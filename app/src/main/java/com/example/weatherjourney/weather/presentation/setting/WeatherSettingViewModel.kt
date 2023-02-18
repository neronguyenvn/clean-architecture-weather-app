package com.example.weatherjourney.weather.presentation.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.data.repository.DefaultRefreshRepository
import com.example.weatherjourney.weather.domain.mapper.getTemperatureUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingViewModel @Inject constructor(
    private val preferences: PreferenceRepository,
    private val refreshRepository: DefaultRefreshRepository
) : ViewModel() {

    var uiState by mutableStateOf(WeatherSettingUiState())

    init {
        viewModelScope.launch {
            val temperatureUnit = preferences.getTemperatureUnit()
            uiState = uiState.copy(temperatureLabel = temperatureUnit.label)
        }
    }

    fun onEvent(event: WeatherSettingEvent) {
        when (event) {
            is WeatherSettingEvent.OnTemperatureUnitUpdate -> {
                uiState = uiState.copy(temperatureLabel = event.unitLabel)

                viewModelScope.launch {
                    preferences.saveTemperatureUnit(getTemperatureUnit(event.unitLabel))
                    refreshRepository.emit()
                }
            }
        }
    }
}
