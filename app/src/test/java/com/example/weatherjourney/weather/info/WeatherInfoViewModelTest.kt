/*
package com.example.weatherjourney.weather.info

import app.cash.turbine.test
import com.example.weatherjourney.R
import com.example.weatherjourney.core.data.NetworkMonitor
import com.example.weatherjourney.fake.FakeNetworkMonitor
import com.example.weatherjourney.fake.FakePreferences
import com.example.weatherjourney.features.weather.data.mapper.coordinate
import com.example.weatherjourney.features.weather.data.mapper.toAllWeather
import com.example.weatherjourney.core.model.unit.PressureUnit
import com.example.weatherjourney.core.model.unit.TemperatureUnit
import com.example.weatherjourney.core.model.unit.TimeFormatUnit
import com.example.weatherjourney.core.model.unit.WindSpeedUnit
import com.example.weatherjourney.core.domain.ValidateCurrentLocationUseCase
import com.example.weatherjourney.core.domain.ConvertUnitUseCase
import com.example.weatherjourney.features.weather.info.WeatherInfoUiState
import com.example.weatherjourney.features.weather.info.WeatherInfoViewModel
import com.example.weatherjourney.app.navigation.WtnDestinations
import com.example.weatherjourney.util.CoroutineRule
import com.example.weatherjourney.core.common.util.UiText
import com.example.weatherjourney.core.common.util.UserMessage
import com.example.weatherjourney.weather.allUnit1
import com.example.weatherjourney.weather.allWeatherDto1
import com.example.weatherjourney.weather.allWeatherDto2
import com.example.weatherjourney.weather.coordinate2
import com.example.weatherjourney.weather.fake.FakeLocationRepository
import com.example.weatherjourney.weather.fake.FakeWeatherRepository
import com.example.weatherjourney.weather.location1
import com.example.weatherjourney.weather.location2
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("LargeClass")
class WeatherInfoViewModelTest {

    private lateinit var weatherInfoViewModel: WeatherInfoViewModel
    private lateinit var connectivityObserver: FakeNetworkMonitor
    private lateinit var preferences: FakePreferences
    private lateinit var locationRepository: FakeLocationRepository
    private lateinit var weatherRepository: FakeWeatherRepository
    private lateinit var weatherUseCase: WeatherUseCases
    private lateinit var locationUseCases: LocationUseCases

    @get:Rule
    val coroutineRule = CoroutineRule()

    @Before
    fun setupViewModel() {
        preferences = FakePreferences()
        connectivityObserver = FakeNetworkMonitor()
        locationRepository = FakeLocationRepository()
        weatherRepository = FakeWeatherRepository()

        weatherUseCase = WeatherUseCases(
            getAllWeather = GetAllWeather(weatherRepository),
            convertUnit = ConvertUnitUseCase(),
        )

        locationUseCases = LocationUseCases(
            ValidateCurrentLocationUseCase(locationRepository, preferences),
        )

        weatherInfoViewModel = WeatherInfoViewModel(
            appPreferences = preferences,
            networkMonitor = connectivityObserver,
            locationUseCases = locationUseCases,
            weatherUseCases = weatherUseCase,
            locationRepository = locationRepository,
        )
    }

    @Test
    fun firstTimeRunApp_viewModelInitializedProperly() = runTest {
        weatherInfoViewModel.apply {
            assertTrue(isInitializing.value)
            assertEquals(WtnDestinations.INFO_ROUTE, appRoute)
            assertEquals(WeatherInfoUiState(isLoading = true), uiState.value)
            assertTrue(isFirstTimeRunApp())
        }
    }

    @Test
    fun firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded() = runTest {
        // Act
        weatherInfoViewModel.onFirstTimeLocationPermissionResult(true)
        // Assert
        weatherInfoViewModel.uiState.test {
            with(awaitItem()) {
                assertEquals(WeatherInfoUiState(isLoading = true), this)
            }

            with(awaitItem()) {
                assertFalse(weatherInfoViewModel.isInitializing.value)
                assertEquals(
                    WeatherInfoUiState(
                        isLoading = true,
                        allUnit = allUnit1,
                        isCurrentLocation = true,
                        cityAddress = location1.cityAddress,
                        allWeather = allWeatherDto1.toAllWeather(
                            location1.timeZone,
                        ).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            with(awaitItem()) {
                assertFalse(this.isLoading)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun firstTimeRunAppAndDenyLocationPermission_navigateToSearch() {
        weatherInfoViewModel.apply {
            // Act
            onFirstTimeLocationPermissionResult(false)
            // Assert
            assertEquals(appRoute, WtnDestinations.SEARCH_ROUTE)
            assertEquals(isInitializing.value, false)
        }
    }

    @Test
    fun firstTimeRunAppAndGrantLocationPermissionButLocationServiceDisabled_navigateToSearch() =
        runTest {
            // Arrange
            locationRepository.setIsGpsEnabled(false)
            // Act
            weatherInfoViewModel.onFirstTimeLocationPermissionResult(true)
            runCurrent()
            // Assert
            assertEquals(WtnDestinations.SEARCH_ROUTE, weatherInfoViewModel.appRoute)
            assertFalse(weatherInfoViewModel.isInitializing.value)
        }

    @Test
    fun firstTimeRunAppAndGrantLocationPermissionButNoInternet_showErrorMessage() = runTest {
        // Arrange
        locationRepository.setHasInternet(false)
        // Act
        weatherInfoViewModel.onFirstTimeLocationPermissionResult(true)
        // Assert
        weatherInfoViewModel.uiState.test {
            with(awaitItem()) {
                assertEquals(WeatherInfoUiState(isLoading = true), this)
            }

            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        isLoading = false,
                        userMessage = UserMessage(UiText.StringResource(R.string.no_internet_connection)),
                        allUnit = allUnit1,
                    ),
                    this,
                )

                assertFalse(weatherInfoViewModel.isInitializing.value)
            }

            weatherInfoViewModel.onHandleUserMessageDone()

            with(awaitItem()) {
                assert(this.userMessage == null)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Suppress("LongMethod")
    @Test
    fun firstTimeRunAppAndGrantLocationPermissionButNoInternetAndAfterRestoringInternet_loadingTogglesAndDataLoaded() =
        runTest {
            firstTimeRunAppAndGrantLocationPermissionButNoInternet_showErrorMessage()
            // Arrange
            locationRepository.setHasInternet(true)
            // Act
            connectivityObserver.setStatus(NetworkMonitor.Status.Available)
            // Assert
            weatherInfoViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(WeatherInfoUiState(allUnit = allUnit1), this)
                }

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = true,
                            userMessage = UserMessage(UiText.StringResource(R.string.restore_internet_connection)),
                            allUnit = allUnit1,
                            isCurrentLocation = true
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = true,
                            allUnit = allUnit1,
                            userMessage = UserMessage(UiText.StringResource(R.string.restore_internet_connection)),
                            isCurrentLocation = true,
                            cityAddress = location1.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                location1.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertFalse(this.isLoading)
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun runAppAndFirstTimeDataLoaded_loadingTogglesAndDataLoaded() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Arrange
        val recreateViewModelJob = launch {
            weatherInfoViewModel.onCleared()
            weatherInfoViewModel = WeatherInfoViewModel(
                appPreferences = preferences,
                networkMonitor = connectivityObserver,
                locationUseCases = locationUseCases,
                weatherUseCases = weatherUseCase,
                locationRepository = locationRepository,
            )
        }
        recreateViewModelJob.join()
        // Act
        weatherInfoViewModel.onRefresh()
        // Assert
        assertFalse(weatherInfoViewModel.isFirstTimeRunApp())
        weatherInfoViewModel.uiState.test {
            with(awaitItem()) {
                assertEquals(this, WeatherInfoUiState(isLoading = true))
            }

            with(awaitItem()) {
                assertFalse(weatherInfoViewModel.isInitializing.value)
                assertEquals(
                    WeatherInfoUiState(
                        isLoading = true,
                        allUnit = allUnit1,
                        isCurrentLocation = true,
                        cityAddress = location1.cityAddress,
                        allWeather = allWeatherDto1.toAllWeather(
                            location1.timeZone,
                        ).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            with(awaitItem()) {
                assertFalse(this.isLoading)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun runAppAndFirstTimeRunDataLoadedAndNoInternet_showErrorMessage() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Arrange
        val recreateViewModelJob = launch {
            weatherInfoViewModel.onCleared()
            weatherInfoViewModel = WeatherInfoViewModel(
                appPreferences = preferences,
                networkMonitor = connectivityObserver,
                locationUseCases = locationUseCases,
                weatherUseCases = weatherUseCase,
                locationRepository = locationRepository,
            )
        }
        recreateViewModelJob.join()
        weatherRepository.haveInternet = false
        // Act
        weatherInfoViewModel.onRefresh()
        // Assert
        assertFalse(weatherInfoViewModel.isFirstTimeRunApp())
        weatherInfoViewModel.uiState.test {
            with(awaitItem()) {
                assertEquals(WeatherInfoUiState(isLoading = true), this)
            }

            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        isLoading = false,
                        userMessage = UserMessage(UiText.StringResource(R.string.no_internet_connection)),
                        allUnit = allUnit1,
                        isCurrentLocation = location1.isCurrentLocation,
                        cityAddress = location1.cityAddress,
                    ),
                    this,
                )

                assertFalse(weatherInfoViewModel.isInitializing.value)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun runAppAndFirstTimeRunDataNotLoaded_navigateToSearch() = runTest {
        firstTimeRunAppAndGrantLocationPermissionButNoInternet_showErrorMessage()
        // Arrange
        val recreateViewModelJob = launch {
            weatherInfoViewModel.onCleared()
            weatherInfoViewModel = WeatherInfoViewModel(
                appPreferences = preferences,
                networkMonitor = connectivityObserver,
                locationUseCases = locationUseCases,
                weatherUseCases = weatherUseCase,
                locationRepository = locationRepository,
            )
        }
        recreateViewModelJob.join()
        // Act
        weatherInfoViewModel.onRefresh()
        runCurrent()
        // Assert
        assertFalse(weatherInfoViewModel.isFirstTimeRunApp())
        assertFalse(weatherInfoViewModel.isInitializing.value)
        assertEquals(WtnDestinations.SEARCH_ROUTE, weatherInfoViewModel.appRoute)
    }

    @Test
    fun onRefresh_loadingNewDataSuccess() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Arrange
        weatherRepository.allWeatherDto = allWeatherDto2
        // Act
        weatherInfoViewModel.onRefresh()
        // Assert
        weatherInfoViewModel.uiState.test {
            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        allUnit = allUnit1,
                        isCurrentLocation = location1.isCurrentLocation,
                        cityAddress = location1.cityAddress,
                        allWeather = allWeatherDto1.toAllWeather(
                            location1.timeZone,
                        ).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        isLoading = true,
                        allUnit = allUnit1,
                        isCurrentLocation = location1.isCurrentLocation,
                        cityAddress = location1.cityAddress,
                        allWeather = allWeatherDto2.toAllWeather(
                            location1.timeZone,
                        ).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            with(awaitItem()) {
                assertFalse(this.isLoading)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onRefreshFailedDueToLackOfInternetAndDataLoaded_keepsPreviousData() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Arrange
        weatherRepository.haveInternet = false
        // Act
        weatherInfoViewModel.onRefresh()
        // Assert
        weatherInfoViewModel.uiState.test {
            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        allUnit = allUnit1,
                        isCurrentLocation = true,
                        cityAddress = location1.cityAddress,
                        allWeather = allWeatherDto1.toAllWeather(
                            location1.timeZone,
                        ).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        userMessage = UserMessage(UiText.StringResource(R.string.no_internet_connection)),
                        allUnit = allUnit1,
                        isCurrentLocation = true,
                        cityAddress = location1.cityAddress,
                        allWeather = allWeatherDto1.toAllWeather(
                            location1.timeZone,
                        ).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changeTemperatureUnit_uiStateUpdated() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Assert
        launch {
            weatherInfoViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }

                val expectedAllUnit = allUnit1.copy(temperature = TemperatureUnit.FAHRENHEIT)

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = expectedAllUnit,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, expectedAllUnit)
                            },
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }
            }
        }
        // Act
        preferences.updateTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        runCurrent()
        preferences.updateTemperatureUnit(TemperatureUnit.CELSIUS)
    }

    @Suppress("LongMethod")
    @Test
    fun changeWindSpeedUnit_uiStateUpdated() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Assert
        launch {
            weatherInfoViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }

                var expectedAllUnit = allUnit1.copy(windSpeed = WindSpeedUnit.METER_PER_SECOND)
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = expectedAllUnit,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, expectedAllUnit)
                            },
                        ),
                        this,
                    )
                }

                expectedAllUnit = allUnit1.copy(windSpeed = WindSpeedUnit.MILE_PER_HOUR)
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = expectedAllUnit,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, expectedAllUnit)
                            },
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }
            }
        }
        // Act
        preferences.updateWindSpeedUnit(WindSpeedUnit.METER_PER_SECOND)
        runCurrent()
        preferences.updateWindSpeedUnit(WindSpeedUnit.MILE_PER_HOUR)
        runCurrent()
        preferences.updateWindSpeedUnit(WindSpeedUnit.KILOMETER_PER_HOUR)
    }

    @Test
    fun changePressureUnit_uiStateUpdated() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Assert
        launch {
            weatherInfoViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }

                val expectedAllUnit = allUnit1.copy(pressure = PressureUnit.INCH_OF_MERCURY)
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = expectedAllUnit,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, expectedAllUnit)
                            },
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }
            }
        }
        // Act
        preferences.updatePressureUnit(PressureUnit.INCH_OF_MERCURY)
        runCurrent()
        preferences.updatePressureUnit(PressureUnit.HECTOPASCAL)
    }

    @Test
    fun changeTimeFormat_uiStateUpdated() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Assert
        launch {
            weatherInfoViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }

                val expectedAllUnit = allUnit1.copy(timeFormat = TimeFormatUnit.AM_PM)
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = expectedAllUnit,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, expectedAllUnit)
                            },
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = false,
                            allUnit = allUnit1,
                            isCurrentLocation = true,
                            cityAddress = preferences.locationPreferencesFlow.value.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                preferences.locationPreferencesFlow.value.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }

            }
        }
        // Act
        preferences.updateTimeFormatUnit(TimeFormatUnit.AM_PM)
        runCurrent()
        preferences.updateTimeFormatUnit(TimeFormatUnit.TWENTY_FOUR)
    }

    @Test
    fun navigateFromSearchWthNotSavedSuggestionCityAndSave_dataLoadedAndSavedSuccessfully() =
        runTest {
            firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
            // Arrange
            preferences.updateLocation(
                cityAddress = location2.cityAddress,
                coordinate = location2.coordinate,
                timeZone = location2.timeZone,
                isCurrentLocation = location2.isCurrentLocation
            )
            weatherRepository.allWeatherDto = allWeatherDto2
            // Act
            weatherInfoViewModel.onNavigateFromSearch(coordinate2)
            // Assert
            weatherInfoViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            allUnit = allUnit1,
                            isCurrentLocation = location1.isCurrentLocation,
                            cityAddress = location1.cityAddress,
                            allWeather = allWeatherDto1.toAllWeather(
                                location1.timeZone,
                            ).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = true,
                            allUnit = allUnit1,
                            isCurrentLocation = location2.isCurrentLocation,
                            cityAddress = location2.cityAddress,
                            allWeather = allWeatherDto2.toAllWeather(location2.timeZone).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                            userMessage = UserMessage.AddingLocation
                        ),
                        this,
                    )
                }

                // Act
                weatherInfoViewModel.onSaveInfo(location2.countryCode)

                with(awaitItem()) {
                    assertFalse(weatherInfoViewModel.isInitializing.value)
                    assertEquals(
                        WeatherInfoUiState(
                            isLoading = true,
                            allUnit = allUnit1,
                            isCurrentLocation = location2.isCurrentLocation,
                            cityAddress = location2.cityAddress,
                            allWeather = allWeatherDto2.toAllWeather(location2.timeZone).let {
                                weatherUseCase.convertUnit(it, allUnit1)
                            },
                            userMessage = UserMessage(UiText.StringResource(R.string.location_saved))
                        ),
                        this,
                    )
                }

                with(awaitItem()) {
                    assertFalse(this.isLoading)
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun navigateFromSearchWithNonCurrentLocationSavedCity_dataLoadedAndNoSaveButton() = runTest {
        firstTimeRunAppAndGrantLocationPermission_loadingTogglesAndDataLoaded()
        // Arrange
        preferences.updateLocation(
            cityAddress = location2.cityAddress,
            coordinate = location2.coordinate,
            timeZone = location2.timeZone,
            isCurrentLocation = location2.isCurrentLocation
        )
        locationRepository.saveLocation(location2)
        weatherRepository.allWeatherDto = allWeatherDto2
        // Act
        weatherInfoViewModel.onNavigateFromSearch(coordinate2)
        // Assert
        weatherInfoViewModel.uiState.test {
            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        allUnit = allUnit1,
                        isCurrentLocation = true,
                        cityAddress = location1.cityAddress,
                        allWeather = allWeatherDto1.toAllWeather(location1.timeZone).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            with(awaitItem()) {
                assertEquals(
                    WeatherInfoUiState(
                        isLoading = true,
                        allUnit = allUnit1,
                        isCurrentLocation = false,
                        cityAddress = location2.cityAddress,
                        allWeather = allWeatherDto2.toAllWeather(
                            location2.timeZone,
                        ).let {
                            weatherUseCase.convertUnit(it, allUnit1)
                        },
                    ),
                    this,
                )
            }

            with(awaitItem()) {
                assertFalse(this.isLoading)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}
*/
