package com.droidstandby.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.droidstandby.app.MainActivity
import com.droidstandby.app.R
import com.droidstandby.app.data.StandByPreferences
import com.droidstandby.app.receiver.PowerConnectionReceiver

/**
 * Lightweight Foreground Service that dynamically registers for ACTION_POWER_CONNECTED and ACTION_POWER_DISCONNECTED.
 * Since Android 8.0 (API 26+), manifest-declared receivers cannot receive implicit power broadcasts.
 * This service ensures StandBy Droid reliably wakes up and displays when connected to power without requiring ADB or developer options.
 */
class StandByMonitorService : Service() {

    companion object {
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "standby_monitor_service_channel"

        fun start(context: Context) {
            val intent = Intent(context, StandByMonitorService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {}
        }

        fun stop(context: Context) {
            val intent = Intent(context, StandByMonitorService::class.java)
            try {
                context.stopService(intent)
            } catch (_: Exception) {}
        }
    }

    private var powerReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()
        registerPowerReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundNotification()
        return START_STICKY
    }

    private fun startForegroundNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "StandBy Charging Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors charging connection to automatically launch StandBy bedside display"
                setShowBadge(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
            }
            nm?.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("StandBy Droid")
            .setContentText("Ready to display when plugged in")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun registerPowerReceiver() {
        if (powerReceiver != null) return

        powerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                val action = intent?.action ?: return
                val prefs = StandByPreferences(context)
                if (!prefs.autoLaunchOnCharge) return

                if (action == Intent.ACTION_POWER_CONNECTED || action == Intent.ACTION_POWER_DISCONNECTED) {
                    PowerConnectionReceiver().onReceive(context, intent)
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        ContextCompat.registerReceiver(
            this,
            powerReceiver!!,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onDestroy() {
        powerReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (_: Exception) {}
            powerReceiver = null
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
