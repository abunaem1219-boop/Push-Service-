package com.taqwanet.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "taqwanet_prefs")

class AppPreferencesDataSource(private val context: Context) {

    companion object {
        val KEY_SELECTED_CITY_BN = stringPreferencesKey("selected_city_bn")
        val KEY_SELECTED_CITY_EN = stringPreferencesKey("selected_city_en")
        val KEY_LATITUDE = doublePreferencesKey("latitude")
        val KEY_LONGITUDE = doublePreferencesKey("longitude")
        val KEY_TIMEZONE_OFFSET = doublePreferencesKey("timezone_offset")
        val KEY_CALCULATION_METHOD = stringPreferencesKey("calculation_method")
        val KEY_ASR_JURISTIC = stringPreferencesKey("asr_juristic")
        val KEY_HIJRI_OFFSET = intPreferencesKey("hijri_offset")

        val KEY_COMPLETION_VOICE_ENABLED = booleanPreferencesKey("completion_voice_enabled")
        val KEY_CELEBRATION_ANIMATION_ENABLED = booleanPreferencesKey("celebration_animation_enabled")
        val KEY_ADHAN_TYPE = stringPreferencesKey("adhan_type")
        val KEY_REMINDER_LEAD_MINUTES = intPreferencesKey("reminder_lead_minutes")
        val KEY_NOTIFICATION_SOUND = stringPreferencesKey("notification_sound")
        val KEY_FRIDAY_REMINDER_ENABLED = booleanPreferencesKey("friday_reminder_enabled")
        val KEY_AYYAM_AL_BEED_ENABLED = booleanPreferencesKey("ayyam_al_beed_enabled")
    }

    private val safePreferencesFlow: Flow<Preferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    val selectedCityName: Flow<String> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_SELECTED_CITY_BN] ?: "ঢাকা"
    }

    val coordinates: Flow<Triple<Double, Double, Double>> = safePreferencesFlow.map { preferences: Preferences ->
        val lat = preferences[KEY_LATITUDE] ?: 23.8103
        val lon = preferences[KEY_LONGITUDE] ?: 90.4125
        val tz = preferences[KEY_TIMEZONE_OFFSET] ?: 6.0
        Triple(lat, lon, tz)
    }

    val calculationMethod: Flow<String> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_CALCULATION_METHOD] ?: "KARACHI"
    }

    val asrJuristic: Flow<String> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_ASR_JURISTIC] ?: "HANAFI"
    }

    val hijriOffset: Flow<Int> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_HIJRI_OFFSET] ?: 0
    }

    val completionVoiceEnabled: Flow<Boolean> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_COMPLETION_VOICE_ENABLED] ?: true
    }

    val celebrationAnimationEnabled: Flow<Boolean> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_CELEBRATION_ANIMATION_ENABLED] ?: true
    }

    val adhanType: Flow<String> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_ADHAN_TYPE] ?: "SHORT"
    }

    val reminderLeadMinutes: Flow<Int> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_REMINDER_LEAD_MINUTES] ?: 10
    }

    val notificationSound: Flow<String> = safePreferencesFlow.map { preferences: Preferences ->
        preferences[KEY_NOTIFICATION_SOUND] ?: "notification_default"
    }

    suspend fun saveLocation(nameBn: String, nameEn: String, lat: Double, lon: Double, tz: Double) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_CITY_BN] = nameBn
            preferences[KEY_SELECTED_CITY_EN] = nameEn
            preferences[KEY_LATITUDE] = lat
            preferences[KEY_LONGITUDE] = lon
            preferences[KEY_TIMEZONE_OFFSET] = tz
        }
    }

    suspend fun setCalculationMethod(method: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CALCULATION_METHOD] = method
        }
    }

    suspend fun setAsrJuristic(juristic: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ASR_JURISTIC] = juristic
        }
    }

    suspend fun setHijriOffset(offset: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HIJRI_OFFSET] = offset
        }
    }

    suspend fun setCompletionVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_COMPLETION_VOICE_ENABLED] = enabled
        }
    }

    suspend fun setCelebrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CELEBRATION_ANIMATION_ENABLED] = enabled
        }
    }

    suspend fun setAdhanType(type: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ADHAN_TYPE] = type
        }
    }

    suspend fun setReminderLeadMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_REMINDER_LEAD_MINUTES] = minutes
        }
    }

    suspend fun setNotificationSound(soundName: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION_SOUND] = soundName
        }
    }
}