package com.taqwanet.app.data.repository

import com.taqwanet.app.core.calculation.AsrJuristic
import com.taqwanet.app.core.calculation.CalculationMethod
import com.taqwanet.app.core.calculation.PrayerTimesCalculator
import com.taqwanet.app.data.local.dao.PrayerRecordDao
import com.taqwanet.app.data.local.entity.PrayerRecordEntity
import com.taqwanet.app.domain.model.PrayerTime
import com.taqwanet.app.domain.model.PrayerType
import com.taqwanet.app.domain.model.ProhibitedTimeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PrayerRepository(
    private val prayerRecordDao: PrayerRecordDao,
    private val calculator: PrayerTimesCalculator = PrayerTimesCalculator()
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getDayPrayerSchedule(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        tzOffset: Double,
        method: CalculationMethod,
        juristic: AsrJuristic
    ): Flow<List<PrayerTime>> {
        val dateString = date.format(dateFormatter)
        val calculated = calculator.calculateTimes(date, latitude, longitude, tzOffset, method, juristic)

        return prayerRecordDao.getRecordsForDate(dateString).combine(
            kotlinx.coroutines.flow.flowOf(LocalTime.now())
        ) { records, currentTime ->
            val recordMap = records.associateBy { it.prayerId }

            val rawTimes = listOf(
                PrayerTime(PrayerType.FAJR, calculated.fajr),
                PrayerTime(PrayerType.SUNRISE, calculated.sunrise),
                PrayerTime(PrayerType.DHUHR, calculated.dhuhr),
                PrayerTime(PrayerType.ASR, calculated.asr),
                PrayerTime(PrayerType.MAGHRIB, calculated.maghrib),
                PrayerTime(PrayerType.ISHA, calculated.isha)
            )

            // বর্তমান ও পরবর্তী ওয়াক্ত নির্ণয়
            val nextIndex = rawTimes.indexOfFirst { it.time.isAfter(currentTime) }.let {
                if (it == -1) 0 else it // যদি সব পার হয়ে যায়, তবে পরবর্তী ফজর
            }
            val currentIndex = if (nextIndex == 0 && currentTime.isAfter(calculated.isha)) {
                rawTimes.lastIndex
            } else {
                (nextIndex - 1).coerceAtLeast(0)
            }

            rawTimes.mapIndexed { index, item ->
                val record = recordMap[item.type.id]
                item.copy(
                    isCurrent = index == currentIndex,
                    isNext = index == nextIndex,
                    isCompleted = record?.isCompleted ?: false,
                    completedAtTimestamp = record?.completedAt
                )
            }
        }
    }

    fun getProhibitedState(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        tzOffset: Double,
        method: CalculationMethod,
        juristic: AsrJuristic
    ): ProhibitedTimeState {
        val calculated = calculator.calculateTimes(date, latitude, longitude, tzOffset, method, juristic)
        return calculator.checkProhibitedTime(LocalTime.now(), calculated)
    }

    suspend fun togglePrayerCompletion(
        date: LocalDate,
        prayerType: PrayerType,
        isCurrentlyCompleted: Boolean
    ): Boolean {
        val dateString = date.format(dateFormatter)
        val newStatus = !isCurrentlyCompleted
        val timestamp = if (newStatus) System.currentTimeMillis() else null

        val entity = PrayerRecordEntity(
            dateString = dateString,
            prayerId = prayerType.id,
            isCompleted = newStatus,
            completedAt = timestamp
        )
        prayerRecordDao.insertOrUpdateRecord(entity)
        return newStatus
    }

    fun getPrayerRecordsForMonth(startDate: LocalDate, endDate: LocalDate): Flow<List<PrayerRecordEntity>> {
        return prayerRecordDao.getRecordsBetweenDates(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        )
    }
}