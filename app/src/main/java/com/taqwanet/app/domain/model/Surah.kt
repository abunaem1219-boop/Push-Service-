package com.taqwanet.app.domain.model

data class Surah(
    val number: Int,
    val nameAr: String,
    val nameBn: String,
    val meaningBn: String,
    val totalAyahs: Int,
    val revelationTypeBn: String // "মাক্কী" বা "মাদানী"
)

data class Ayah(
    val numberInSurah: Int,
    val textAr: String,
    val textBn: String,
    val audioUrl: String = ""
)

data class Reciter(
    val id: String,
    val nameBn: String,
    val cdnSubpath: String
) {
    companion object {
        val DEFAULT_RECITERS = listOf(
            Reciter("alafasy", "মিশারী রাশিদ আল-আফাসী", "Alafasy_128kbps"),
            Reciter("sudais", "আব্দুর রহমান আস-সুদাইস", "Abdurrahmaan_As-Sudais_192kbps"),
            Reciter("ghamadi", "সাদ আল-গামদী", "Ghamadi_40kbps")
        )
    }
}