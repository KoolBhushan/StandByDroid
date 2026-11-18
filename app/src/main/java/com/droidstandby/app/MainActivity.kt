package com.droidstandby.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.droidstandby.app.data.StandByPreferences
import com.droidstandby.app.data.WeatherRepository
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.model.NightAccent
import com.droidstandby.app.model.WeatherInfo
import com.droidstandby.app.ui.StandByContent
import com.droidstandby.app.ui.screens.AppSettingsScreen
import com.droidstandby.app.ui.theme.DroidStandByTheme
import com.droidstandby.app.ui.theme.getStandByColors
import kotlinx.coroutines.delay
import java.util.Date

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var prefs: StandByPreferences
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private var isRoomDark = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = StandByPreferences(this)

        setupWindowFlags()
        hideSystemUI()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            DroidStandByTheme {
                MainStandByScreen(prefs = prefs, isRoomDark = isRoomDark.value)
            }
        }
    }

    private fun setupWindowFlags() {
        // Keep screen awake while docked on nightstand
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Show over keyguard / lock screen when plugged in
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? android.app.KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        lightSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values.firstOrNull() ?: 50f
            isRoomDark.value = lux < 5f
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun MainStandByScreen(prefs: StandByPreferences, isRoomDark: Boolean) {
    val context = LocalContext.current

    var now by remember { mutableStateOf(Date()) }
    var isLandscape by remember {
        mutableStateOf(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    var batteryInfo by remember { mutableStateOf(BatteryInfo()) }
    var weatherInfo by remember { mutableStateOf(WeatherInfo(city = prefs.city)) }

    var photosList by remember { mutableStateOf(prefs.getPhotoUris()) }

    // Smart Stacks Pager States
    val leftWidgetPagerState = rememberPagerState(initialPage = prefs.leftWidgetIndex.coerceIn(0, 3)) { 4 }
    val rightWidgetPagerState = rememberPagerState(initialPage = prefs.rightWidgetIndex.coerceIn(0, 2)) { 3 }
    val bigClockPagerState = rememberPagerState(initialPage = prefs.bigClockIndex.coerceIn(0, 4)) { 5 }

    // Save page indices when changed
    LaunchedEffect(leftWidgetPagerState.currentPage) {
        prefs.leftWidgetIndex = leftWidgetPagerState.currentPage
    }
    LaunchedEffect(rightWidgetPagerState.currentPage) {
        prefs.rightWidgetIndex = rightWidgetPagerState.currentPage
    }
    LaunchedEffect(bigClockPagerState.currentPage) {
        prefs.bigClockIndex = bigClockPagerState.currentPage
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
        }
        if (uris.isNotEmpty()) {
            photosList = uris
            prefs.savePhotoUris(uris)
        }
    }

    // Battery & Disconnect Broadcast Receiver
    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent == null) return
                if (intent.action == Intent.ACTION_POWER_DISCONNECTED) {
                    (context as? android.app.Activity)?.finish()
                    return
                }
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100

                val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                val plugType = when (chargePlug) {
                    BatteryManager.BATTERY_PLUGGED_AC -> "Fast AC"
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
                    BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                    else -> "Battery"
                }

                batteryInfo = BatteryInfo(
                    level = batteryPct,
                    isCharging = isCharging,
                    chargePlug = plugType
                )
            }
        }
        context.registerReceiver(receiver, filter)
        onDispose {
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
        }
    }

    // Clock ticker and orientation poll
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            delay(1000L)
        }
    }

    // Weather refresh
    LaunchedEffect(prefs.city) {
        weatherInfo = WeatherRepository.getWeather(prefs.city)
    }

    // Night mode determination from saved preferences
    val nightModeActive = if (prefs.autoNightMode) isRoomDark else prefs.nightModeEnabled
    val currentAccent = try {
        NightAccent.valueOf(prefs.selectedAccentName)
    } catch (_: Exception) {
        NightAccent.IOS_RED
    }
    val colors = getStandByColors(isNightMode = nightModeActive, nightAccent = currentAccent)

    if (!isLandscape) {
        // App In-Depth Customization & Settings Dashboard (in Portrait)
        AppSettingsScreen(
            prefs = prefs,
            battery = batteryInfo,
            weather = weatherInfo,
            photosCount = photosList.size,
            onPickPhotos = { photoPickerLauncher.launch(arrayOf("image/*")) },
            onPreviewStandBy = {
                (context as? android.app.Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        )
    } else {
        // Pure Screen Saver / Bedside Ambient Window (NO customization controls)
        StandByContent(
            now = now,
            battery = batteryInfo,
            weather = weatherInfo,
            photos = photosList,
            is24Hour = prefs.is24Hour,
            showSeconds = prefs.showSeconds,
            antiBurnIn = prefs.antiBurnIn,
            colors = colors,
            leftWidgetPagerState = leftWidgetPagerState,
            rightWidgetPagerState = rightWidgetPagerState,
            bigClockPagerState = bigClockPagerState,
            onPickPhotos = { photoPickerLauncher.launch(arrayOf("image/*")) }
        )
    }
}
