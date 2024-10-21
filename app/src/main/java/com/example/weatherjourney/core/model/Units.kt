package com.example.weatherjourney.core.model

import kotlinx.serialization.Serializable

interface WeatherUnit {
    val label: String
}

@Serializable
enum class PressureUnit(override val label: String) : WeatherUnit {
    Hectopascal("hPa"),
    InchOfMercury("inHg")
}

@Serializable
enum class TemperatureUnit(override val label: String) : WeatherUnit {
    Celsius("°C"),
    Fahrenheit("°F")
}

@Serializable
enum class TimeFormatUnit(override val label: String) : WeatherUnit {
    TwentyFour("24-hour"),
    AmPm("12-hour")
}

@Serializable
enum class WindSpeedUnit(override val label: String) : WeatherUnit {
    KilometerPerHour("km/h"),
    MeterPerSecond("m/s"),
    MilePerHour("mph")
}
