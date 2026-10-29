package com.taqwanet.app.core.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taqwanet.app.core.audio.AudioPlaybackManager
import com.taqwanet.app.core.notification.AlarmScheduler
import com.taqwanet.app.core.notification.NotificationHelper
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.domain.model.PrayerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerId = intent.getStringExtra(AlarmScheduler.EXTRA_PRAYER_TYPE) ?: return
        val alarmKind = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_KIND) ?: return
        val formattedTime = intent.getStringExtra(AlarmScheduler.EXTRA_FORMATTED_TIME) ?: ""
        val leadMinutes = intent.getIntExtra(AlarmScheduler.EXTRA_LEAD_MINUTES, 10)

        val prayerType = PrayerType.fromId(prayerId)
        val notificationHelper = NotificationHelper(context)
        val notificationId = prayerType.ordinal + 100

        // ব্যাকগ্রাউন্ড থ্রেডে নিরাপদে ডেটাস্টোর থেকে সাউন্ড ও আজান পড়ার জন্য goAsync()
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = AppPreferencesDataSource(context)
                val soundName = prefs.notificationSound.first()
                val adhanType = prefs.adhanType.first()

                if (alarmKind == AlarmScheduler.ALARM_TYPE_START) {
                    // ১. ওয়াক্ত শুরুর পপআপ নোটিফিকেশন (কাস্টম সাউন্ড সহ)
                    notificationHelper.showPrayerStartNotification(
                        prayerType = prayerType,
                        formattedTime = formattedTime,
                        soundName = soundName,
                        notificationId = notificationId
                    )

                    // আজান প্লেব্যাক (যদি সেটিংসে সক্রিয় থাকে)
                    if (adhanType == "SHORT") {
                        AudioPlaybackManager.getInstance(context).playAdhan(isFull = false)
                    } else if (adhanType == "FULL") {
                        AudioPlaybackManager.getInstance(context).playAdhan(isFull = true)
                    }
                } else if (alarmKind == AlarmScheduler.ALARM_TYPE_ENDING) {
                    val dateString = LocalDate.now().toString()
                    
                    // ২. ওয়াক্ত সমাপ্তি নোটিফিকেশন (“আদায় করেছি” ও “এখনো পড়িনি” বাটন সহ)
                    notificationHelper.showPrayerEndingSoonNotification(
                        prayerType = prayerType,
                        remainingMinutes = leadMinutes,
                        dateString = dateString,
                        soundName = soundName,
                        notificationId = notificationId + 50
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}