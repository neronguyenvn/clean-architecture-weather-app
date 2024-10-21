package com.example.weatherjourney.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weatherjourney.core.common.util.TimeUtils
import com.example.weatherjourney.core.common.util.countPastHoursToday
import com.example.weatherjourney.core.common.util.filterPastHours
import com.example.weatherjourney.core.common.util.getCurrentDate
import com.example.weatherjourney.core.common.util.toDate
import com.example.weatherjourney.core.common.util.toDayNameInWeek
import com.example.weatherjourney.core.model.WeatherType
import com.example.weatherjourney.core.model.CurrentWeather
import com.example.weatherjourney.core.model.DailyWeather
import com.example.weatherjourney.core.model.HourlyWeather
import com.example.weatherjourney.core.model.Weather
import com.example.weatherjourney.core.model.LocationWithWeather

data class LocationEntityWithWeather(
    @Embedded
    val location: LocationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val hourlyWeather: HourlyWeatherEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val dailyWeather: DailyWeatherEntity?
)

val LocationEntityWithWeather.weather: Weather?
    get() {
        if (hourlyWeather == null || dailyWeather == null) return null
        val count = hourlyWeather.time.list.countPastHoursToday()
        return Weather(
            current = hourlyWeather.run {
                CurrentWeather(
                    date = getCurrentDate(location.timeZone),
                    temp = temperatures.list[count],
                    windSpeed = windSpeeds.list[count],
                    humidity = humidities.list[count],
                    pressure = pressures.list[count],
                    weatherType = WeatherType.fromWMO(weatherCodes.list[count]),
                )
            },
            dailyForecasts = dailyWeather.run {
                time.list.mapIndexed { index, time ->
                    DailyWeather(
                        date = time.toDayNameInWeek(location.timeZone),
                        maxTemp = maxTemperatures.list[index],
                        minTemp = minTemperatures.list[index],
                        weatherType = WeatherType.fromWMO(weatherCodes.list[index]),
                    )
                }
            },
            hourlyForecasts = hourlyWeather.run {
                time.list.filterPastHours().mapIndexed { index, time ->
                    HourlyWeather(
                        date = time.toDate(location.timeZone, TimeUtils.HOUR_PATTERN),
                        temp = temperatures.list[index + count],
                        windSpeed = windSpeeds.list[index + count],
                        weatherType = WeatherType.fromWMO(weatherCodes.list[index + count]),
                    )
                }
            },
        )
    }

fun LocationEntityWithWeather.asExternalModel(): LocationWithWeather = LocationWithWeather(
    location = location.asExternalModel(),
    weather = weather,
)
