package com.taqwanet.app.domain.model

data class Dua(
    val id: String,
    val categoryBn: String,
    val titleBn: String,
    val arabicText: String,
    val pronunciationBn: String,
    val meaningBn: String,
    val referenceBn: String
)

enum class DuaCategory(val titleBn: String) {
    MORNING_EVENING("সকাল-সন্ধ্যা"),
    SALAT_WUDU("সালাত ও ওযু"),
    DAILY_LIFE("দৈনন্দিন জীবন"),
    PROTECTION("বিপদ ও সুরক্ষা"),
    FORGIVENESS("ক্ষমা প্রার্থনা")
}