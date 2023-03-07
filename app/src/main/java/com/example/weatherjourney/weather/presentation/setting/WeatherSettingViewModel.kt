package com.example.weatherjourney.weather.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.mapper.getTemperatureUnit
import com.example.weatherjourney.weather.domain.mapper.getWindSpeedUnit
import com.example.weatherjourney.weather.domain.model.unit.AllUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingViewModel @Inject constructor(
    private val preferences: PreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AllUnit?>(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                AllUnit(
                    temperature = preferences.temperatureUnitFlow.first(),
                    windSpeed = preferences.windSpeedUnitFlow.first()
                )
            }
        }
    }

    fun onTemperatureLabelUpdate(label: String) {
        val temperatureUnit = getTemperatureUnit(label)
        _uiState.update { it?.copy(temperature = temperatureUnit) }

        viewModelScope.launch {
            preferences.saveTemperatureUnit(temperatureUnit)
        }
    }

    fun onWindSpeedLabelUpdate(label: String) {
        val windSpeedUnit = getWindSpeedUnit(label)
        _uiState.update { it?.copy(windSpeed = windSpeedUnit) }

        viewModelScope.launch {
            preferences.saveWindSpeedUnit(getWindSpeedUnit(label))
        }
    }
}
