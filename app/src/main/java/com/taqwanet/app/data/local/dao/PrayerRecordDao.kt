package com.taqwanet.app.data.local.dao

import com.taqwanet.app.data.local.entity.PrayerRecordEntity
import kotlinx.coroutines.flow.Flow

interface PrayerRecordDao {
    fun getRecordsForDate(date: String): Flow<List<PrayerRecordEntity>>
    suspend fun insertOrUpdateRecord(record: PrayerRecordEntity)
    fun getRecordsBetweenDates(startDate: String, endDate: String): Flow<List<PrayerRecordEntity>>
    fun getTotalCompletedPrayersCount(): Flow<Int>
}