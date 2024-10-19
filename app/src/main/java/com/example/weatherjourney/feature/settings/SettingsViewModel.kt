package com.example.weatherjourney.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.datastore.model.UserData
import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.WindSpeedUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    val uiState = userDataRepository.userData
        .map { SettingsUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = SettingsUiState.Loading,
        )

    fun onTemperatureChanged(unit: TemperatureUnit) {
        viewModelScope.launch {
            userDataRepository.setTemperatureUnit(unit)
        }
    }

    fun onWindSpeedChanged(unit: WindSpeedUnit) {
        viewModelScope.launch {
            userDataRepository.setWindSpeedUnit(unit)
        }
    }

    fun onPressureChanged(unit: PressureUnit) {
        viewModelScope.launch {
            userDataRepository.setPressureUnit(unit)
        }
    }

    fun onTimeFormatChanged(unit: TimeFormatUnit) {
        viewModelScope.launch {
            userDataRepository.setTimeFormatUnit(unit)
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val data: UserData) : SettingsUiState
}