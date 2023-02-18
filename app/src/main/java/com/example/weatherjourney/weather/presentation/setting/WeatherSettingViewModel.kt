package com.example.weatherjourney.weather.presentation.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.data.repository.DefaultRefreshRepository
import com.example.weatherjourney.weather.domain.mapper.getTemperatureUnit
import com.example.weatherjourney.weather.domain.mapper.getWindSpeedUnit
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
            val windSpeedUnit = preferences.getWindSpeedUnit()
            uiState = uiState.copy(
                temperatureLabel = temperatureUnit.label,
                windSpeedLabel = windSpeedUnit.label
            )
        }
    }

    fun onEvent(event: WeatherSettingEvent) {
        when (event) {
            is WeatherSettingEvent.OnTemperatureLabelUpdate -> {
                uiState = uiState.copy(temperatureLabel = event.label)

                viewModelScope.launch {
                    preferences.saveTemperatureUnit(getTemperatureUnit(event.label))
                    refreshRepository.emit()
                }
            }

            is WeatherSettingEvent.OnWindSpeedLabelUpdate -> {
                uiState = uiState.copy(windSpeedLabel = event.label)

                viewModelScope.launch {
                    preferences.saveWindSpeedUnit(getWindSpeedUnit(event.label))
                    refreshRepository.emit()
                }
            }
        }
    }
}
