package com.taqwanet.app.feature.prayer

import com.taqwanet.app.domain.model.PrayerTime
import java.time.LocalDate
import java.time.LocalTime

data class AdditionalIslamicTimes(
    val tahajjudEnd: LocalTime,
    val sahurEnd: LocalTime,
    val ishraq: LocalTime,
    val chasht: LocalTime,
    val iftar: LocalTime
)

data class MonthlyPrayerStat(
    val totalFardhPrayers: Int = 150,
    val completedCount: Int = 0,
    val completionPercentage: Int = 0
)

data class PrayerScreenUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val isToday: Boolean = true,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val additionalTimes: AdditionalIslamicTimes? = null,
    val monthlyStat: MonthlyPrayerStat = MonthlyPrayerStat()
)