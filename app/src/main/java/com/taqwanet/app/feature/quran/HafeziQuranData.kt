package com.taqwanet.app.feature.quran

data class JuzInfo(val number: Int, val nameAr: String, val nameBn: String, val startPage: Int)
data class SurahPageInfo(val number: Int, val nameBn: String, val nameAr: String, val startPage: Int, val totalAyahs: Int)

object HafeziQuranData {
    const val TOTAL_PAGES = 611 // ১৫ লাইনের স্ট্যান্ডার্ড হাফেজী কুরআনের মোট পৃষ্ঠা

    // ৩০ পারার প্রমিত পৃষ্ঠা তালিকা
    val JUZ_LIST = listOf(
        JuzInfo(1, "الم", "আলিফ লাম মীম", 2),
        JuzInfo(2, "سَيَقُولُ", "সায়াকুলু", 22),
        JuzInfo(3, "تِلْكَ الرُّسُلُ", "তিলকার রুসুলু", 42),
        JuzInfo(4, "لَنْ تَنَالُوا", "লান তানালু", 62),
        JuzInfo(5, "وَالْمُحْصَنَاتُ", "ওয়াল মুহসানাতু", 82),
        JuzInfo(6, "لَا يُحِبُّ اللَّهُ", "লা ইউহিব্বুল্লাহু", 102),
        JuzInfo(7, "وَإِذَا سَمِعُوا", "ওয়া ইযা সামিউ", 122),
        JuzInfo(8, "وَلَوْ أَنَّنَا", "ওয়া লাউ আন্নানা", 142),
        JuzInfo(9, "قَالَ الْمَلَأُ", "ক্বলাল মালাউ", 162),
        JuzInfo(10, "وَاعْلَمُوا", "ওয়ালামু", 182),
        JuzInfo(11, "يَعْتَذِرُونَ", "ইয়াতাযিরুন", 202),
        JuzInfo(12, "وَمَا مِنْ دَابَّةٍ", "ওয়ামা মিন দাব্বাহ", 222),
        JuzInfo(13, "وَمَا أُبَرِّئُ", "ওয়ামা উবাররিউ", 242),
        JuzInfo(14, "رُبَمَا", "রুবা মা", 262),
        JuzInfo(15, "سُبْحَانَ الَّذِي", "সুবহানাল্লাজি", 282),
        JuzInfo(16, "قَالَ أَلَمْ", "ক্বলা আলাম", 302),
        JuzInfo(17, "اقْتَرَبَ", "ইক্বতারাবা", 322),
        JuzInfo(18, "قَدْ أَفْلَحَ", "ক্বদ আফলাহা", 342),
        JuzInfo(19, "وَقَالَ الَّذِينَ", "ওয়া ক্বলাল্লাযিনা", 362),
        JuzInfo(20, "أَمَّنْ خَلَقَ", "আম্মান খালাক্ব", 382),
        JuzInfo(21, "اتْلُ مَا أُوحِيَ", "উতলু মা উহিয়া", 402),
        JuzInfo(22, "وَمَنْ يَقْنُتْ", "ওয়ামান ইয়াক্বনুত", 422),
        JuzInfo(23, "وَمَا لِيَ", "ওয়ামা লিইয়া", 442),
        JuzInfo(24, "فَمَنْ أَظْلَمُ", "ফামান আজলামু", 462),
        JuzInfo(25, "إِلَيْهِ يُرَدُّ", "ইলাইহি ইউরাদ্দু", 482),
        JuzInfo(26, "حم", "হা-মীম", 502),
        JuzInfo(27, "قَالَ فَمَا خَطْبُكُمْ", "ক্বলা ফামা খাতবুকুম", 522),
        JuzInfo(28, "قَدْ سَمِعَ اللَّهُ", "ক্বদ সামিয়াল্লাহু", 542),
        JuzInfo(29, "تَبَارَكَ الَّذِي", "তাবারাকাল্লাজি", 562),
        JuzInfo(30, "عَمَّ يَتَسَاءَلُونَ", "আম্মা ইয়াতাসাআলুন", 582)
    )

    // প্রধান প্রধান সূরার সূচনাকারী পৃষ্ঠা
    val SURAH_INDEX = listOf(
        SurahPageInfo(1, "আল-ফাতিহা", "الفاتحة", 2, 7),
        SurahPageInfo(2, "আল-বাকারা", "البقرة", 3, 286),
        SurahPageInfo(3, "আলে ইমরান", "آل عمران", 50, 200),
        SurahPageInfo(4, "আন-নিসা", "النساء", 77, 176),
        SurahPageInfo(5, "আল-মায়িদাহ", "المائدة", 106, 120),
        SurahPageInfo(6, "আল-আনআম", "الأنعام", 128, 165),
        SurahPageInfo(7, "আল-আরাফ", "الأعراف", 151, 206),
        SurahPageInfo(8, "আল-আনফাল", "الأنفال", 177, 75),
        SurahPageInfo(9, "আত-তাওবাহ", "التوبة", 187, 129),
        SurahPageInfo(10, "ইউনুস", "يونس", 208, 109),
        SurahPageInfo(12, "ইউসুফ", "يوسف", 235, 111),
        SurahPageInfo(18, "আল-কাহফ", "الكهف", 293, 110),
        SurahPageInfo(19, "মারইয়াম", "مريم", 305, 98),
        SurahPageInfo(20, "ত্বা-হা", "طه", 312, 135),
        SurahPageInfo(36, "ইয়াসীন", "يس", 440, 83),
        SurahPageInfo(55, "আর-রাহমান", "الرحمن", 531, 78),
        SurahPageInfo(56, "আল-ওয়াকিয়াহ", "الواقعة", 534, 96),
        SurahPageInfo(67, "আল-মুলক", "الملك", 562, 30),
        SurahPageInfo(78, "আন-নাবা", "النبأ", 582, 40),
        SurahPageInfo(112, "আল-ইখলাস", "الإخلاص", 604, 4),
        SurahPageInfo(113, "আল-ফালাক", "الفلق", 604, 5),
        SurahPageInfo(114, "আন-নাস", "الناس", 604, 6)
    )

    fun getJuzForPage(page: Int): JuzInfo {
        return JUZ_LIST.lastOrNull { page >= it.startPage } ?: JUZ_LIST.first()
    }

    fun getSurahForPage(page: Int): SurahPageInfo {
        return SURAH_INDEX.lastOrNull { page >= it.startPage } ?: SURAH_INDEX.first()
    }
}