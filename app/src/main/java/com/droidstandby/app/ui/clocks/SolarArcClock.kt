package com.droidstandby.app.ui.clocks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SolarArcClock(
    now: Date,
    is24Hour: Boolean,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply { time = now }
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val dayProgress = (hour * 60 + minute) / (24f * 60f) // 0f to 1f

    val timeFormat = if (is24Hour) "HH:mm" else "h:mm a"
    val timeStr = SimpleDateFormat(timeFormat, Locale.getDefault()).format(now)

    val arcColor = colors.secondaryText.copy(alpha = 0.35f)
    val sunColor = if (colors.isNightMode) colors.accent else Color(0xFFFFD60A)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp)) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val baselineY = height * 0.8f
            val arcRadius = width * 0.42f

            // Baseline horizon line
            drawLine(
                color = colors.cardBorder,
                start = Offset(centerX - arcRadius - 20.dp.toPx(), baselineY),
                end = Offset(centerX + arcRadius + 20.dp.toPx(), baselineY),
                strokeWidth = 2.dp.toPx()
            )

            // Solar Arc (semi-circle)
            drawArc(
                color = arcColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - arcRadius, baselineY - arcRadius),
                size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                )
            )

            // Sun/Moon Orb Position along Arc
            val sunAngle = (180f + dayProgress * 180f) * (Math.PI / 180.0)
            val sunX = (centerX + arcRadius * cos(sunAngle)).toFloat()
            val sunY = (baselineY + arcRadius * sin(sunAngle)).toFloat()

            // Glow and Orb
            drawCircle(color = sunColor.copy(alpha = 0.25f), radius = 18.dp.toPx(), center = Offset(sunX, sunY))
            drawCircle(color = sunColor, radius = 9.dp.toPx(), center = Offset(sunX, sunY))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeStr,
                color = colors.primaryText,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SOLAR TRACKER",
                color = colors.secondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }
    }
}
