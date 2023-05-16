package com.example.weatherjourney.fake

import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.unit.PressureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TimeFormatUnit
import com.example.weatherjourney.features.weather.domain.model.unit.WindSpeedUnit
import com.example.weatherjourney.locationpreferences.LocationPreferences
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferences : AppPreferences {

    var isFirstTimeRunApp = true

    override val locationPreferencesFlow =
        MutableStateFlow(LocationPreferences.getDefaultInstance())

    override val temperatureUnitFlow = MutableStateFlow(TemperatureUnit.CELSIUS)
    override val windSpeedUnitFlow = MutableStateFlow(WindSpeedUnit.KILOMETER_PER_HOUR)
    override val pressureUnitFlow = MutableStateFlow(PressureUnit.HECTOPASCAL)
    override val timeFormatUnitFlow = MutableStateFlow(TimeFormatUnit.TWENTY_FOUR)

    override suspend fun isFirstTimeRunApp(): Boolean = isFirstTimeRunApp

    override suspend fun updateLocation(
        cityAddress: String,
        coordinate: Coordinate,
        timeZone: String,
        isCurrentLocation: Boolean?,
    ) {
        val builder = LocationPreferences.newBuilder()
            .setCityAddress(cityAddress)
            .setLatitude(coordinate.latitude)
            .setLongitude(coordinate.longitude)
            .setTimeZone(timeZone)

        if (isCurrentLocation != null) {
            builder.isCurrentLocation = isCurrentLocation
        }

        locationPreferencesFlow.value = builder.build()
    }

    override suspend fun updateIsCurrentLocation(isCurrentLocation: Boolean) {
        // Do nothing
    }

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        temperatureUnitFlow.value = unit
    }

    override suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        windSpeedUnitFlow.value = unit
    }

    override suspend fun updatePressureUnit(unit: PressureUnit) {
        pressureUnitFlow.value = unit
    }

    override suspend fun updateTimeFormatUnit(unit: TimeFormatUnit) {
        timeFormatUnitFlow.value = unit
    }

    override suspend fun setFirstTimeRunAppToFalse() {
        isFirstTimeRunApp = false
    }
}
