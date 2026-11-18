package com.droidstandby.app.model

import androidx.compose.ui.graphics.Color

data class BatteryInfo(
    val level: Int = 100,
    val isCharging: Boolean = false,
    val chargePlug: String = "AC",
    val temperature: Float = 25.0f
)

data class WeatherInfo(
    val city: String = "Pune",
    val temperature: String = "--",
    val condition: String = "Loading...",
    val weatherCode: Int = 0,
    val highTemp: String = "--",
    val lowTemp: String = "--",
    val humidity: String = "--",
    val aqi: Int? = null,
    val aqiStatus: String = "",
    val isError: Boolean = false
)

enum class NightAccent(val title: String, val primaryColor: Color, val secondaryColor: Color) {
    IOS_RED("Bedside Red", Color(0xFFFF453A), Color(0xFF9E2A2B)),
    AMBER_ORANGE("Warm Amber", Color(0xFFFF9F0A), Color(0xFFB36B00)),
    SOLAR_YELLOW("Solar Warm", Color(0xFFFFD60A), Color(0xFFA68B00)),
    MINT_GREEN("Night Mint", Color(0xFF30D158), Color(0xFF1E8236)),
    GLACIER_BLUE("Ice Blue", Color(0xFF64D2FF), Color(0xFF2A6F8F)),
    DEEP_PURPLE("Neon Violet", Color(0xFFBF5AF2), Color(0xFF692F8A))
}

enum class StandByScreen(val title: String) {
    WIDGETS("Widgets"),
    PHOTOS("Photos"),
    BIG_CLOCKS("Full Clock")
}

enum class BigClockStyle(val title: String) {
    BOLD_DIGITAL("Bold Digital"),
    RETRO_FLIP("Flip Clock"),
    SOLAR_ARC("Solar Arc"),
    ROMAN_ANALOG("Roman Analog"),
    NEON_GLOW("Neon Glow")
}
