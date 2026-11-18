package com.droidstandby.app.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.ui.theme.ChargingGreen
import com.droidstandby.app.ui.theme.StandByColors

@Composable
fun BatteryWidget(
    battery: BatteryInfo,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val progress = (battery.level.coerceIn(0, 100)) / 100f
    val sweepAngle = progress * 360f

    val ringActiveColor = if (colors.isNightMode) colors.accent else ChargingGreen
    val ringTrackColor = if (colors.isNightMode) colors.secondaryText.copy(alpha = 0.2f) else Color(0x3334C759)

    Column(
        modifier = modifier.fillMaxSize().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 10.dp.toPx()
                // Track
                drawCircle(
                    color = ringTrackColor,
                    style = Stroke(width = strokeWidth)
                )
                // Active Arc
                drawArc(
                    color = ringActiveColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (battery.isCharging) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Charging",
                        tint = ringActiveColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "${battery.level}%",
                    color = colors.primaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (battery.isCharging) "Charging (${battery.chargePlug})" else "Bedside Stand",
            color = if (battery.isCharging) ringActiveColor else colors.secondaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
