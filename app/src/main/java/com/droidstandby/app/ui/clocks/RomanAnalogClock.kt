package com.droidstandby.app.ui.clocks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors
import java.util.Calendar
import java.util.Date
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RomanAnalogClock(
    now: Date,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply { time = now }
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    val millis = cal.get(Calendar.MILLISECOND)

    val secondAngle = (second + millis / 1000f) * 6f
    val minuteAngle = (minute + second / 60f) * 6f
    val hourAngle = (hour + minute / 60f) * 30f

    val handColor = colors.primaryText
    val secondHandColor = if (colors.isNightMode) colors.accent else Color(0xFFFF9F0A)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.85f

            // 12 Major Hour Markers
            for (i in 0 until 12) {
                val angle = i * 30f * (Math.PI / 180.0)
                val isQuarter = i % 3 == 0
                val markerLen = if (isQuarter) radius * 0.18f else radius * 0.10f
                val strokeW = if (isQuarter) 4.dp.toPx() else 2.dp.toPx()

                val startX = (center.x + (radius - markerLen) * sin(angle)).toFloat()
                val startY = (center.y - (radius - markerLen) * cos(angle)).toFloat()
                val endX = (center.x + radius * sin(angle)).toFloat()
                val endY = (center.y - radius * cos(angle)).toFloat()

                drawLine(
                    color = colors.secondaryText,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }

            // Hour Hand
            rotate(hourAngle, pivot = center) {
                drawLine(
                    color = handColor,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.55f),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Minute Hand
            rotate(minuteAngle, pivot = center) {
                drawLine(
                    color = handColor,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.8f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Second Hand
            rotate(secondAngle, pivot = center) {
                drawLine(
                    color = secondHandColor,
                    start = Offset(center.x, center.y + radius * 0.2f),
                    end = Offset(center.x, center.y - radius * 0.9f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(color = secondHandColor, radius = 5.dp.toPx(), center = center)
            }
        }

        // Roman Numerals
        Text(
            text = "XII",
            color = colors.secondaryText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 18.dp)
        )
        Text(
            text = "VI",
            color = colors.secondaryText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp)
        )
    }
}
