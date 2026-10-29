package com.taqwanet.app.core.calculation

import com.taqwanet.app.domain.model.PrayerTime
import com.taqwanet.app.domain.model.PrayerType
import com.taqwanet.app.domain.model.ProhibitedPeriod
import com.taqwanet.app.domain.model.ProhibitedTimeState
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.*

enum class CalculationMethod(val fajrAngle: Double, val ishaAngle: Double, val methodNameBn: String) {
    KARACHI(18.0, 18.0, "করাচি ইসলামিক বিশ্ববিদ্যালয় (হানাফি প্রাধান্য)"),
    MUSLIM_WORLD_LEAGUE(18.0, 17.0, "মুসলিম ওয়ার্ল্ড লীগ"),
    UMM_AL_QURA(18.5, 19.0, "উম্মুল কুরা (মক্কা মুকাররমা)"),
    EGYPTIAN(19.5, 17.5, "মিশরীয় জেনারেল অথরিটি")
}

enum class AsrJuristic(val shadowFactor: Double, val titleBn: String) {
    HANAFI(2.0, "হানাফি (ছায়ার অনুপাত দ্বিগুণ)"),
    STANDARD(1.0, "শাফিয়ী, মালিকি, হাম্বলী (ছায়ার অনুপাত একগুণ)")
}

class PrayerTimesCalculator {

    data class DailyCalculatedTimes(
        val fajr: LocalTime,
        val sunrise: LocalTime,
        val dhuhr: LocalTime,
        val asr: LocalTime,
        val maghrib: LocalTime,
        val isha: LocalTime,
        val zawalStart: LocalTime
    )

    fun calculateTimes(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        timezoneOffset: Double,
        method: CalculationMethod = CalculationMethod.KARACHI,
        juristic: AsrJuristic = AsrJuristic.HANAFI,
        manualOffsetMinutes: Map<PrayerType, Int> = emptyMap()
    ): DailyCalculatedTimes {
        val julianDay = getJulianDay(date.year, date.monthValue, date.dayOfMonth)
        val d = julianDay - 2451545.0

        // সৌর দ্রাঘিমাংশ ও অ্যানোমালি
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(toRadians(g)) + 0.020 * sin(toRadians(2 * g)))

        // পৃথিবীর অক্ষের হেলানো কোণ
        val e = 23.439 - 0.00000036 * d
        val declination = toDegrees(asin(sin(toRadians(e)) * sin(toRadians(l))))
        val ra = toDegrees(atan2(cos(toRadians(e)) * sin(toRadians(l)), cos(toRadians(l)))) / 15.0
        val raFixed = fixHour(ra)

        // সময়ের সমীকরণ (Equation of Time)
        val eqT = q / 15.0 - raFixed

        // মধ্যাহ্ন / সোলার নুন (Solar Noon)
        val noon = fixHour(12.0 + timezoneOffset - (longitude / 15.0) - eqT)

        // দিগন্ত কোণের সমীকরণ
        fun hourAngle(angle: Double): Double {
            val term = (-sin(toRadians(angle)) - sin(toRadians(latitude)) * sin(toRadians(declination))) /
                    (cos(toRadians(latitude)) * cos(toRadians(declination)))
            val clamped = term.coerceIn(-1.0, 1.0)
            return toDegrees(acos(clamped)) / 15.0
        }

        // আসরের কোণ সমীকরণ
        fun asrHourAngle(factor: Double): Double {
            val angleTerm = -atan(1.0 / (factor + tan(toRadians(abs(latitude - declination)))))
            val alt = toDegrees(angleTerm)
            val term = (sin(toRadians(-alt)) - sin(toRadians(latitude)) * sin(toRadians(declination))) /
                    (cos(toRadians(latitude)) * cos(toRadians(declination)))
            val clamped = term.coerceIn(-1.0, 1.0)
            return toDegrees(acos(clamped)) / 15.0
        }

        val sunriseHour = noon - hourAngle(0.833)
        val sunsetHour = noon + hourAngle(0.833)
        val fajrHour = noon - hourAngle(method.fajrAngle)
        val ishaHour = noon + hourAngle(method.ishaAngle)
        val asrHour = noon + asrHourAngle(juristic.shadowFactor)

        fun toLocalTime(hourFraction: Double, offsetMinutes: Int = 0): LocalTime {
            val totalMinutes = (fixHour(hourFraction) * 60.0).roundToInt() + offsetMinutes
            val safeMinutes = ((totalMinutes % 1440) + 1440) % 1440
            return LocalTime.of(safeMinutes / 60, safeMinutes % 60)
        }

        val dhuhrTime = toLocalTime(noon, manualOffsetMinutes[PrayerType.DHUHR] ?: 0)
        val zawalStartTime = dhuhrTime.minusMinutes(10) // দ্বিপ্রহরের নিষিদ্ধ সময় যোহরের ১০ মিনিট আগে

        return DailyCalculatedTimes(
            fajr = toLocalTime(fajrHour, manualOffsetMinutes[PrayerType.FAJR] ?: 0),
            sunrise = toLocalTime(sunriseHour, manualOffsetMinutes[PrayerType.SUNRISE] ?: 0),
            dhuhr = dhuhrTime,
            asr = toLocalTime(asrHour, manualOffsetMinutes[PrayerType.ASR] ?: 0),
            maghrib = toLocalTime(sunsetHour, manualOffsetMinutes[PrayerType.MAGHRIB] ?: 0),
            isha = toLocalTime(ishaHour, manualOffsetMinutes[PrayerType.ISHA] ?: 0),
            zawalStart = zawalStartTime
        )
    }

    fun checkProhibitedTime(
        currentTime: LocalTime,
        times: DailyCalculatedTimes
    ): ProhibitedTimeState {
        // ১. সূর্যোদয়ের নিষিদ্ধ সময় (সূর্যোদয় থেকে ১৫ মিনিট পর পর্যন্ত)
        val sunriseEnd = times.sunrise.plusMinutes(15)
        if (!currentTime.isBefore(times.sunrise) && currentTime.isBefore(sunriseEnd)) {
            val rem = ChronoUnit.MINUTES.between(currentTime, sunriseEnd)
            return ProhibitedTimeState(true, ProhibitedPeriod.SUNRISE, times.sunrise, sunriseEnd, rem)
        }

        // ২. ঠিক দ্বিপ্রহর বা জাওয়াল (যোহরের ১০ মিনিট পূর্ব থেকে যোহরের ওয়াক্ত পর্যন্ত)
        if (!currentTime.isBefore(times.zawalStart) && currentTime.isBefore(times.dhuhr)) {
            val rem = ChronoUnit.MINUTES.between(currentTime, times.dhuhr)
            return ProhibitedTimeState(true, ProhibitedPeriod.ZAWAL, times.zawalStart, times.dhuhr, rem)
        }

        // ৩. সূর্যাস্তের নিষিদ্ধ সময় (মাগরিবের ১৫ মিনিট পূর্ব থেকে মাগরিব পর্যন্ত)
        val sunsetStart = times.maghrib.minusMinutes(15)
        if (!currentTime.isBefore(sunsetStart) && currentTime.isBefore(times.maghrib)) {
            val rem = ChronoUnit.MINUTES.between(currentTime, times.maghrib)
            return ProhibitedTimeState(true, ProhibitedPeriod.SUNSET, sunsetStart, times.maghrib, rem)
        }

        return ProhibitedTimeState(false, ProhibitedPeriod.NONE, null, null, 0)
    }

    private fun getJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(a: Double): Double = (a % 360.0 + 360.0) % 360.0
    private fun fixHour(h: Double): Double = (h % 24.0 + 24.0) % 24.0
    private fun toRadians(d: Double): Double = d * (PI / 180.0)
    private fun toDegrees(r: Double): Double = r * (180.0 / PI)
}