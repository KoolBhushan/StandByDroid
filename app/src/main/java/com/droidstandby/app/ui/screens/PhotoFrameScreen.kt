package com.droidstandby.app.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.ui.theme.StandByColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhotoFrameScreen(
    photos: List<Uri>,
    now: Date,
    is24Hour: Boolean,
    battery: BatteryInfo,
    colors: StandByColors,
    onPickPhotosClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPhotoIndex by remember { mutableIntStateOf(0) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Rotate photos every 10 seconds if multiple exist
    LaunchedEffect(photos) {
        if (photos.isNotEmpty()) {
            while (true) {
                delay(10000L)
                currentPhotoIndex = (currentPhotoIndex + 1) % photos.size
            }
        }
    }

    LaunchedEffect(photos, currentPhotoIndex) {
        if (photos.isNotEmpty()) {
            val uri = photos[currentPhotoIndex]
            currentBitmap = withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    // Subtle Ken Burns slow zoom effect
    val infiniteTransition = rememberInfiniteTransition(label = "KenBurns")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Zoom"
    )

    val timeFormat = if (is24Hour) "HH:mm" else "h:mm"
    val timeStr = SimpleDateFormat(timeFormat, Locale.getDefault()).format(now)
    val dateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap!!.asImageBitmap(),
                contentDescription = "Photo Slide",
                modifier = Modifier
                    .fillMaxSize()
                    .scale(zoomScale),
                contentScale = ContentScale.Crop
            )

            // Night mode or subtle gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (colors.isNightMode) colors.accent.copy(alpha = 0.5f)
                        else Color(0x33000000)
                    )
            )
        } else {
            // Empty photo state prompt
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "No Photos",
                    tint = colors.accent,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Personal Bedside Photo Frame",
                    color = colors.primaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Display memories while your phone charges horizontally",
                    color = colors.secondaryText,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                FilledTonalButton(
                    onClick = onPickPhotosClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    )
                ) {
                    Text("Select Photos from Gallery")
                }
            }
        }

        // Floating Minimalist Clock in Top-Left
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp),
            color = Color(0x66000000),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeStr,
                    color = colors.primaryText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = dateStr,
                        color = colors.secondaryText,
                        fontSize = 11.sp
                    )
                    if (battery.isCharging) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = "Charging",
                                tint = colors.accent,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${battery.level}% Charging",
                                color = colors.accent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Bottom right photo counter or add photos button
        if (photos.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .clickable { onPickPhotosClick() },
                color = Color(0x66000000),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${currentPhotoIndex + 1} / ${photos.size}  •  Tap to change",
                    color = colors.secondaryText,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
