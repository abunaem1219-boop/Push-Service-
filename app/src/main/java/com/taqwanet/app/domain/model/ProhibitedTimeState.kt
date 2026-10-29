package com.taqwanet.app.domain.model

import java.time.LocalTime

enum class ProhibitedPeriod(val titleBn: String, val descriptionBn: String) {
    SUNRISE("সূর্যোদয়ের সময়", "সূর্যোদয় শুরু হওয়া থেকে সূর্য পুরোপুরি উপরে ওঠা পর্যন্ত (প্রায় ১৫ মিনিট) নামাজ পড়া নিষিদ্ধ।"),
    ZAWAL("ঠিক দ্বিপ্রহরের সময়", "সূর্য ঠিক মাথার ওপর অবস্থানকালে যোহরের ওয়াক্ত শুরু হওয়ার আগ পর্যন্ত নামাজ পড়া নিষিদ্ধ।"),
    SUNSET("সূর্যাস্তের সময়", "সূর্য হলুদ বর্ণ ধারণ করা থেকে সূর্যাস্ত সম্পন্ন হওয়া পর্যন্ত আসরের কাজা ব্যতীত অন্যান্য নামাজ পড়া নিষিদ্ধ।"),
    NONE("", "")
}

data class ProhibitedTimeState(
    val isActive: Boolean,
    val period: ProhibitedPeriod,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val remainingMinutes: Long = 0
)