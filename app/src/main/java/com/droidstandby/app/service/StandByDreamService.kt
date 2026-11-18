package com.droidstandby.app.service

import android.content.Intent
import android.service.dreams.DreamService
import com.droidstandby.app.MainActivity

class StandByDreamService : DreamService() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isFullscreen = true
        launchStandByActivity()
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        launchStandByActivity()
    }

    private fun launchStandByActivity() {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("triggered_by_charging", true)
        }
        try {
            startActivity(launchIntent)
            wakeUp()
        } catch (_: Exception) {}
    }
}
