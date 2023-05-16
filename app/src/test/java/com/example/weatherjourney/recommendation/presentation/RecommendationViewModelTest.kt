package com.example.weatherjourney.recommendation.presentation

import app.cash.turbine.test
import com.example.weatherjourney.R
import com.example.weatherjourney.domain.ConnectivityObserver
import com.example.weatherjourney.fake.FakeConnectivityObserver
import com.example.weatherjourney.features.recommendation.presentation.RecommendationUiState
import com.example.weatherjourney.features.recommendation.presentation.RecommendationViewModel
import com.example.weatherjourney.recommendation.fake.FakeRecommendationRepository
import com.example.weatherjourney.recommendation.recommendations1
import com.example.weatherjourney.recommendation.recommendations2
import com.example.weatherjourney.util.CoroutineRule
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.util.UserMessage
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

class RecommendationViewModelTest {

    private lateinit var recommendationViewModel: RecommendationViewModel
    private lateinit var recommendationRepository: FakeRecommendationRepository
    private lateinit var connectivityObserver: FakeConnectivityObserver

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    val coroutineRule = CoroutineRule()

    @Before
    fun setupViewModel() {
        recommendationRepository = FakeRecommendationRepository()
        connectivityObserver = FakeConnectivityObserver()

        recommendationViewModel = RecommendationViewModel(
            recommendationRepository,
            connectivityObserver,
        )
    }

    @Test
    fun createViewModel_loadingTogglesAndDataLoaded() = runTest {
        launch {
            recommendationViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(this, RecommendationUiState(isLoading = true))
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = true,
                            recommendations = recommendations1,
                        ),
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            recommendations = recommendations1,
                        ),
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

        // Run the initial loading
        runCurrent()
    }

    @Test
    fun createViewModel_loadingTogglesAndFailToLoadData() = runTest {
        launch {
            recommendationViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(this, RecommendationUiState(isLoading = true))
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = true,
                            userMessage = UserMessage(UiText.StringResource(R.string.something_went_wrong)),
                        ),
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            userMessage = UserMessage(UiText.StringResource(R.string.something_went_wrong)),
                        ),
                    )
                }

                // Simulate user message dismissed
                recommendationViewModel.onHandleUserMessageDone()

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            userMessage = null,
                        ),
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

        // Setup and run the initial loading
        recommendationRepository.isSuccessful = false
        runCurrent()
    }

    @Test
    fun createViewModel_loadingTogglesAndFailToLoadDataDueToLackOfInternet() = runTest {
        launch {
            recommendationViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(this, RecommendationUiState(isLoading = true))
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = true,
                            userMessage = UserMessage(UiText.StringResource(R.string.no_internet_connection)),
                        ),
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            userMessage = UserMessage(UiText.StringResource(R.string.no_internet_connection)),
                        ),
                    )
                }

                // Simulate user message dismissed
                recommendationViewModel.onHandleUserMessageDone()

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            userMessage = null,
                        ),
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

        // Setup and run the initial loading
        recommendationRepository.apply {
            isSuccessful = false
            exception = UnknownHostException()
        }
        runCurrent()
    }

    @Test
    fun onRefresh_successfulNewDataLoaded() = runTest {
        createViewModel_loadingTogglesAndDataLoaded()

        launch {
            recommendationViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            recommendations = recommendations1,
                        ),
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = true,
                            recommendations = recommendations2,
                        ),
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            recommendations = recommendations2,
                        ),
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

        // Prepare and run the second loading
        recommendationRepository.recommendations = recommendations2
        recommendationViewModel.onRefresh()
        runCurrent()
    }

    @Test
    fun onRefreshFailedAndDataAlreadyLoaded_keepsPreviousData() = runTest {
        createViewModel_loadingTogglesAndDataLoaded()

        launch {
            recommendationViewModel.uiState.test {
                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            recommendations = recommendations1,
                        ),
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = true,
                            recommendations = recommendations1,
                            userMessage = UserMessage(UiText.StringResource(R.string.something_went_wrong)),
                        ),
                    )
                }

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            recommendations = recommendations1,
                            userMessage = UserMessage(UiText.StringResource(R.string.something_went_wrong)),
                        ),
                    )
                }

                // Simulate user message dismissed
                recommendationViewModel.onHandleUserMessageDone()

                with(awaitItem()) {
                    assertEquals(
                        this,
                        RecommendationUiState(
                            isLoading = false,
                            recommendations = recommendations1,
                            userMessage = null,
                        ),
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

        // Prepare and run the second loading
        recommendationRepository.isSuccessful = false
        recommendationViewModel.onRefresh()
        runCurrent()
    }

    @Test
    fun onRefreshFailedDueToLackOfInternetAndAfterRestoringInternet_loadingTogglesAndDataLoaded() =
        runTest {
            createViewModel_loadingTogglesAndFailToLoadDataDueToLackOfInternet()

            launch {
                recommendationViewModel.uiState.test {
                    with(awaitItem()) {
                        assertEquals(
                            this,
                            RecommendationUiState(
                                isLoading = false,
                                userMessage = null,
                                recommendations = null,
                            ),
                        )
                    }

                    with(awaitItem()) {
                        assertEquals(
                            this,
                            RecommendationUiState(
                                isLoading = true,
                                userMessage = UserMessage(UiText.StringResource(R.string.restore_internet_connection)),
                                recommendations = recommendations1,
                            ),
                        )
                    }

                    with(awaitItem()) {
                        assertEquals(
                            this,
                            RecommendationUiState(
                                isLoading = false,
                                userMessage = UserMessage(UiText.StringResource(R.string.restore_internet_connection)),
                                recommendations = recommendations1,
                            ),
                        )
                    }

                    recommendationViewModel.onHandleUserMessageDone()

                    with(awaitItem()) {
                        assertEquals(
                            this,
                            RecommendationUiState(
                                isLoading = false,
                                userMessage = null,
                                recommendations = recommendations1,
                            ),
                        )
                    }
                }
            }

            recommendationRepository.isSuccessful = true
            connectivityObserver.setStatus(ConnectivityObserver.Status.Available)
            runCurrent()
        }
}
