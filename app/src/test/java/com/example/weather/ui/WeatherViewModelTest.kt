package com.example.weather.ui

import app.cash.turbine.test
import com.example.weather.allWeather1
import com.example.weather.fake.data.FakeLocationRepository
import com.example.weather.fake.data.FakeWeatherRepository
import com.example.weather.ui.screens.WeatherViewModel
import com.example.weather.util.CoroutineRule
import com.example.weather.utils.toUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [WeatherViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherRepository: FakeWeatherRepository
    private lateinit var locationRepository: FakeLocationRepository

    @Before
    fun setupViewModel() {
        weatherRepository = FakeWeatherRepository()
        locationRepository = FakeLocationRepository()
        viewModel = WeatherViewModel(weatherRepository, locationRepository)
    }

    @Test
    fun weatherViewModel_ValidCitySearchedAndInternetStable_CityWeatherReceived() = runTest {
        assertFalse(viewModel.uiState.value.isLoading)

        launch {
            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading)
                with(awaitItem()) {
                    assertEquals(
                        this.current,
                        allWeather1.current.toUiModel(allWeather1.timezoneOffset)
                    )
                    assertEquals(
                        this.listDaily,
                        allWeather1.daily.map { it.toUiModel(allWeather1.timezoneOffset) }
                    )
                }
                assertFalse(awaitItem().isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

        viewModel.getAllWeather("")
        runCurrent()
    }
}
