package com.droidstandby.app.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.model.WeatherInfo
import com.droidstandby.app.ui.screens.BigClocksScreen
import com.droidstandby.app.ui.screens.PhotoFrameScreen
import com.droidstandby.app.ui.screens.WidgetDuoScreen
import com.droidstandby.app.ui.theme.StandByColors
import kotlinx.coroutines.delay
import java.util.Date
import kotlin.random.Random

@Composable
fun StandByContent(
    now: Date,
    battery: BatteryInfo,
    weather: WeatherInfo,
    photos: List<Uri>,
    is24Hour: Boolean,
    showSeconds: Boolean,
    antiBurnIn: Boolean,
    colors: StandByColors,
    leftWidgetPagerState: PagerState,
    rightWidgetPagerState: PagerState,
    bigClockPagerState: PagerState,
    onPickPhotos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mainPagerState = rememberPagerState(initialPage = 0) { 3 }

    // Anti-Burn-In pixel shifting offset
    var burnInOffsetX by remember { mutableIntStateOf(0) }
    var burnInOffsetY by remember { mutableIntStateOf(0) }

    LaunchedEffect(antiBurnIn) {
        if (antiBurnIn) {
            while (true) {
                delay(120_000L) // every 2 minutes
                burnInOffsetX = Random.nextInt(-4, 5)
                burnInOffsetY = Random.nextInt(-4, 5)
            }
        } else {
            burnInOffsetX = 0
            burnInOffsetY = 0
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Main Horizontal Pager with 3 primary StandBy pages
        HorizontalPager(
            state = mainPagerState,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(burnInOffsetX, burnInOffsetY) }
        ) { page ->
            when (page) {
                0 -> WidgetDuoScreen(
                    leftPagerState = leftWidgetPagerState,
                    rightPagerState = rightWidgetPagerState,
                    now = now,
                    is24Hour = is24Hour,
                    showSeconds = showSeconds,
                    battery = battery,
                    weather = weather,
                    photoUri = photos.firstOrNull(),
                    colors = colors,
                    onPickPhotoClick = onPickPhotos
                )
                1 -> PhotoFrameScreen(
                    photos = photos,
                    now = now,
                    is24Hour = is24Hour,
                    battery = battery,
                    colors = colors,
                    onPickPhotosClick = onPickPhotos
                )
                2 -> BigClocksScreen(
                    pagerState = bigClockPagerState,
                    now = now,
                    is24Hour = is24Hour,
                    showSeconds = showSeconds,
                    battery = battery,
                    colors = colors
                )
            }
        }

        // Clean Top-Right Battery % Indicator (No customization controls on screen saver)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (colors.isNightMode) colors.accent.copy(alpha = 0.18f)
                        else Color(0x22FFFFFF)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                if (battery.isCharging) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Charging",
                        tint = if (colors.isNightMode) colors.accent else Color(0xFF34C759),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = "${battery.level}%",
                    color = if (battery.isCharging) (if (colors.isNightMode) colors.accent else Color(0xFF34C759)) else colors.primaryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Page Indicator Dots (Horizontal)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 3) {
                val isSelected = i == mainPagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 7.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) colors.accent else colors.mutedText)
                )
            }
        }
    }
}
