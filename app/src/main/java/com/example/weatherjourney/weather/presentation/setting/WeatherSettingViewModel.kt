package com.example.weatherjourney.weather.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.data.repository.DefaultRefreshRepository
import com.example.weatherjourney.weather.domain.mapper.getTemperatureUnit
import com.example.weatherjourney.weather.domain.mapper.getWindSpeedUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingViewModel @Inject constructor(
    private val preferences: PreferenceRepository,
    private val refreshRepository: DefaultRefreshRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherSettingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                WeatherSettingUiState(
                    temperatureLabel = preferences.temperatureUnitFlow.first().label,
                    windSpeedLabel = preferences.windSpeedUnitFlow.first().label
                )
            }
        }
    }

    fun onTemperatureLabelUpdate(label: String) {
        _uiState.update { it.copy(temperatureLabel = label) }

        viewModelScope.launch {
            preferences.saveTemperatureUnit(getTemperatureUnit(label))
            refreshRepository.emit()
        }
    }

    fun onWindSpeedLabelUpdate(label: String) {
        _uiState.update { it.copy(windSpeedLabel = label) }

        viewModelScope.launch {
            preferences.saveWindSpeedUnit(getWindSpeedUnit(label))
            refreshRepository.emit()
        }
    }
}
