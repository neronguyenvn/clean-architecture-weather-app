package com.example.weatherjourney.weather.fake

/*class FakeWeatherRepository : WeatherRepository {

    var haveInternet = true
    var allWeatherDto: NetworkWeather = allWeatherDto1

    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<NetworkWeather> {
        return if (haveInternet) {
            Result.Success(allWeatherDto)
        } else {
            Result.Error(UnknownHostException())
        }
    }
}*/
