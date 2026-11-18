package com.droidstandby.app.receiver

import android.app.ActivityOptions
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.droidstandby.app.MainActivity
import com.droidstandby.app.R

class PowerConnectionReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "standby_charging_channel_v2"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (action == Intent.ACTION_POWER_DISCONNECTED) {
            nm?.cancel(NOTIFICATION_ID)
            return
        }

        if (action == Intent.ACTION_POWER_CONNECTED) {
            // 1. Wake the physical display
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            val wakeLock = pm?.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "droidstandby:charging_wake"
            )
            try {
                wakeLock?.acquire(5000L)
            } catch (_: Exception) {}

            // 2. Prepare intent to launch MainActivity over lock screen in landscape StandBy mode
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra("triggered_by_charging", true)
            }

            // 3. Direct Activity Start with ActivityOptions
            try {
                val bOptions = ActivityOptions.makeBasic()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    bOptions.setPendingIntentBackgroundActivityStartMode(
                        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                    )
                }
                context.startActivity(launchIntent, bOptions.toBundle())
            } catch (_: Exception) {}

            // 4. ALWAYS trigger Full-Screen Intent notification if device is locked or screen off
            // Full-Screen Intent is Android's guaranteed platform mechanism to display over keyguard
            val km = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            val isLocked = km?.isKeyguardLocked == true || pm?.isInteractive == false
            if (isLocked) {
                showFullScreenNotification(context, launchIntent)
            }
        }
    }

    private fun showFullScreenNotification(context: Context, launchIntent: Intent) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "StandBy Charging Display",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows StandBy bedside display when charging"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setSound(null, null)
                enableVibration(false)
            }
            nm.createNotificationChannel(channel)
        }

        val options = ActivityOptions.makeBasic()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            options.setPendingIntentBackgroundActivityStartMode(
                ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
            )
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            options.toBundle()
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("StandBy Droid")
            .setContentText("Connected to charger")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .setOngoing(false)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        nm.notify(NOTIFICATION_ID, notification)
    }
}
