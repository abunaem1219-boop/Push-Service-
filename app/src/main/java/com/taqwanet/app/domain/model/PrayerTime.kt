package com.taqwanet.app.domain.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PrayerTime(
    val type: PrayerType,
    val time: LocalTime,
    val isCurrent: Boolean = false,
    val isNext: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAtTimestamp: Long? = null
) {
    val formattedTime12Hour: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
            val timeString = time.format(formatter)
            return timeString
                .replace("AM", "পূর্বাহ্ন")
                .replace("PM", "অপরাহ্ন")
                .replace("0", "০")
                .replace("1", "১")
                .replace("2", "২")
                .replace("3", "৩")
                .replace("4", "৪")
                .replace("5", "৫")
                .replace("6", "৬")
                .replace("7", "৭")
                .replace("8", "৮")
                .replace("9", "৯")
        }
}