package com.example.weatherjourney.weather.setting

import com.example.weatherjourney.fake.FakePreferences
import com.example.weatherjourney.features.weather.domain.model.unit.PressureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TimeFormatUnit
import com.example.weatherjourney.features.weather.domain.model.unit.WindSpeedUnit
import com.example.weatherjourney.features.weather.presentation.setting.WeatherSettingViewModel
import com.example.weatherjourney.util.CoroutineRule
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherSettingViewModelTest {

    private lateinit var viewModel: WeatherSettingViewModel
    private lateinit var preferences: FakePreferences


    @get:Rule
    val coroutineRule = CoroutineRule()

    @Before
    fun setup() {
        preferences = FakePreferences()
        viewModel = WeatherSettingViewModel(preferences)
    }

    @Test
    fun onTemperatureUnitUpdate_uiStateUpdated() = runTest {
        // Arrange
        val label = TemperatureUnit.FAHRENHEIT.label
        val expectedUnit = TemperatureUnit.FAHRENHEIT

        // Act
        viewModel.onTemperatureUnitUpdate(label)
        runCurrent()

        // Assert
        assertEquals(expectedUnit, viewModel.uiState.value?.temperature)
    }

    @Test
    fun onWindSpeedUnitUpdate_uiStateUpdated() = runTest {
        // Arrange
        val label = WindSpeedUnit.METER_PER_SECOND.label
        val expectedUnit = WindSpeedUnit.METER_PER_SECOND

        // Act
        viewModel.onWindSpeedUnitUpdate(label)
        runCurrent()

        // Assert
        assertEquals(expectedUnit, viewModel.uiState.value?.windSpeed)
    }

    @Test
    fun onPressureUnitUpdate_uiStateUpdated() = runTest {
        // Arrange
        val label = PressureUnit.INCH_OF_MERCURY.label
        val expectedUnit = PressureUnit.INCH_OF_MERCURY

        // Act
        viewModel.onPressureUnitUpdate(label)
        runCurrent()

        // Assert
        assertEquals(expectedUnit, viewModel.uiState.value?.pressure)
    }

    @Test
    fun onTimeFormatUnitUpdate_uiStateUpdated() = runTest {
        // Arrange
        val label = TimeFormatUnit.AM_PM.label
        val expectedUnit = TimeFormatUnit.AM_PM

        // Act
        viewModel.onTimeFormatUnitUpdate(label)
        runCurrent()

        // Assert
        assertEquals(expectedUnit, viewModel.uiState.value?.timeFormat)
    }
}