package com.example.weatherjourney.core.common.util

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.roundTo(n: Int): Double {
    val scale = 10.0.pow(n)
    return (this * scale).roundToInt() / scale
}
