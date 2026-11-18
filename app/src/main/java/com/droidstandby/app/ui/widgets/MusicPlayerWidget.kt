package com.droidstandby.app.ui.widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.ui.theme.StandByColors

@Composable
fun MusicPlayerWidget(
    colors: StandByColors,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var trackProgress by remember { mutableFloatStateOf(0.42f) }

    val infiniteTransition = rememberInfiniteTransition(label = "VinylSpin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiscRotation"
    )

    Column(
        modifier = modifier.fillMaxSize().padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rotating Vinyl Disc Artwork
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .rotate(if (isPlaying) rotationAngle else 0f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f
                    // Outer vinyl grooves
                    drawCircle(color = Color(0xFF1C1C1E), radius = radius)
                    drawCircle(
                        color = Color(0xFF2C2C2E),
                        radius = radius * 0.75f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF2C2C2E),
                        radius = radius * 0.5f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    // Center label
                    drawCircle(
                        color = if (colors.isNightMode) colors.accent else Color(0xFFFF375F),
                        radius = radius * 0.35f
                    )
                    drawCircle(color = Color.Black, radius = 2.dp.toPx())
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Midnight Ambient",
                    color = colors.primaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Lofi Sleep Beats",
                    color = colors.secondaryText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Playing",
                tint = colors.accent,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress line
        LinearProgressIndicator(
            progress = { trackProgress },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = colors.accent,
            trackColor = colors.secondaryText.copy(alpha = 0.25f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { trackProgress = (trackProgress - 0.1f).coerceAtLeast(0f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.FastRewind, contentDescription = "Prev", tint = colors.secondaryText)
            }

            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = colors.primaryText,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = { trackProgress = (trackProgress + 0.1f).coerceAtMost(1f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.FastForward, contentDescription = "Next", tint = colors.secondaryText)
            }
        }
    }
}
