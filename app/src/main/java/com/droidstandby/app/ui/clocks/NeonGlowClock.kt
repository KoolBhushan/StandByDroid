package com.droidstandby.app.ui.clocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NeonGlowClock(
    now: Date,
    is24Hour: Boolean,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val hourFormat = if (is24Hour) "HH" else "hh"
    val hourStr = SimpleDateFormat(hourFormat, Locale.getDefault()).format(now)
    val minStr = SimpleDateFormat("mm", Locale.getDefault()).format(now)
    val secStr = SimpleDateFormat("ss", Locale.getDefault()).format(now)

    val neonColor = colors.accent

    val neonStyle = TextStyle(
        color = neonColor,
        fontSize = 110.sp,
        fontWeight = FontWeight.Light,
        fontFamily = FontFamily.Monospace,
        shadow = Shadow(
            color = neonColor.copy(alpha = 0.8f),
            offset = Offset(0f, 0f),
            blurRadius = 32f
        )
    )

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = hourStr, style = neonStyle)
            Text(text = ":", style = neonStyle, modifier = Modifier.padding(horizontal = 4.dp))
            Text(text = minStr, style = neonStyle)

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = secStr,
                style = TextStyle(
                    color = neonColor.copy(alpha = 0.8f),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                    shadow = Shadow(
                        color = neonColor.copy(alpha = 0.6f),
                        blurRadius = 16f
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "NEON BEDSIDE",
            color = colors.secondaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )
    }
}
