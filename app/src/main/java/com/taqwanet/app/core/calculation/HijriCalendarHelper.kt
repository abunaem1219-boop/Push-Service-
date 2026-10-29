package com.taqwanet.app.core.calculation

import java.time.LocalDate
import kotlin.math.floor

data class HijriDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthNameBn: String
) {
    val formattedBn: String
        get() {
            val dayBn = day.toString().toBanglaDigits()
            val yearBn = year.toString().toBanglaDigits()
            return "$dayBn $monthNameBn, $yearBn হিজরি"
        }
}

object HijriCalendarHelper {

    private val HIJRI_MONTHS_BN = listOf(
        "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শাবান",
        "রমাদান", "শাওয়াল", "জিলকদ", "জিলহজ"
    )

    fun getHijriDate(gregorianDate: LocalDate, manualDayAdjustment: Int = 0): HijriDate {
        val adjustedDate = gregorianDate.plusDays(manualDayAdjustment.toLong())
        val day = adjustedDate.dayOfMonth
        val month = adjustedDate.monthValue
        val year = adjustedDate.year

        var m = month
        var y = year
        if (m < 3) {
            y -= 1
            m += 12
        }

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524

        val z = jd - 1948440 + 10632
        val n = floor((z - 1) / 10631.0)
        val z1 = z - 10631 * n + 354
        val j = (floor((10985 - z1) / 5316.0)) * (floor((50 * z1) / 17719.0)) +
                (floor(z1 / 5670.0)) * (floor((43 * z1) / 15238.0))
        val z2 = z1 - (floor((30 - j) / 15.0)) * (floor((17719 * j) / 50.0)) -
                (floor(j / 16.0)) * (floor((15238 * j) / 43.0)) + 29
        val hMonth = floor((24 * z2) / 709.0).toInt()
        val hDay = (z2 - floor((709 * hMonth) / 24.0)).toInt()
        val hYear = (30 * n + j - 30).toInt()

        val safeMonthIndex = ((hMonth - 1) % 12 + 12) % 12
        return HijriDate(
            day = hDay,
            month = safeMonthIndex + 1,
            year = hYear,
            monthNameBn = HIJRI_MONTHS_BN[safeMonthIndex]
        )
    }

    fun isAyyamAlBeed(hijriDate: HijriDate): Boolean {
        return hijriDate.day in 13..15
    }
}

fun String.toBanglaDigits(): String {
    return this.replace('0', '০')
        .replace('1', '১')
        .replace('2', '২')
        .replace('3', '৩')
        .replace('4', '৪')
        .replace('5', '৫')
        .replace('6', '৬')
        .replace('7', '৭')
        .replace('8', '৮')
        .replace('9', '৯')
}