package com.taqwanet.app.data.repository

import com.taqwanet.app.core.calculation.AsrJuristic
import com.taqwanet.app.core.calculation.CalculationMethod
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.domain.model.CityLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataSource: AppPreferencesDataSource) {

    val selectedCityName: Flow<String> = dataSource.selectedCityName
    val coordinates: Flow<Triple<Double, Double, Double>> = dataSource.coordinates

    val calculationMethod: Flow<CalculationMethod> = dataSource.calculationMethod.map {
        try { CalculationMethod.valueOf(it) } catch (e: Exception) { CalculationMethod.KARACHI }
    }

    val asrJuristic: Flow<AsrJuristic> = dataSource.asrJuristic.map {
        try { AsrJuristic.valueOf(it) } catch (e: Exception) { AsrJuristic.HANAFI }
    }

    val hijriOffset: Flow<Int> = dataSource.hijriOffset
    val completionVoiceEnabled: Flow<Boolean> = dataSource.completionVoiceEnabled
    val celebrationAnimationEnabled: Flow<Boolean> = dataSource.celebrationAnimationEnabled
    val adhanType: Flow<String> = dataSource.adhanType
    val reminderLeadMinutes: Flow<Int> = dataSource.reminderLeadMinutes
    val notificationSound: Flow<String> = dataSource.notificationSound

    suspend fun updateCity(city: CityLocation) {
        dataSource.saveLocation(city.nameBn, city.nameEn, city.latitude, city.longitude, city.timezoneOffsetHours)
    }

    suspend fun updateCalculationMethod(method: CalculationMethod) = dataSource.setCalculationMethod(method.name)
    suspend fun updateAsrJuristic(juristic: AsrJuristic) = dataSource.setAsrJuristic(juristic.name)
    suspend fun updateHijriOffset(offset: Int) = dataSource.setHijriOffset(offset)
    suspend fun setCompletionVoiceEnabled(enabled: Boolean) = dataSource.setCompletionVoiceEnabled(enabled)
    suspend fun setCelebrationEnabled(enabled: Boolean) = dataSource.setCelebrationEnabled(enabled)
    suspend fun setAdhanType(type: String) = dataSource.setAdhanType(type)
    suspend fun setReminderLeadMinutes(minutes: Int) = dataSource.setReminderLeadMinutes(minutes)
    suspend fun setNotificationSound(sound: String) = dataSource.setNotificationSound(sound)
}