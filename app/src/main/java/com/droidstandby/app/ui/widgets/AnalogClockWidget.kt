package com.droidstandby.app.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors
import java.util.Calendar
import java.util.Date
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClockWidget(
    now: Date,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply { time = now }
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    val millis = cal.get(Calendar.MILLISECOND)
    val day = cal.get(Calendar.DAY_OF_MONTH)

    val secondAngle = (second + millis / 1000f) * 6f
    val minuteAngle = (minute + second / 60f) * 6f
    val hourAngle = (hour + minute / 60f) * 30f

    val dialColor = colors.secondaryText
    val handColor = colors.primaryText
    val secondHandColor = if (colors.isNightMode) colors.accent else Color(0xFFFF453A)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Hour and Minute Markers
            for (i in 0 until 60) {
                val isHour = i % 5 == 0
                val angle = i * 6f * (Math.PI / 180.0)
                val strokeLength = if (isHour) radius * 0.16f else radius * 0.08f
                val strokeWidth = if (isHour) 3.5.dp.toPx() else 1.5.dp.toPx()
                val markerColor = if (isHour) dialColor else dialColor.copy(alpha = 0.35f)

                val startX = (center.x + (radius - strokeLength) * sin(angle)).toFloat()
                val startY = (center.y - (radius - strokeLength) * cos(angle)).toFloat()
                val endX = (center.x + radius * sin(angle)).toFloat()
                val endY = (center.y - radius * cos(angle)).toFloat()

                drawLine(
                    color = markerColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // Hour Hand
            rotate(hourAngle, pivot = center) {
                drawLine(
                    color = handColor,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.52f),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Minute Hand
            rotate(minuteAngle, pivot = center) {
                drawLine(
                    color = handColor,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.75f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Second Hand
            rotate(secondAngle, pivot = center) {
                // Tail
                drawLine(
                    color = secondHandColor,
                    start = Offset(center.x, center.y + radius * 0.2f),
                    end = center,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Main pointer
                drawLine(
                    color = secondHandColor,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.85f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Center cap
                drawCircle(color = secondHandColor, radius = 5.dp.toPx(), center = center)
                drawCircle(color = Color.Black, radius = 2.dp.toPx(), center = center)
            }
        }

        // Circular Date Badge at 3 o'clock position
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 44.dp)
        ) {
            Text(
                text = day.toString(),
                color = colors.primaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
