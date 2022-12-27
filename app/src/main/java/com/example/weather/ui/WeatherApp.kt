package com.example.weather.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.weather.R
import com.example.weather.ui.screens.HomeScreen

@Composable
fun WeatherApp(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.day_rain),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.Transparent
        ) {
            Column(modifier = Modifier.padding(it)) {
                HomeScreen()
            }
        }
    }
}