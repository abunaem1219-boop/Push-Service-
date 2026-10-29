package com.taqwanet.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taqwanet.app.core.audio.AudioPlaybackManager
import com.taqwanet.app.core.calculation.HijriCalendarHelper
import com.taqwanet.app.core.calculation.HijriDate
import com.taqwanet.app.core.calculation.PrayerTimesCalculator
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.notification.AlarmScheduler
import com.taqwanet.app.data.local.AppDatabase
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.data.remote.RemoteConfigManager
import com.taqwanet.app.data.repository.PrayerRepository
import com.taqwanet.app.data.repository.SettingsRepository
import com.taqwanet.app.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class HomeUiState(
    val currentTime: LocalTime = LocalTime.now(),
    val currentPeriod: CurrentPeriodState = CurrentPeriodState(),
    val cityNameBn: String = "ঢাকা",
    val hijriDate: HijriDate = HijriCalendarHelper.getHijriDate(LocalDate.now()),
    val prayerList: List<PrayerTime> = emptyList(),
    val celebrationPrayerName: String? = null,
    val isCityPickerVisible: Boolean = false,
    val updateInfo: AppUpdateInfo? = null // ফায়ারবেস আপডেট স্টেট
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val prefs = AppPreferencesDataSource(application)
    private val prayerRepo = PrayerRepository(db.prayerRecordDao())
    private val settingsRepo = SettingsRepository(prefs)
    private val audioManager = AudioPlaybackManager.getInstance(application)
    private val alarmScheduler = AlarmScheduler(application)
    private val calculator = PrayerTimesCalculator()
    private val remoteConfigManager = RemoteConfigManager(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        startClockTicker()
        checkAppUpdates()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                settingsRepo.selectedCityName,
                settingsRepo.coordinates,
                settingsRepo.calculationMethod,
                settingsRepo.asrJuristic,
                settingsRepo.hijriOffset
            ) { city, coords, method, juristic, offset ->
                val today = LocalDate.now()
                val now = LocalTime.now()
                val hijri = HijriCalendarHelper.getHijriDate(today, offset)
                val daily = calculator.calculateTimes(today, coords.first, coords.second, coords.third, method, juristic)
                val periodState = computePeriodState(now, daily)

                _uiState.update {
                    it.copy(
                        cityNameBn = city,
                        hijriDate = hijri,
                        currentPeriod = periodState
                    )
                }

                prayerRepo.getDayPrayerSchedule(today, coords.first, coords.second, coords.third, method, juristic)
            }.flatMapLatest { it }.collect { prayers ->
                _uiState.update { it.copy(prayerList = prayers) }
            }
        }
    }

    // ফায়ারবেস থেকে আপডেট চেক করার পদ্ধতি
    private fun checkAppUpdates() {
        viewModelScope.launch {
            try {
                val update = remoteConfigManager.checkForUpdates()
                if (update.isUpdateAvailable) {
                    _uiState.update { it.copy(updateInfo = update) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun dismissUpdateDialog() {
        _uiState.update { it.copy(updateInfo = null) }
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (true) {
                val now = LocalTime.now()
                val (lat, lon, tz) = settingsRepo.coordinates.first()
                val method = settingsRepo.calculationMethod.first()
                val juristic = settingsRepo.asrJuristic.first()
                val daily = calculator.calculateTimes(LocalDate.now(), lat, lon, tz, method, juristic)
                val periodState = computePeriodState(now, daily)

                _uiState.update {
                    it.copy(
                        currentTime = now,
                        currentPeriod = periodState
                    )
                }
                delay(1000)
            }
        }
    }

    private fun computePeriodState(now: LocalTime, daily: PrayerTimesCalculator.DailyCalculatedTimes): CurrentPeriodState {
        val sunriseEnd = daily.sunrise.plusMinutes(15)
        val zawalStart = daily.dhuhr.minusMinutes(10)
        val sunsetStart = daily.maghrib.minusMinutes(15)

        fun diff(target: LocalTime): String {
            val secs = ChronoUnit.SECONDS.between(now, target).let {
                if (it < 0) it + 86400 else it
            }
            val h = secs / 3600
            val m = (secs % 3600) / 60
            val s = secs % 60
            val hStr = h.toString().toBanglaDigits()
            val mStr = m.toString().toBanglaDigits()
            val sStr = s.toString().toBanglaDigits()
            return if (h > 0) "$hStr ঘণ্টা $mStr মিনিট $sStr সেকেন্ড" else "$mStr মিনিট $sStr সেকেন্ড"
        }

        return when {
            !now.isBefore(daily.fajr) && now.isBefore(daily.sunrise) -> {
                CurrentPeriodState("ফজর ওয়াক্ত চলছে", "الفجر", false, daily.sunrise, diff(daily.sunrise), "সূর্যোদয়", PeriodKind.FAJR)
            }
            !now.isBefore(daily.sunrise) && now.isBefore(sunriseEnd) -> {
                CurrentPeriodState("সূর্যোদয় (সালাত নিষিদ্ধ)", "الشروق", true, sunriseEnd, diff(sunriseEnd), "ইশরাক", PeriodKind.SUNRISE_PROHIBITED)
            }
            !now.isBefore(sunriseEnd) && now.isBefore(zawalStart) -> {
                CurrentPeriodState("ইশরাক ও চাশতের ওয়াক্ত", "الضحى", false, zawalStart, diff(zawalStart), "যোহর", PeriodKind.DUHA_ISHRAQ)
            }
            !now.isBefore(zawalStart) && now.isBefore(daily.dhuhr) -> {
                CurrentPeriodState("দ্বিপ্রহর (সালাত নিষিদ্ধ)", "الزوال", true, daily.dhuhr, diff(daily.dhuhr), "যোহর", PeriodKind.ZAWAL_PROHIBITED)
            }
            !now.isBefore(daily.dhuhr) && now.isBefore(daily.asr) -> {
                CurrentPeriodState("যোহর ওয়াক্ত চলছে", "الظهر", false, daily.asr, diff(daily.asr), "আসর", PeriodKind.DHUHR)
            }
            !now.isBefore(daily.asr) && now.isBefore(sunsetStart) -> {
                CurrentPeriodState("আসর ওয়াক্ত চলছে", "العصر", false, sunsetStart, diff(sunsetStart), "মাগরিব", PeriodKind.ASR)
            }
            !now.isBefore(sunsetStart) && now.isBefore(daily.maghrib) -> {
                CurrentPeriodState("সূর্যাস্ত (সালাত নিষিদ্ধ)", "الغروب", true, daily.maghrib, diff(daily.maghrib), "মাগরিব", PeriodKind.SUNSET_PROHIBITED)
            }
            !now.isBefore(daily.maghrib) && now.isBefore(daily.isha) -> {
                CurrentPeriodState("মাগরিব ওয়াক্ত চলছে", "المغرب", false, daily.isha, diff(daily.isha), "এশা", PeriodKind.MAGHRIB)
            }
            else -> {
                CurrentPeriodState("এশা ওয়াক্ত চলছে", "العشاء", false, daily.fajr, diff(daily.fajr), "ফজর", PeriodKind.ISHA)
            }
        }
    }

    fun showCityPicker() = _uiState.update { it.copy(isCityPickerVisible = true) }
    fun hideCityPicker() = _uiState.update { it.copy(isCityPickerVisible = false) }

    fun onSelectCity(city: CityLocation) {
        viewModelScope.launch {
            settingsRepo.updateCity(city)
            alarmScheduler.scheduleDailyPrayerAlarms(prefs)
        }
    }

    fun onTogglePrayer(prayer: PrayerTime) {
        viewModelScope.launch {
            val isNowCompleted = prayerRepo.togglePrayerCompletion(LocalDate.now(), prayer.type, prayer.isCompleted)
            if (isNowCompleted) {
                val isVoiceEnabled = settingsRepo.completionVoiceEnabled.first()
                if (isVoiceEnabled) {
                    audioManager.playPrayerCompletionVoice(prayer.type)
                }
                _uiState.update { it.copy(celebrationPrayerName = prayer.type.banglaName) }
                delay(3200)
                _uiState.update { it.copy(celebrationPrayerName = null) }
            }
        }
    }
}