package com.taqwanet.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.taqwanet.app.MainActivity
import com.taqwanet.app.R
import com.taqwanet.app.core.notification.receivers.NotificationActionReceiver
import com.taqwanet.app.domain.model.PrayerType

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        const val BASE_CHANNEL_START = "channel_prayer_start"
        const val BASE_CHANNEL_END = "channel_prayer_end"
        const val CHANNEL_ISLAMIC_REMINDERS = "channel_islamic_reminders_v1"

        const val ACTION_MARK_PRAYER_COMPLETED = "com.taqwanet.app.ACTION_PRAYER_COMPLETED"
        const val ACTION_DISMISS_OR_SNOOZE = "com.taqwanet.app.ACTION_PRAYER_DISMISS"

        const val EXTRA_PRAYER_ID = "extra_prayer_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_DATE_STRING = "extra_date_string"
    }

    // নির্বাচিত সাউন্ড অনুযায়ী চ্যানেল আইডি তৈরি করা (Android 8+ সাউন্ড ইমিউটেবিলিটি বাইপাস)
    fun getDynamicChannelId(base: String, soundName: String): String {
        return "${base}_${soundName}"
    }

    fun ensureChannelWithSound(channelId: String, channelName: String, soundName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val soundResId = context.resources.getIdentifier(soundName, "raw", context.packageName)
            val soundUri: Uri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/$soundResId")

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "নামাজের সময়সূচি ও ওয়াক্ত নোটিফিকেশন"
                enableVibration(true)
                if (soundResId != 0) {
                    setSound(soundUri, audioAttributes)
                }
            }
            nm.createNotificationChannel(channel)
        }
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val reminderChannel = NotificationChannel(
                CHANNEL_ISLAMIC_REMINDERS,
                context.getString(R.string.islamic_reminders_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.islamic_reminders_channel_desc)
            }
            nm.createNotificationChannel(reminderChannel)
        }
    }

    fun showPrayerStartNotification(prayerType: PrayerType, formattedTime: String, soundName: String, notificationId: Int) {
        val channelId = getDynamicChannelId(BASE_CHANNEL_START, soundName)
        ensureChannelWithSound(channelId, "${prayerType.banglaName} ওয়াক্ত শুরু", soundName)

        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "${prayerType.banglaName} নামাজের ওয়াক্ত শুরু হয়েছে"
        val message = "ওয়াক্তের সময়: $formattedTime। সময়মতো জামাতের সাথে সালাত আদায় করুন।"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_crescent_moon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun showPrayerEndingSoonNotification(
        prayerType: PrayerType,
        remainingMinutes: Int,
        dateString: String,
        soundName: String,
        notificationId: Int
    ) {
        val channelId = getDynamicChannelId(BASE_CHANNEL_END, soundName)
        ensureChannelWithSound(channelId, "ওয়াক্ত সমাপ্তি সতর্কতা", soundName)

        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // অ্যাকশন বাটন ১: “আদায় করেছি” (ট্যাপ করলে ডাটাবেজে অটো-টিক হয়ে যাবে)
        val completedIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_PRAYER_COMPLETED
            putExtra(EXTRA_PRAYER_ID, prayerType.id)
            putExtra(EXTRA_DATE_STRING, dateString)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val completedPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            completedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // অ্যাকশন বাটন ২: “এখনো পড়িনি”
        val dismissIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_DISMISS_OR_SNOOZE
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "${prayerType.banglaName} ওয়াক্ত শেষ হতে আর মাত্র $remainingMinutes মিনিট বাকি"
        val message = "আপনি কি নামাজ পড়েছেন? ওয়াক্ত শেষ হওয়ার পূর্বেই আদায় করে নিন।"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_crescent_moon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .addAction(0, "আদায় করেছি", completedPendingIntent)
            .addAction(0, "এখনো পড়িনি", dismissPendingIntent)

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}