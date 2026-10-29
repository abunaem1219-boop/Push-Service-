package com.taqwanet.app.domain.model

data class AppUpdateInfo(
    val isUpdateAvailable: Boolean = false,
    val isForceUpdate: Boolean = false,
    val latestVersionName: String = "1.0.0",
    val latestVersionCode: Int = 1,
    val titleBn: String = "নতুন সংস্করণ এসেছে!",
    val releaseNotesBn: List<String> = emptyList(),
    val downloadUrl: String = "https://play.google.com/store"
)