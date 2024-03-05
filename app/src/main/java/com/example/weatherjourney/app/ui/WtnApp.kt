package com.example.weatherjourney.app.ui

import androidx.compose.runtime.Composable
import com.example.weatherjourney.app.navigation.WtnNavGraph

@Composable
fun WtnApp(startRoute: String?) {
    startRoute?.let {
        WtnNavGraph(startDestination = startRoute)
    }
}