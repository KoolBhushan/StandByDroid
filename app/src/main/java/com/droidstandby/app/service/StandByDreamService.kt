package com.droidstandby.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.BatteryManager
import android.service.dreams.DreamService
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
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

        val prefs = StandByPreferences(this)
        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@StandByDreamService)
            setViewTreeSavedStateRegistryOwner(this@StandByDreamService)
            setContent {
                DroidStandByTheme {
                    var now by remember { mutableStateOf(Date()) }
                    var isLandscape by remember {
                        mutableStateOf(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    }
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
                            isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                            delay(1000L)
                        }
                    }

                    LaunchedEffect(prefs.city) {
                        weatherInfo = WeatherRepository.getWeather(prefs.city)
                    }

                    if (!isLandscape) {
                        RotateDevicePrompt(battery = liveBatteryInfo.value)
                    } else {
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
            }
        }

        setContentView(composeView)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        registerBatteryReceiver()
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
        try {
            batteryReceiver?.let { unregisterReceiver(it) }
        } catch (_: Exception) {}
    }
}
