package com.taqwanet.app.data.local.entity

data class PrayerRecordEntity(
    val id: Long = 0,
    val dateString: String,
    val prayerId: String,
    val isCompleted: Boolean,
    val completedAt: Long? = null,
    val isDelayed: Boolean = false
)