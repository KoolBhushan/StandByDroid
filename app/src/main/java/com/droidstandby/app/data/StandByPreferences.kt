package com.droidstandby.app.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.droidstandby.app.model.NightAccent

class StandByPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("droid_standby_prefs", Context.MODE_PRIVATE)

    var city: String
        get() = prefs.getString("weather_city", "Current Location") ?: "Current Location"
        set(value) = prefs.edit().putString("weather_city", value).apply()

    var is24Hour: Boolean
        get() = prefs.getBoolean("is_24_hour", false)
        set(value) = prefs.edit().putBoolean("is_24_hour", value).apply()

    var showSeconds: Boolean
        get() = prefs.getBoolean("show_seconds", true)
        set(value) = prefs.edit().putBoolean("show_seconds", value).apply()

    var nightModeEnabled: Boolean
        get() = prefs.getBoolean("night_mode_enabled", false)
        set(value) = prefs.edit().putBoolean("night_mode_enabled", value).apply()

    var autoNightMode: Boolean
        get() = prefs.getBoolean("auto_night_mode", false)
        set(value) = prefs.edit().putBoolean("auto_night_mode", value).apply()

    var antiBurnIn: Boolean
        get() = prefs.getBoolean("anti_burn_in", true)
        set(value) = prefs.edit().putBoolean("anti_burn_in", value).apply()

    var autoLaunchOnCharge: Boolean
        get() = prefs.getBoolean("auto_launch_on_charge", true)
        set(value) = prefs.edit().putBoolean("auto_launch_on_charge", value).apply()

    var selectedAccentName: String
        get() = prefs.getString("accent_name", NightAccent.IOS_RED.name) ?: NightAccent.IOS_RED.name
        set(value) = prefs.edit().putString("accent_name", value).apply()

    var leftWidgetIndex: Int
        get() = prefs.getInt("left_widget_idx", 0)
        set(value) = prefs.edit().putInt("left_widget_idx", value).apply()

    var rightWidgetIndex: Int
        get() = prefs.getInt("right_widget_idx", 0)
        set(value) = prefs.edit().putInt("right_widget_idx", value).apply()

    var bigClockIndex: Int
        get() = prefs.getInt("big_clock_idx", 0)
        set(value) = prefs.edit().putInt("big_clock_idx", value).apply()

    fun getPhotoUris(): List<Uri> {
        val raw = prefs.getStringSet("photo_uris", emptySet()) ?: emptySet()
        return raw.mapNotNull {
            try { Uri.parse(it) } catch (_: Exception) { null }
        }
    }

    fun savePhotoUris(uris: List<Uri>) {
        val stringSet = uris.map { it.toString() }.toSet()
        prefs.edit().putStringSet("photo_uris", stringSet).apply()
    }
}
