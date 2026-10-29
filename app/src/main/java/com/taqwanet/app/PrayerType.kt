package com.taqwanet.app.domain.model

enum class PrayerType(
    val id: String,
    val banglaName: String,
    val arabicName: String,
    val isFardh: Boolean
) {
    FAJR("fajr", "ফজর", "الفجر", true),
    SUNRISE("sunrise", "সূর্যোদয়", "الشروق", false),
    DHUHR("dhuhr", "যোহর", "الظهر", true),
    ASR("asr", "আসর", "العصر", true),
    MAGHRIB("maghrib", "মাগরিব", "المغرب", true),
    ISHA("isha", "ইশা", "العشاء", true);

    companion object {
        fun fromId(id: String): PrayerType = entries.find { it.id.equals(id, ignoreCase = true) } ?: FAJR
        fun fardhPrayers(): List<PrayerType> = entries.filter { it.isFardh }
    }
}