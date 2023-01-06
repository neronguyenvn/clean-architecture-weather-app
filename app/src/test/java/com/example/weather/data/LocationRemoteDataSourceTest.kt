package com.example.weather.data

import com.example.weather.network.ApiService
import com.example.weather.util.city1
import com.example.weather.util.coordinate1
import com.example.weather.util.emptyResultForwardGeocodingResponse
import com.example.weather.util.errorResponse
import com.example.weather.util.successfulForwardGeocodingResponse
import com.example.weather.util.successfulReverseGeocodingResponse
import com.example.weather.utils.Result.Error
import com.example.weather.utils.Result.Success
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
@OptIn(ExperimentalSerializationApi::class)
class LocationRemoteDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: ApiService

    private val client = OkHttpClient.Builder().build()
    private val converterFactory = Json.asConverterFactory("application/json".toMediaType())

    @Before
    fun createServer() {
        mockWebServer = MockWebServer()
        apiClient = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun closeServer() {
        mockWebServer.close()
    }

    @Test
    fun locationRemoteDataSource_GetCityWithValidCoordinate_CorrectResponseRetrieved() =
        runTest {
            // Arrange
            val response = MockResponse()
                .setBody(successfulReverseGeocodingResponse)
                .setResponseCode(200)

            mockWebServer.enqueue(response)

            val locationRemoteDataSource = LocationRemoteDataSource(apiClient)
            val expectedCity = city1

            // Act
            val result = locationRemoteDataSource.getCityName(coordinate1)

            // Assert
            assertTrue(result is Success)
            assertEquals((result as Success).data, expectedCity)
        }

    @Test
    fun locationRemoteDataSource_GetCityWithCoordinate_MalformedJsonRetrieved() = runTest {
        // Arrange
        val response = MockResponse()
            .setBody(errorResponse)
            .setResponseCode(200)

        mockWebServer.enqueue(response)

        val locationRemoteDataSource = LocationRemoteDataSource(apiClient)

        // Act
        val result = locationRemoteDataSource.getCityName(coordinate1)

        // Assert
        assertTrue(result is Error)
        assertTrue((result as Error).exception is SerializationException)
    }

    @Test
    fun locationRemoteDataSource_GetCoordinateWithValidCity_CorrectResponseRetrieved() =
        runTest {
            // Arrange
            val response = MockResponse()
                .setBody(successfulForwardGeocodingResponse)
                .setResponseCode(200)

            mockWebServer.enqueue(response)

            val locationRemoteDataSource = LocationRemoteDataSource(apiClient)
            val expectedCoordinate = coordinate1

            // Act
            val result = locationRemoteDataSource.getCoordinate(city1)

            // Assert
            assertTrue(result is Success)
            assertEquals((result as Success).data, expectedCoordinate)
        }

    @Test
    fun locationRemoteDataSource_GetCoordinateWithCity_MalformedJsonRetrieved() = runTest {
        // Arrange
        val response = MockResponse()
            .setBody(errorResponse)
            .setResponseCode(200)

        mockWebServer.enqueue(response)

        val locationRemoteDataSource = LocationRemoteDataSource(apiClient)

        // Act
        val result = locationRemoteDataSource.getCoordinate(city1)

        // Assert
        assertTrue(result is Error)
        assertTrue((result as Error).exception is SerializationException)
    }

    @Test
    fun locationRemoteDataSource_GetCoordinateWithCity_EmptyResultRetrieved() = runTest {
        // Arrange
        val response = MockResponse()
            .setBody(emptyResultForwardGeocodingResponse)
            .setResponseCode(200)

        mockWebServer.enqueue(response)

        val locationRemoteDataSource = LocationRemoteDataSource(apiClient)

        // Act
        val result = locationRemoteDataSource.getCoordinate("ffffffff")

        // Assert
        assertTrue(result is Error)
        assertTrue((result as Error).exception is NoSuchElementException)
    }

    @Test
    fun locationRemoteDataSource_GetCoordinateWithEmptyInput_ErrorRetrieved() = runTest {
        // Arrange
        val response = MockResponse()
            .setBody(errorResponse)
            .setResponseCode(400)

        mockWebServer.enqueue(response)

        val locationRemoteDataSource = LocationRemoteDataSource(apiClient)

        // Act
        val result = locationRemoteDataSource.getCoordinate("")

        // Assert
        assertTrue(result is Error)
        assertTrue((result as Error).exception is HttpException)
    }
}
