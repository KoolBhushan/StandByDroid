package com.droidstandby.app.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WorldCity(val name: String, val timezoneId: String, val offsetLabel: String)

@Composable
fun WorldClockWidget(
    now: Date,
    is24Hour: Boolean,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val cities = listOf(
        WorldCity("London", "Europe/London", "GMT"),
        WorldCity("New York", "America/New_York", "-5HRS"),
        WorldCity("Tokyo", "Asia/Tokyo", "+9HRS")
    )

    val timeFormat = if (is24Hour) "HH:mm" else "h:mm a"

    Column(
        modifier = modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        cities.forEach { city ->
            val sdf = SimpleDateFormat(timeFormat, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone(city.timezoneId)
            }
            val formattedTime = sdf.format(now)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = city.name,
                        color = colors.primaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = city.offsetLabel,
                        color = colors.mutedText,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = formattedTime,
                    color = colors.accent,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
