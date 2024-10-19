package com.example.weatherjourney.core.model

enum class PressureUnit(val label: String) {
    HECTOPASCAL("hPa"),
    INCH_OF_MERCURY("inHg"),
}

enum class TemperatureUnit(val label: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
}

enum class TimeFormatUnit(val label: String) {
    TWENTY_FOUR("24-hour"),
    AM_PM("12-hour"),
}

enum class WindSpeedUnit(val label: String) {
    KILOMETER_PER_HOUR("km/h"),
    METER_PER_SECOND("m/s"),
    MILE_PER_HOUR("mph"),
}
