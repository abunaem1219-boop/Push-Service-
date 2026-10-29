package com.taqwanet.app.core.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.taqwanet.app.core.audio.AudioPlaybackManager
import com.taqwanet.app.core.notification.NotificationHelper
import com.taqwanet.app.data.local.AppDatabase
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.data.local.entity.PrayerRecordEntity
import com.taqwanet.app.domain.model.PrayerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(notificationId)
        }

        when (intent.action) {
            NotificationHelper.ACTION_MARK_PRAYER_COMPLETED -> {
                val prayerId = intent.getStringExtra(NotificationHelper.EXTRA_PRAYER_ID) ?: return
                val dateString = intent.getStringExtra(NotificationHelper.EXTRA_DATE_STRING) ?: return
                val prayerType = PrayerType.fromId(prayerId)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        val entity = PrayerRecordEntity(
                            dateString = dateString,
                            prayerId = prayerId,
                            isCompleted = true,
                            completedAt = System.currentTimeMillis()
                        )
                        // ১. ডাটাবেজে স্বয়ংক্রিয় টিক মার্ক আপডেট
                        db.prayerRecordDao().insertOrUpdateRecord(entity)

                        // ২. সেটিংস অনুযায়ী সংশ্লিষ্ট ওয়াক্তের বাংলা কণ্ঠবার্তা প্লে করা
                        val prefs = AppPreferencesDataSource(context)
                        val isVoiceEnabled = prefs.completionVoiceEnabled.first()
                        if (isVoiceEnabled) {
                            AudioPlaybackManager.getInstance(context).playPrayerCompletionVoice(prayerType)
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            NotificationHelper.ACTION_DISMISS_OR_SNOOZE -> {
                // নোটিফিকেশন বন্ধ হয়েছে
            }
        }
    }
}