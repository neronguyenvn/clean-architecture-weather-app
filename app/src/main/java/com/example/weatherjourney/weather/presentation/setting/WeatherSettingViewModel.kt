package com.example.weatherjourney.weather.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.util.WhileUiSubscribed
import com.example.weatherjourney.weather.domain.mapper.getPressureUnit
import com.example.weatherjourney.weather.domain.mapper.getTemperatureUnit
import com.example.weatherjourney.weather.domain.mapper.getTimeFormatUnit
import com.example.weatherjourney.weather.domain.mapper.getWindSpeedUnit
import com.example.weatherjourney.weather.domain.model.unit.AllUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingViewModel @Inject constructor(
    private val preferences: PreferenceRepository
) : ViewModel() {

    private val _temperatureUnit = preferences.temperatureUnitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _windSpeedUnit = preferences.windSpeedUnitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _pressureUnit = preferences.pressureUnitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _timeFormatUnit = preferences.timeFormatUnitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    val uiState =
        combine(
            _temperatureUnit,
            _windSpeedUnit,
            _pressureUnit,
            _timeFormatUnit
        ) { tUnit, wpUnit, psUnit, tfUnit ->
            if (tUnit != null && wpUnit != null && psUnit != null && tfUnit != null) {
                AllUnit(tUnit, wpUnit, psUnit, tfUnit)
            } else {
                null
            }
        }.stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    fun onTemperatureUnitUpdate(label: String) = viewModelScope.launch {
        preferences.saveTemperatureUnit(getTemperatureUnit(label))
    }

    fun onWindSpeedUnitUpdate(label: String) = viewModelScope.launch {
        preferences.saveWindSpeedUnit(getWindSpeedUnit(label))
    }

    fun onPressureUnitUpdate(label: String) = viewModelScope.launch {
        preferences.savePressureUnit(getPressureUnit(label))
    }

    fun onTimeFormatUnitUpdate(label: String) = viewModelScope.launch {
        preferences.saveTimeFormatUnit(getTimeFormatUnit(label))
    }
}
