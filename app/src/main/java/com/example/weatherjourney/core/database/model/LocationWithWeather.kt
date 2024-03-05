package com.example.weatherjourney.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weatherjourney.core.common.constant.HOUR_PATTERN
import com.example.weatherjourney.core.common.util.countPastHoursToday
import com.example.weatherjourney.core.common.util.filterPastHours
import com.example.weatherjourney.core.common.util.getCurrentDate
import com.example.weatherjourney.core.common.util.toDate
import com.example.weatherjourney.core.common.util.toDayNameInWeek
import com.example.weatherjourney.core.model.location.CityWithWeather
import com.example.weatherjourney.core.model.weather.CurrentWeather
import com.example.weatherjourney.core.model.weather.DailyWeather
import com.example.weatherjourney.core.model.weather.HourlyWeather
import com.example.weatherjourney.core.model.weather.Weather
import com.example.weatherjourney.core.model.weather.WeatherType

data class LocationWithWeather(
    @Embedded
    val location: LocationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val hourlyWeather: HourlyWeatherEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val dailyWeather: DailyWeatherEntity
)

val LocationWithWeather.weather: Weather
    get() {
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
            listDaily = dailyWeather.run {
                time.list.mapIndexed { index, time ->
                    DailyWeather(
                        date = time.toDayNameInWeek(location.timeZone),
                        maxTemp = maxTemperatures.list[index],
                        minTemp = minTemperatures.list[index],
                        weatherType = WeatherType.fromWMO(weatherCodes.list[index]),
                    )
                }
            },
            listHourly = hourlyWeather.run {
                time.list.filterPastHours().mapIndexed { index, time ->
                    HourlyWeather(
                        date = time.toDate(location.timeZone, HOUR_PATTERN),
                        temp = temperatures.list[index + count],
                        windSpeed = windSpeeds.list[index + count],
                        weatherType = WeatherType.fromWMO(weatherCodes.list[index + count]),
                    )
                }
            },
        )
    }

fun LocationWithWeather.toCityWithWeather(): CityWithWeather {
    val weather = this.weather
    return CityWithWeather(
        temp = weather.current.temp,
        weatherType = weather.current.weatherType,
        id = location.id,
        cityAddress = location.cityAddress,
        coordinate = location.coordinate,
        timeZone = location.timeZone,
        countryCode = location.countryCode
    )
}