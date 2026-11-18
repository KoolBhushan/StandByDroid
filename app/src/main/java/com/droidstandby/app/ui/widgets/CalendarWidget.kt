package com.droidstandby.app.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarWidget(
    now: Date,
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply { time = now }
    val today = cal.get(Calendar.DAY_OF_MONTH)
    val monthTitle = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(now)

    // Compute first day of month and total days
    val monthCal = Calendar.getInstance().apply {
        time = now
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday
    val maxDays = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Month Title
        Text(
            text = monthTitle.uppercase(),
            color = if (colors.isNightMode) colors.accent else Color(0xFFFF453A),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Weekday header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    color = colors.mutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.size(24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar days grid (up to 5 weeks)
        var currentDay = 1
        val startOffset = firstDayOfWeek - 1

        for (week in 0 until 5) {
            if (currentDay > maxDays) break
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (dayCol in 0 until 7) {
                    val cellIndex = week * 7 + dayCol
                    if (cellIndex < startOffset || currentDay > maxDays) {
                        Box(modifier = Modifier.size(26.dp))
                    } else {
                        val dayNumber = currentDay
                        val isToday = dayNumber == today
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isToday) (if (colors.isNightMode) colors.accent else Color(0xFFFF453A))
                                    else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                color = if (isToday) Color.White else colors.primaryText,
                                fontSize = 13.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                        currentDay++
                    }
                }
            }
        }
    }
}
