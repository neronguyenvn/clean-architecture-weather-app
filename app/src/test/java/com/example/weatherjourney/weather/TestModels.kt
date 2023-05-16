package com.example.weatherjourney.weather

import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.features.weather.data.remote.dto.DailyWeatherDto
import com.example.weatherjourney.features.weather.data.remote.dto.HourlyWeatherDto
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.features.weather.domain.model.unit.PressureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TimeFormatUnit
import com.example.weatherjourney.features.weather.domain.model.unit.WindSpeedUnit
import java.time.Duration
import java.time.Instant

val now: Instant = Instant.now()
val timeList = listOf(
    now.epochSecond,
    now.plus(Duration.ofHours(1)).epochSecond,
    now.plus(Duration.ofHours(2)).epochSecond,
)

val allWeatherDto1 = AllWeatherDto(
    HourlyWeatherDto(
        timeList,
        listOf(12.3, 13.2, 14.1),
        listOf(800, 801, 802),
        listOf(1013.5, 1014.0, 1014.5),
        listOf(2.0, 2.5, 3.0),
        listOf(75.0, 80.0, 85.0),
    ),
    DailyWeatherDto(
        timeList,
        listOf(800, 801, 802),
        listOf(16.5, 17.8, 18.2),
        listOf(10.2, 10.5, 11.1),
    ),
)

val allWeatherDto2 = AllWeatherDto(
    HourlyWeatherDto(
        timeList,
        listOf(17.6, 18.1, 18.7),
        listOf(500, 501, 502),
        listOf(1012.2, 1012.5, 1012.7),
        listOf(3.0, 3.5, 4.0),
        listOf(70.0, 75.0, 80.0),
    ),
    DailyWeatherDto(
        timeList,
        listOf(501, 502, 503),
        listOf(20.1, 21.2, 19.5),
        listOf(8.7, 9.5, 7.9),
    ),
)

val allUnit1 = AllUnit(
    temperature = TemperatureUnit.CELSIUS,
    windSpeed = WindSpeedUnit.KILOMETER_PER_HOUR,
    pressure = PressureUnit.HECTOPASCAL,
    timeFormat = TimeFormatUnit.TWENTY_FOUR,
)

val location1 = LocationEntity(
    cityAddress = "New York",
    latitude = 40.7128,
    longitude = -74.0060,
    timeZone = "America/New_York",
    countryCode = "US",
    isCurrentLocation = true,
)

val location2 = LocationEntity(
    cityAddress = "City 2",
    latitude = 30.0,
    longitude = 30.0,
    timeZone = "America/New_York",
    countryCode = "US",
    isCurrentLocation = false,
)

val coordinate1 = Coordinate(40.7128, -74.0060)
val coordinate2 = Coordinate(30.0, 30.0)
