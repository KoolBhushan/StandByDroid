package com.droidstandby.app.ui.clocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RetroFlipClock(
    now: Date,
    is24Hour: Boolean,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val hourFormat = if (is24Hour) "HH" else "hh"
    val hourStr = SimpleDateFormat(hourFormat, Locale.getDefault()).format(now)
    val minStr = SimpleDateFormat("mm", Locale.getDefault()).format(now)
    val amPmStr = if (!is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(now) else ""

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            FlipTile(text = hourStr, colors = colors)
            Spacer(modifier = Modifier.width(16.dp))
            FlipTile(text = minStr, colors = colors)

            if (amPmStr.isNotEmpty()) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = amPmStr,
                    color = colors.accent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FlipTile(text: String, colors: StandByColors) {
    val cardBg = if (colors.isNightMode) Color(0xFF1E0708) else Color(0xFF1C1C1E)
    val dividerColor = if (colors.isNightMode) Color(0xFF000000) else Color(0xFF121214)

    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colors.primaryText,
            fontSize = 90.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-2).sp
        )

        // Middle Split Divider
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = dividerColor
        )
    }
}
