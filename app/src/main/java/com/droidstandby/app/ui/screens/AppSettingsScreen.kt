package com.droidstandby.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidstandby.app.data.StandByPreferences
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.model.NightAccent
import com.droidstandby.app.model.WeatherInfo

@Composable
fun AppSettingsScreen(
    prefs: StandByPreferences,
    battery: BatteryInfo,
    weather: WeatherInfo,
    photosCount: Int,
    onPickPhotos: () -> Unit,
    onPreviewStandBy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var cityText by remember { mutableStateOf(prefs.city) }
    var is24Hour by remember { mutableStateOf(prefs.is24Hour) }
    var showSeconds by remember { mutableStateOf(prefs.showSeconds) }
    var nightMode by remember { mutableStateOf(prefs.nightModeEnabled) }
    var autoNightMode by remember { mutableStateOf(prefs.autoNightMode) }
    var antiBurnIn by remember { mutableStateOf(prefs.antiBurnIn) }
    var selectedAccent by remember {
        mutableStateOf(
            try { NightAccent.valueOf(prefs.selectedAccentName) } catch (_: Exception) { NightAccent.IOS_RED }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0C))
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // App Title & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "StandBy Droid",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Screen Saver & Bedside Display Settings",
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }

            // Battery status pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x22FFFFFF))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (battery.isCharging) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Charging",
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "${battery.level}%",
                        color = if (battery.isCharging) Color(0xFF34C759) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Preview StandBy Button
        Button(
            onClick = onPreviewStandBy,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0A84FF))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Preview StandBy Display (Landscape)", color = Color(0xFF0A84FF), fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 1: Weather & Current Location
        SettingsCard(title = "WEATHER & LOCATION", icon = Icons.Default.LocationOn) {
            OutlinedTextField(
                value = cityText,
                onValueChange = { cityText = it },
                label = { Text("Location (Use 'Current Location' for Auto-GPS)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF0A84FF),
                    unfocusedBorderColor = Color(0x33FFFFFF)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Currently detected: 📍 ${weather.city} (${weather.temperature}, ${weather.condition}${if (weather.aqi != null) ", AQI ${weather.aqi}" else ""})",
                fontSize = 12.sp,
                color = Color(0xFF34C759)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Clock Format
        SettingsCard(title = "CLOCK PREFERENCES", icon = Icons.Default.Schedule) {
            SettingRow(
                label = "24-Hour Time Format",
                checked = is24Hour,
                onCheckedChange = { is24Hour = it }
            )
            SettingRow(
                label = "Show Seconds Counter",
                checked = showSeconds,
                onCheckedChange = { showSeconds = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Bedside Night Mode
        SettingsCard(title = "BEDSIDE NIGHT MODE", icon = Icons.Default.Bedtime) {
            SettingRow(
                label = "Night Mode (Deep Red Monochromatic)",
                checked = nightMode,
                onCheckedChange = { nightMode = it }
            )
            SettingRow(
                label = "Auto Night Mode in Dark Room (Sensor)",
                checked = autoNightMode,
                onCheckedChange = { autoNightMode = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 4: Night Accent Color
        SettingsCard(title = "NIGHT COLOR THEME", icon = Icons.Default.Palette) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NightAccent.values().forEach { accent ->
                    val isSelected = accent == selectedAccent
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accent.primaryColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedAccent = accent },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 5: OLED Burn-in Protection
        SettingsCard(title = "OLED SCREEN PROTECTION", icon = Icons.Default.Security) {
            SettingRow(
                label = "Anti-Burn-In Pixel Shifter",
                checked = antiBurnIn,
                onCheckedChange = { antiBurnIn = it }
            )
            Text(
                text = "Micro-shifts elements subtly every 2 minutes to protect your AMOLED display during all-night bedside charging.",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 6: Photo Frame
        SettingsCard(title = "PHOTO FRAME ALBUM", icon = Icons.Default.AddPhotoAlternate) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (photosCount > 0) "$photosCount photos selected" else "No photos selected",
                    color = Color.White,
                    fontSize = 13.sp
                )
                OutlinedButton(onClick = onPickPhotos) {
                    Text("Select Photos")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 7: Lock Screen & Background Permissions Status
        val pm = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
        val isBatteryUnrestricted = pm?.isIgnoringBatteryOptimizations(context.packageName) == true
        val canDrawOverlays = Settings.canDrawOverlays(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
        val canUseFSI = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            nm?.canUseFullScreenIntent() == true
        } else true

        SettingsCard(title = "PERMISSIONS & BACKGROUND SETUP", icon = Icons.Default.Security) {
            Text(
                text = "Required for StandBy to wake up and display over lock screen without Developer Options:",
                fontSize = 12.sp,
                color = Color(0xFFB0B0B8)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Appear on Top
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Appear on top", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (canDrawOverlays) "Granted" else "Action needed: allow drawing over lock screen",
                        fontSize = 11.sp,
                        color = if (canDrawOverlays) Color(0xFF34C759) else Color(0xFFFF9F0A)
                    )
                }
                if (!canDrawOverlays) {
                    OutlinedButton(onClick = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                        } catch (_: Exception) {
                            context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                        }
                    }) {
                        Text("Grant", fontSize = 12.sp)
                    }
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34C759), modifier = Modifier.size(20.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFF2C2C2E))

            // 2. Battery Unrestricted
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Battery usage", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (isBatteryUnrestricted) "Unrestricted (Samsung will not kill app)" else "Action needed: set to Unrestricted",
                        fontSize = 11.sp,
                        color = if (isBatteryUnrestricted) Color(0xFF34C759) else Color(0xFFFF9F0A)
                    )
                }
                if (!isBatteryUnrestricted) {
                    OutlinedButton(onClick = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}")))
                        } catch (_: Exception) {
                            try {
                                context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                            } catch (_: Exception) {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                            }
                        }
                    }) {
                        Text("Fix", fontSize = 12.sp)
                    }
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34C759), modifier = Modifier.size(20.dp))
                }
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFF2C2C2E))

                // 3. Full-Screen Alerts (Android 14+)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Full-screen alerts", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            if (canUseFSI) "Allowed" else "Action needed: allow full-screen intent",
                            fontSize = 11.sp,
                            color = if (canUseFSI) Color(0xFF34C759) else Color(0xFFFF9F0A)
                        )
                    }
                    if (!canUseFSI) {
                        OutlinedButton(onClick = {
                            try {
                                context.startActivity(Intent("android.settings.MANAGE_APP_USE_FULL_SCREEN_INTENT", Uri.parse("package:${context.packageName}")))
                            } catch (_: Exception) {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                            }
                        }) {
                            Text("Allow", fontSize = 12.sp)
                        }
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34C759), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 8: Android System Screen Saver Shortcut
        SettingsCard(title = "ANDROID SCREEN SAVER SETUP", icon = Icons.Default.Bolt) {
            Text(
                text = "To let Android automatically activate StandBy whenever your phone charges on your nightstand:",
                fontSize = 12.sp,
                color = Color(0xFFB0B0B8)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_DREAM_SETTINGS))
                    } catch (_: Exception) {
                        context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Android Screen Saver Settings")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Save Button
        Button(
            onClick = {
                prefs.city = cityText
                prefs.is24Hour = is24Hour
                prefs.showSeconds = showSeconds
                prefs.nightModeEnabled = nightMode
                prefs.autoNightMode = autoNightMode
                prefs.antiBurnIn = antiBurnIn
                prefs.selectedAccentName = selectedAccent.name
                Toast.makeText(context, "Settings saved for Screen Saver!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Save & Apply Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16161A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0A84FF), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color(0xFF8E8E93),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34C759),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}
