package com.droidstandby.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.ui.clocks.BoldDigitalClock
import com.droidstandby.app.ui.clocks.NeonGlowClock
import com.droidstandby.app.ui.clocks.RetroFlipClock
import com.droidstandby.app.ui.clocks.RomanAnalogClock
import com.droidstandby.app.ui.clocks.SolarArcClock
import com.droidstandby.app.ui.theme.StandByColors
import java.util.Date

@Composable
fun BigClocksScreen(
    pagerState: PagerState,
    now: Date,
    is24Hour: Boolean,
    showSeconds: Boolean,
    battery: BatteryInfo,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> BoldDigitalClock(
                    now = now,
                    is24Hour = is24Hour,
                    showSeconds = showSeconds,
                    battery = battery,
                    colors = colors
                )
                1 -> RetroFlipClock(
                    now = now,
                    is24Hour = is24Hour,
                    colors = colors
                )
                2 -> SolarArcClock(
                    now = now,
                    is24Hour = is24Hour,
                    colors = colors
                )
                3 -> RomanAnalogClock(
                    now = now,
                    colors = colors
                )
                4 -> NeonGlowClock(
                    now = now,
                    is24Hour = is24Hour,
                    colors = colors
                )
            }
        }

        VerticalPagerIndicator(
            pageCount = pagerState.pageCount,
            currentPage = pagerState.currentPage,
            activeColor = colors.accent,
            inactiveColor = colors.mutedText,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )
    }
}
