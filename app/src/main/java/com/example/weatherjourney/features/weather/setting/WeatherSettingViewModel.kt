package com.example.weatherjourney.features.weather.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.datastore.model.toAllUnit
import com.example.weatherjourney.core.model.unit.getPressureUnit
import com.example.weatherjourney.core.model.unit.getTemperatureUnit
import com.example.weatherjourney.core.model.unit.getTimeFormatUnit
import com.example.weatherjourney.core.model.unit.getWindSpeedUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    val uiState = userDataRepository.userData.map {
        it.toAllUnit()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null,
    )

    fun onTemperatureUnitUpdate(label: String) = viewModelScope.launch {
        userDataRepository.setTemperatureUnit(getTemperatureUnit(label))
    }

    fun onWindSpeedUnitUpdate(label: String) = viewModelScope.launch {
        userDataRepository.setWindSpeedUnit(getWindSpeedUnit(label))
    }

    fun onPressureUnitUpdate(label: String) = viewModelScope.launch {
        userDataRepository.setPressureUnit(getPressureUnit(label))
    }

    fun onTimeFormatUnitUpdate(label: String) = viewModelScope.launch {
        userDataRepository.setTimeFormatUnit(getTimeFormatUnit(label))
    }
}
