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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.ui.theme.StandByColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BoldDigitalClock(
    now: Date,
    is24Hour: Boolean,
    showSeconds: Boolean,
    battery: BatteryInfo,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val hourFormat = if (is24Hour) "HH" else "hh"
    val hourStr = SimpleDateFormat(hourFormat, Locale.getDefault()).format(now)
    val minStr = SimpleDateFormat("mm", Locale.getDefault()).format(now)
    val secStr = SimpleDateFormat("ss", Locale.getDefault()).format(now)
    val dateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now)

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = hourStr,
                color = colors.primaryText,
                fontSize = 110.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-4).sp
            )

            Text(
                text = ":",
                color = if (colors.isNightMode) colors.accent else androidx.compose.ui.graphics.Color(0x99FFFFFF),
                fontSize = 90.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Text(
                text = minStr,
                color = colors.primaryText,
                fontSize = 110.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-4).sp
            )

            if (showSeconds) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = secStr,
                    color = colors.secondaryText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = dateStr,
            color = colors.secondaryText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (battery.isCharging) "⚡ ${battery.level}% CHARGING" else "${battery.level}% BATTERY",
            color = if (battery.isCharging) (if (colors.isNightMode) colors.accent else androidx.compose.ui.graphics.Color(0xFF34C759)) else colors.mutedText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}
