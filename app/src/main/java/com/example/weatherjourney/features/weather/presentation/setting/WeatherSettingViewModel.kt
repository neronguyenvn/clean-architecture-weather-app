package com.example.weatherjourney.features.weather.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.weather.domain.mapper.getPressureUnit
import com.example.weatherjourney.features.weather.domain.mapper.getTemperatureUnit
import com.example.weatherjourney.features.weather.domain.mapper.getTimeFormatUnit
import com.example.weatherjourney.features.weather.domain.mapper.getWindSpeedUnit
import com.example.weatherjourney.features.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _temperatureUnit = appPreferences.temperatureUnitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _windSpeedUnit = appPreferences.windSpeedUnitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _pressureUnit = appPreferences.pressureUnitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _timeFormatUnit = appPreferences.timeFormatUnitFlow
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
        appPreferences.saveTemperatureUnit(getTemperatureUnit(label))
    }

    fun onWindSpeedUnitUpdate(label: String) = viewModelScope.launch {
        appPreferences.saveWindSpeedUnit(getWindSpeedUnit(label))
    }

    fun onPressureUnitUpdate(label: String) = viewModelScope.launch {
        appPreferences.savePressureUnit(getPressureUnit(label))
    }

    fun onTimeFormatUnitUpdate(label: String) = viewModelScope.launch {
        appPreferences.saveTimeFormatUnit(getTimeFormatUnit(label))
    }
}
