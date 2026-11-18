package com.droidstandby.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.droidstandby.app.model.NightAccent

val PureBlack = Color(0xFF000000)
val DarkBackground = Color(0xFF000000)
val CardBackground = Color.Transparent
val CardBorder = Color.Transparent

val WhitePrimary = Color(0xFFFFFFFF)
val WhiteSecondary = Color(0xB3FFFFFF)
val WhiteMuted = Color(0x66FFFFFF)

val ChargingGreen = Color(0xFF34C759)
val BatteryRingBg = Color(0x2E34C759)

data class StandByColors(
    val background: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val mutedText: Color,
    val accent: Color,
    val isNightMode: Boolean
)

@Composable
fun getStandByColors(isNightMode: Boolean, nightAccent: NightAccent): StandByColors {
    return if (isNightMode) {
        StandByColors(
            background = PureBlack,
            cardBackground = Color.Transparent,
            cardBorder = Color.Transparent,
            primaryText = nightAccent.primaryColor,
            secondaryText = nightAccent.secondaryColor,
            mutedText = nightAccent.secondaryColor.copy(alpha = 0.6f),
            accent = nightAccent.primaryColor,
            isNightMode = true
        )
    } else {
        StandByColors(
            background = PureBlack,
            cardBackground = Color.Transparent,
            cardBorder = Color.Transparent,
            primaryText = WhitePrimary,
            secondaryText = WhiteSecondary,
            mutedText = WhiteMuted,
            accent = Color(0xFF0A84FF),
            isNightMode = false
        )
    }
}

@Composable
fun DroidStandByTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = PureBlack,
            surface = DarkBackground
        ),
        content = content
    )
}
