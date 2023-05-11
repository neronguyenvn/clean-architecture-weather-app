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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
) : ViewModel() {

    val uiState = combine(
        appPreferences.temperatureUnitFlow,
        appPreferences.windSpeedUnitFlow,
        appPreferences.pressureUnitFlow,
        appPreferences.timeFormatUnitFlow,
    ) { tUnit, wpUnit, psUnit, tfUnit ->
        AllUnit(tUnit, wpUnit, psUnit, tfUnit)
    }.stateIn(
        viewModelScope,
        WhileUiSubscribed,
        null,
    )

    fun onTemperatureUnitUpdate(label: String) = viewModelScope.launch {
        appPreferences.updateTemperatureUnit(getTemperatureUnit(label))
    }

    fun onWindSpeedUnitUpdate(label: String) = viewModelScope.launch {
        appPreferences.updateWindSpeedUnit(getWindSpeedUnit(label))
    }

    fun onPressureUnitUpdate(label: String) = viewModelScope.launch {
        appPreferences.updatePressureUnit(getPressureUnit(label))
    }

    fun onTimeFormatUnitUpdate(label: String) = viewModelScope.launch {
        appPreferences.updateTimeFormatUnit(getTimeFormatUnit(label))
    }
}
