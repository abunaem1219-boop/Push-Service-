package com.taqwanet.app.core.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taqwanet.app.core.notification.AlarmScheduler
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val prefs = AppPreferencesDataSource(context)
                        val scheduler = AlarmScheduler(context)
                        scheduler.scheduleDailyPrayerAlarms(prefs)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}