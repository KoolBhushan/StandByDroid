package com.droidstandby.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.SensorManager
import android.os.BatteryManager
import android.service.dreams.DreamService
import android.view.OrientationEventListener
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.droidstandby.app.data.StandByPreferences
import com.droidstandby.app.data.WeatherRepository
import com.droidstandby.app.model.BatteryInfo
import com.droidstandby.app.model.NightAccent
import com.droidstandby.app.model.WeatherInfo
import com.droidstandby.app.ui.StandByContent
import com.droidstandby.app.ui.components.RotateDevicePrompt
import com.droidstandby.app.ui.theme.DroidStandByTheme
import com.droidstandby.app.ui.theme.getStandByColors
import kotlinx.coroutines.delay
import java.util.Date

class StandByDreamService : DreamService(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private var batteryReceiver: BroadcastReceiver? = null
    private var liveBatteryInfo = mutableStateOf(BatteryInfo(100, true, "Fast AC", 25f))

    private var orientationListener: OrientationEventListener? = null
    private var deviceTiltAngle = mutableStateOf(0)

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isFullscreen = true
        isScreenBright = true

        window?.let { win ->
            try {
                win.attributes = win.attributes.apply {
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }
                windowManager?.updateViewLayout(win.decorView, win.attributes)
            } catch (_: Exception) {}
        }

        initOrientationListener()

        val prefs = StandByPreferences(this)
        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@StandByDreamService)
            setViewTreeSavedStateRegistryOwner(this@StandByDreamService)
            setContent {
                DroidStandByTheme {
                    val configuration = LocalConfiguration.current
                    val isNativeLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    val tilt = deviceTiltAngle.value
                    val isTiltLandscape = (tilt in 60..120) || (tilt in 240..300)

                    var now by remember { mutableStateOf(Date()) }
                    var weatherInfo by remember { mutableStateOf(WeatherInfo(prefs.city, "--", "Loading...")) }
                    var nightMode by remember { mutableStateOf(prefs.nightModeEnabled) }
                    val accent = try {
                        NightAccent.valueOf(prefs.selectedAccentName)
                    } catch (_: Exception) {
                        NightAccent.IOS_RED
                    }

                    val leftPagerState = rememberPagerState(initialPage = prefs.leftWidgetIndex.coerceIn(0, 3)) { 4 }
                    val rightPagerState = rememberPagerState(initialPage = prefs.rightWidgetIndex.coerceIn(0, 2)) { 3 }
                    val bigClockPagerState = rememberPagerState(initialPage = prefs.bigClockIndex.coerceIn(0, 4)) { 5 }

                    val colors = getStandByColors(isNightMode = nightMode, nightAccent = accent)

                    LaunchedEffect(Unit) {
                        while (true) {
                            now = Date()
                            delay(1000L)
                        }
                    }

                    LaunchedEffect(prefs.city) {
                        weatherInfo = WeatherRepository.getWeather(prefs.city)
                    }

                    if (isNativeLandscape) {
                        // 1. Native landscape window
                        StandByContent(
                            now = now,
                            battery = liveBatteryInfo.value,
                            weather = weatherInfo,
                            photos = prefs.getPhotoUris(),
                            is24Hour = prefs.is24Hour,
                            showSeconds = prefs.showSeconds,
                            antiBurnIn = prefs.antiBurnIn,
                            colors = colors,
                            leftWidgetPagerState = leftPagerState,
                            rightWidgetPagerState = rightPagerState,
                            bigClockPagerState = bigClockPagerState,
                            onPickPhotos = {}
                        )
                    } else if (isTiltLandscape) {
                        // 2. Window is portrait buffer (Auto-Rotate OFF), but phone is held/docked horizontally!
                        // Using requiredSize so Compose does NOT clamp to the 411dp portrait parent width!
                        val rotationZ = if (tilt in 60..120) 270f else 90f
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .requiredSize(
                                        width = configuration.screenHeightDp.dp,
                                        height = configuration.screenWidthDp.dp
                                    )
                                    .graphicsLayer {
                                        this.rotationZ = rotationZ
                                    }
                            ) {
                                StandByContent(
                                    now = now,
                                    battery = liveBatteryInfo.value,
                                    weather = weatherInfo,
                                    photos = prefs.getPhotoUris(),
                                    is24Hour = prefs.is24Hour,
                                    showSeconds = prefs.showSeconds,
                                    antiBurnIn = prefs.antiBurnIn,
                                    colors = colors,
                                    leftWidgetPagerState = leftPagerState,
                                    rightWidgetPagerState = rightPagerState,
                                    bigClockPagerState = bigClockPagerState,
                                    onPickPhotos = {}
                                )
                            }
                        }
                    } else {
                        // 3. Phone is physically upright in portrait -> prompt user to place on horizontal stand
                        RotateDevicePrompt(battery = liveBatteryInfo.value)
                    }
                }
            }
        }

        setContentView(composeView)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        registerBatteryReceiver()
    }

    private fun initOrientationListener() {
        orientationListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation != ORIENTATION_UNKNOWN) {
                    deviceTiltAngle.value = orientation
                }
            }
        }
        if (orientationListener?.canDetectOrientation() == true) {
            orientationListener?.enable()
        }
    }

    private fun registerBatteryReceiver() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
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
                liveBatteryInfo.value = BatteryInfo(
                    level = batteryPct,
                    isCharging = isCharging,
                    chargePlug = plugType
                )
            }
        }
        registerReceiver(batteryReceiver, filter)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        orientationListener?.disable()
        try {
            batteryReceiver?.let { unregisterReceiver(it) }
        } catch (_: Exception) {}
    }
}
