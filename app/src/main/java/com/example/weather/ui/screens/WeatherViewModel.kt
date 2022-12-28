package com.example.weather.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.WeatherRepository
import com.example.weather.model.weather.DailyWeather
import com.example.weather.model.weather.asModel
import com.example.weather.utils.DATE_PATTERN
import com.example.weather.utils.toDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class WeatherUiState(
    val city: String = "",
    val date: String = "",
    val temp: Int = 0,
    val weather: String = "",
    val listDaily: List<DailyWeather> = emptyList()
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()


    fun updateCity(value: String) {
        _uiState.update {
            it.copy(city = value)
        }
    }

    fun getWeather(city: String) {
        viewModelScope.launch {
            val weather = repository.getWeather(city)
            val current = weather.current
            _uiState.update { it ->
                it.copy(
                    date = current.dt.toDateString(DATE_PATTERN),
                    temp = current.temp.roundToInt(),
                    weather = current.weatherItem.first().main,
                    listDaily = weather.daily.map { daily -> daily.asModel(current.dt) }
                )
            }
        }
    }
}
