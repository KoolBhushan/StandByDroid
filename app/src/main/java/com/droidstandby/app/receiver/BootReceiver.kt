package com.droidstandby.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.droidstandby.app.data.StandByPreferences
import com.droidstandby.app.service.StandByMonitorService

/**
 * Listens for ACTION_BOOT_COMPLETED and ACTION_MY_PACKAGE_REPLACED.
 * Both are on Android's implicit broadcast exemption list, allowing the app
 * to restart StandByMonitorService after device reboot or app upgrade.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val prefs = StandByPreferences(context)
            if (prefs.autoLaunchOnCharge) {
                StandByMonitorService.start(context)
            }
        }
    }
}
