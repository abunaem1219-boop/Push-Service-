package com.taqwanet.app.data.local.entity

data class QuranBookmarkEntity(
    val id: Long = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameBn: String,
    val surahNameAr: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)