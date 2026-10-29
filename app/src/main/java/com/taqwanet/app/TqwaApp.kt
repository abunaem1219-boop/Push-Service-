package com.taqwanet.app

import android.app.Application
import com.taqwanet.app.core.notification.AlarmScheduler
import com.taqwanet.app.core.notification.NotificationHelper
import com.taqwanet.app.data.local.AppDatabase
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaqwaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // ১. নোটিফিকেশন চ্যানেল রেজিস্টার
        NotificationHelper(this).createNotificationChannels()

        // ২. ডাটাবেজ প্রিলোড
        AppDatabase.getInstance(this)

        // ৩. ব্যাকগ্রাউন্ড অ্যালার্ম শিডিউল
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = AppPreferencesDataSource(this@TaqwaApp)
                val scheduler = AlarmScheduler(this@TaqwaApp)
                scheduler.scheduleDailyPrayerAlarms(prefs)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}