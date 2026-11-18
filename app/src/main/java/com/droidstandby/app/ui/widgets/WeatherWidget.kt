package com.droidstandby.app.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.model.WeatherInfo
import com.droidstandby.app.ui.theme.StandByColors

@Composable
fun CompactWeatherWidget(
    weather: WeatherInfo,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val icon = getWeatherIcon(weather.weatherCode)

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Location row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = if (colors.isNightMode) colors.accent else Color(0xFFFF453A),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = weather.city,
                color = colors.primaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Icon + Large Temp + Condition
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = weather.condition,
                tint = if (colors.isNightMode) colors.accent else Color(0xFFFFCC00),
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = weather.temperature,
                color = colors.primaryText,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = weather.condition,
                    color = colors.secondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (weather.highTemp.isNotEmpty() || weather.lowTemp.isNotEmpty()) {
                    Text(
                        text = "${weather.highTemp} ${weather.lowTemp}",
                        color = colors.mutedText,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // AQI & Humidity row
        if (weather.aqi != null || (weather.humidity.isNotEmpty() && weather.humidity != "--%")) {
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (weather.aqi != null) {
                    val aqiColor = if (colors.isNightMode) colors.accent else getAqiColor(weather.aqi)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(aqiColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AQI ${weather.aqi} • ${weather.aqiStatus}",
                            color = aqiColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (weather.humidity.isNotEmpty() && weather.humidity != "--%") {
                    Text(
                        text = "💧 ${weather.humidity}",
                        color = colors.secondaryText,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherWidget(
    weather: WeatherInfo,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val icon = getWeatherIcon(weather.weatherCode)

    Column(
        modifier = modifier.fillMaxSize().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = weather.condition,
                tint = if (colors.isNightMode) colors.accent else Color(0xFFFFCC00),
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = weather.temperature,
                color = colors.primaryText,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = if (colors.isNightMode) colors.accent else Color(0xFFFF453A),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = weather.city,
                color = colors.primaryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = weather.condition,
            color = colors.secondaryText,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (weather.highTemp.isNotEmpty()) {
                Text(text = weather.highTemp, color = colors.secondaryText, fontSize = 11.sp)
            }
            if (weather.lowTemp.isNotEmpty()) {
                Text(text = weather.lowTemp, color = colors.secondaryText, fontSize = 11.sp)
            }
            if (weather.humidity.isNotEmpty() && weather.humidity != "--%") {
                Text(text = "💧 ${weather.humidity}", color = colors.secondaryText, fontSize = 11.sp)
            }
        }

        if (weather.aqi != null) {
            Spacer(modifier = Modifier.height(6.dp))
            val aqiColor = if (colors.isNightMode) colors.accent else getAqiColor(weather.aqi)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(aqiColor.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "AQI ${weather.aqi} • ${weather.aqiStatus}",
                    color = aqiColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getAqiColor(aqi: Int): Color = when {
    aqi <= 50 -> Color(0xFF34C759)   // Good (Green)
    aqi <= 100 -> Color(0xFFFFD60A)  // Moderate (Yellow)
    aqi <= 150 -> Color(0xFFFF9F0A)  // Sensitive (Orange)
    aqi <= 200 -> Color(0xFFFF453A)  // Unhealthy (Red)
    aqi <= 300 -> Color(0xFFBF5AF2)  // Very Unhealthy (Purple)
    else -> Color(0xFF8E0000)        // Hazardous (Maroon)
}

private fun getWeatherIcon(code: Int): ImageVector = when (code) {
    0 -> Icons.Default.WbSunny
    1, 2, 3 -> Icons.Default.Cloud
    45, 48 -> Icons.Default.Cloud
    51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Default.WaterDrop
    71, 73, 75, 77, 85, 86 -> Icons.Default.WaterDrop
    95, 96, 99 -> Icons.Default.Thunderstorm
    else -> Icons.Default.WbSunny
}
