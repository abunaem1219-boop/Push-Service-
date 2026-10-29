package com.taqwanet.app.domain.model

import java.time.LocalTime

enum class PeriodKind {
    FAJR,
    SUNRISE_PROHIBITED,
    DUHA_ISHRAQ,
    ZAWAL_PROHIBITED,
    DHUHR,
    ASR,
    SUNSET_PROHIBITED,
    MAGHRIB,
    ISHA
}

data class CurrentPeriodState(
    val titleBn: String = "যোহর ওয়াক্ত চলছে",
    val titleAr: String = "الظهر",
    val isProhibited: Boolean = false,
    val endTime: LocalTime = LocalTime.now(),
    val remainingTimeText: String = "০১ ঘণ্টা ২৩ মিনিট",
    val nextPrayerName: String = "আসর",
    val periodKind: PeriodKind = PeriodKind.DHUHR
)