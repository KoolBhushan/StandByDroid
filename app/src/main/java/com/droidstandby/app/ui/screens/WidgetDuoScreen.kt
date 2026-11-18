package com.droidstandby.app.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.model.WeatherInfo
import com.droidstandby.app.ui.theme.StandByColors
import com.droidstandby.app.ui.widgets.AnalogClockWidget
import com.droidstandby.app.ui.widgets.BatteryWidget
import com.droidstandby.app.ui.widgets.CalendarWidget
import com.droidstandby.app.ui.widgets.CompactWeatherWidget
import com.droidstandby.app.ui.widgets.DigitalClockWidget
import com.droidstandby.app.ui.widgets.MusicPlayerWidget
import com.droidstandby.app.ui.widgets.PhotoWidget
import com.droidstandby.app.ui.widgets.WorldClockWidget
import java.util.Date

@Composable
fun WidgetDuoScreen(
    leftPagerState: PagerState,
    rightPagerState: PagerState,
    now: Date,
    is24Hour: Boolean,
    showSeconds: Boolean,
    battery: BatteryInfo,
    weather: WeatherInfo,
    photoUri: Uri?,
    colors: StandByColors,
    onPickPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Left Smart Stack: Full Analog Clock (Default)
        Box(
            modifier = Modifier
                .weight(1.05f)
                .fillMaxHeight()
        ) {
            VerticalPager(
                state = leftPagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> AnalogClockWidget(now = now, colors = colors)
                    1 -> DigitalClockWidget(
                        now = now,
                        is24Hour = is24Hour,
                        showSeconds = showSeconds,
                        colors = colors
                    )
                    2 -> BatteryWidget(battery = battery, colors = colors)
                    3 -> WorldClockWidget(
                        now = now,
                        is24Hour = is24Hour,
                        colors = colors
                    )
                }
            }

            VerticalPagerIndicator(
                pageCount = leftPagerState.pageCount,
                currentPage = leftPagerState.currentPage,
                activeColor = colors.accent,
                inactiveColor = colors.mutedText,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
            )
        }

        // 2. Right Stack: Weather on top of Calendar (Default)
        Box(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight()
        ) {
            VerticalPager(
                state = rightPagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> WeatherOnCalendarCard(now = now, weather = weather, colors = colors)
                    1 -> MusicPlayerWidget(colors = colors)
                    2 -> PhotoWidget(
                        photoUri = photoUri,
                        colors = colors,
                        onPickPhotoClick = onPickPhotoClick
                    )
                }
            }

            VerticalPagerIndicator(
                pageCount = rightPagerState.pageCount,
                currentPage = rightPagerState.currentPage,
                activeColor = colors.accent,
                inactiveColor = colors.mutedText,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
            )
        }
    }
}

@Composable
private fun WeatherOnCalendarCard(
    now: Date,
    weather: WeatherInfo,
    colors: StandByColors
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Top: Live Weather for Current Location
        CompactWeatherWidget(weather = weather, colors = colors)

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom: Calendar with enhanced size and height
        CalendarWidget(now = now, colors = colors)
    }
}

@Composable
fun VerticalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    if (pageCount <= 1) return
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 5.dp else 3.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else inactiveColor)
            )
        }
    }
}
