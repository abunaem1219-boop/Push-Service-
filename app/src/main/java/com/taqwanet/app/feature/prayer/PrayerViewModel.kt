package com.taqwanet.app.feature.prayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taqwanet.app.core.calculation.PrayerTimesCalculator
import com.taqwanet.app.data.local.AppDatabase
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.data.repository.PrayerRepository
import com.taqwanet.app.data.repository.SettingsRepository
import com.taqwanet.app.domain.model.PrayerTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val prefs = AppPreferencesDataSource(application)
    private val prayerRepo = PrayerRepository(db.prayerRecordDao())
    private val settingsRepo = SettingsRepository(prefs)
    private val calculator = PrayerTimesCalculator()

    private val _uiState = MutableStateFlow(PrayerScreenUiState())
    val uiState: StateFlow<PrayerScreenUiState> = _uiState.asStateFlow()

    init {
        loadScheduleForDate(LocalDate.now())
        loadMonthlyStats()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, isToday = date == LocalDate.now()) }
        loadScheduleForDate(date)
    }

    private fun loadScheduleForDate(date: LocalDate) {
        viewModelScope.launch {
            combine(
                settingsRepo.coordinates,
                settingsRepo.calculationMethod,
                settingsRepo.asrJuristic
            ) { coords, method, juristic ->
                val daily = calculator.calculateTimes(date, coords.first, coords.second, coords.third, method, juristic)
                
                // নফল ও বিশেষ ওয়াক্ত
                val extraTimes = AdditionalIslamicTimes(
                    tahajjudEnd = daily.fajr.minusMinutes(20),
                    sahurEnd = daily.fajr.minusMinutes(5),
                    ishraq = daily.sunrise.plusMinutes(20),
                    chasht = daily.sunrise.plusMinutes(45),
                    iftar = daily.maghrib
                )

                _uiState.update { it.copy(additionalTimes = extraTimes) }
                prayerRepo.getDayPrayerSchedule(date, coords.first, coords.second, coords.third, method, juristic)
            }.flatMapLatest { it }.collect { prayers ->
                _uiState.update { it.copy(prayerTimes = prayers) }
            }
        }
    }

    private fun loadMonthlyStats() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val start = now.withDayOfMonth(1)
            val end = now.withDayOfMonth(now.lengthOfMonth())

            prayerRepo.getPrayerRecordsForMonth(start, end).collect { records ->
                val completed = records.count { it.isCompleted }
                val totalPossible = now.dayOfMonth * 5
                val percent = if (totalPossible > 0) ((completed.toDouble() / totalPossible) * 100).toInt() else 0

                _uiState.update {
                    it.copy(
                        monthlyStat = MonthlyPrayerStat(
                            totalFardhPrayers = totalPossible,
                            completedCount = completed,
                            completionPercentage = percent
                        )
                    )
                }
            }
        }
    }

    fun togglePrayer(prayer: PrayerTime) {
        viewModelScope.launch {
            prayerRepo.togglePrayerCompletion(_uiState.value.selectedDate, prayer.type, prayer.isCompleted)
            loadMonthlyStats()
        }
    }
}