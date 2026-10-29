package com.taqwanet.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.taqwanet.app.core.calculation.PrayerTimesCalculator
import com.taqwanet.app.core.notification.receivers.PrayerAlarmReceiver
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.domain.model.PrayerType
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.AUDIO_SERVICE.let { Context.ALARM_SERVICE }) as AlarmManager
    private val calculator = PrayerTimesCalculator()

    companion object {
        const val ALARM_TYPE_START = "alarm_start"
        const val ALARM_TYPE_ENDING = "alarm_ending"

        const val EXTRA_PRAYER_TYPE = "extra_prayer_type"
        const val EXTRA_ALARM_KIND = "extra_alarm_kind"
        const val EXTRA_FORMATTED_TIME = "extra_formatted_time"
        const val EXTRA_LEAD_MINUTES = "extra_lead_minutes"
    }

    suspend fun scheduleDailyPrayerAlarms(preferences: AppPreferencesDataSource) {
        val (lat, lon, tz) = preferences.coordinates.first()
        val leadMinutes = preferences.reminderLeadMinutes.first()
        val today = LocalDate.now()

        val dailyTimes = calculator.calculateTimes(today, lat, lon, tz)
        val prayerList = listOf(
            Pair(PrayerType.FAJR, dailyTimes.fajr),
            Pair(PrayerType.DHUHR, dailyTimes.dhuhr),
            Pair(PrayerType.ASR, dailyTimes.asr),
            Pair(PrayerType.MAGHRIB, dailyTimes.maghrib),
            Pair(PrayerType.ISHA, dailyTimes.isha)
        )

        val now = LocalDateTime.now()
        val zoneId = ZoneId.systemDefault()

        for (item in prayerList) {
            val prayerType = item.first
            val prayerLocalTime = item.second
            val prayerDateTime = LocalDateTime.of(today, prayerLocalTime)

            // ১. ওয়াক্ত শুরুর এলার্ম শিডিউল
            if (prayerDateTime.isAfter(now)) {
                val triggerEpochMillis = prayerDateTime.atZone(zoneId).toInstant().toEpochMilli()
                val requestCode = prayerType.ordinal * 100 + 1
                scheduleExact(triggerEpochMillis, requestCode, prayerType, ALARM_TYPE_START, prayerLocalTime.toString(), 0)
            }

            // ২. ওয়াক্ত সমাপ্তির সতর্কবার্তা (যেমন: যোহরের শেষ সময় = আসর শুরুর ১০ মিনিট আগে)
            val nextPrayerTime = when (prayerType) {
                PrayerType.FAJR -> dailyTimes.sunrise
                PrayerType.DHUHR -> dailyTimes.asr
                PrayerType.ASR -> dailyTimes.maghrib
                PrayerType.MAGHRIB -> dailyTimes.isha
                PrayerType.ISHA -> dailyTimes.fajr.plusHours(24) // পরবর্তী ফজর
                else -> null
            }

            nextPrayerTime?.let {
                val warningDateTime = LocalDateTime.of(today, it).minusMinutes(leadMinutes.toLong())
                if (warningDateTime.isAfter(now)) {
                    val triggerEpochMillis = warningDateTime.atZone(zoneId).toInstant().toEpochMilli()
                    val requestCode = prayerType.ordinal * 100 + 2
                    scheduleExact(triggerEpochMillis, requestCode, prayerType, ALARM_TYPE_ENDING, it.toString(), leadMinutes)
                }
            }
        }
    }

    private fun scheduleExact(
        triggerEpochMillis: Long,
        requestCode: Int,
        prayerType: PrayerType,
        alarmKind: String,
        timeStr: String,
        leadMinutes: Int
    ) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_TYPE, prayerType.id)
            putExtra(EXTRA_ALARM_KIND, alarmKind)
            putExtra(EXTRA_FORMATTED_TIME, timeStr)
            putExtra(EXTRA_LEAD_MINUTES, leadMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerEpochMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerEpochMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerEpochMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "SecurityException scheduling exact alarm: ${e.message}")
        }
    }
}