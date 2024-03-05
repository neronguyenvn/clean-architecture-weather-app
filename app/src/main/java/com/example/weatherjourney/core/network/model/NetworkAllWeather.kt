package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.common.constant.HOUR_PATTERN
import com.example.weatherjourney.core.common.util.countPastHoursToday
import com.example.weatherjourney.core.common.util.filterPastHours
import com.example.weatherjourney.core.common.util.getCurrentDate
import com.example.weatherjourney.core.common.util.toDate
import com.example.weatherjourney.core.common.util.toDayNameInWeek
import com.example.weatherjourney.core.model.weather.AllWeather
import com.example.weatherjourney.core.model.weather.CurrentWeather
import com.example.weatherjourney.core.model.weather.DailyWeather
import com.example.weatherjourney.core.model.weather.HourlyWeather
import com.example.weatherjourney.core.model.weather.WeatherType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkAllWeather(
    val hourly: HourlyWeatherDto,
    val daily: DailyWeatherDto,
)

@Serializable
data class HourlyWeatherDto(
    val time: List<Long>,
    @SerialName("temperature_2m")
    val temperatures: List<Double>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("pressure_msl")
    val pressures: List<Double>,
    @SerialName("windspeed_10m")
    val windSpeeds: List<Double>,
    @SerialName("relativehumidity_2m")
    val humidities: List<Double>,
)

@Serializable
data class DailyWeatherDto(
    val time: List<Long>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("temperature_2m_max")
    val maxTemperatures: List<Double>,
    @SerialName("temperature_2m_min")
    val minTemperatures: List<Double>,
)

fun NetworkAllWeather.toAllWeather(timeZone: String): AllWeather {
    val count = this.hourly.time.countPastHoursToday()

    return AllWeather(
        current = this.hourly.run {
            CurrentWeather(
                date = getCurrentDate(timeZone),
                temp = temperatures[count],
                windSpeed = windSpeeds[count],
                humidity = humidities[count],
                pressure = pressures[count],
                weatherType = WeatherType.fromWMO(weatherCodes[count]),
            )
        },
        listDaily = this.daily.run {
            time.mapIndexed { index, time ->
                DailyWeather(
                    date = time.toDayNameInWeek(timeZone),
                    maxTemp = maxTemperatures[index],
                    minTemp = minTemperatures[index],
                    weatherType = WeatherType.fromWMO(weatherCodes[index]),
                )
            }
        },
        listHourly = this.hourly.run {
            time.filterPastHours().mapIndexed { index, time ->
                HourlyWeather(
                    date = time.toDate(timeZone, HOUR_PATTERN),
                    temp = temperatures[index + count],
                    windSpeed = windSpeeds[index + count],
                    weatherType = WeatherType.fromWMO(weatherCodes[index + count]),
                )
            }
        },
    )
}
