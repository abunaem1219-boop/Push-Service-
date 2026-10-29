package com.taqwanet.app.domain.model

data class CityLocation(
    val nameBn: String,
    val nameEn: String,
    val divisionBn: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double = 6.0
) {
    companion object {
        val DEFAULT_DHAKA = CityLocation("ঢাকা", "Dhaka", "ঢাকা বিভাগ", 23.8103, 90.4125)

        val ALL_BANGLADESH_DISTRICTS = listOf(
            // ঢাকা বিভাগ (১৩টি)
            DEFAULT_DHAKA,
            CityLocation("গাজীপুর", "Gazipur", "ঢাকা বিভাগ", 23.9999, 90.4203),
            CityLocation("নারায়ণগঞ্জ", "Narayanganj", "ঢাকা বিভাগ", 23.6238, 90.5000),
            CityLocation("টাঙ্গাইল", "Tangail", "ঢাকা বিভাগ", 24.2513, 89.9167),
            CityLocation("কিশোরগঞ্জ", "Kishoreganj", "ঢাকা বিভাগ", 24.4449, 90.7766),
            CityLocation("নরসিংদী", "Narsingdi", "ঢাকা বিভাগ", 23.9322, 90.7154),
            CityLocation("মুন্সীগঞ্জ", "Munshiganj", "ঢাকা বিভাগ", 23.5422, 90.5305),
            CityLocation("মানিকগঞ্জ", "Manikganj", "ঢাকা বিভাগ", 23.8644, 90.0047),
            CityLocation("ফরিদপুর", "Faridpur", "ঢাকা বিভাগ", 23.6071, 89.8429),
            CityLocation("গোপালগঞ্জ", "Gopalganj", "ঢাকা বিভাগ", 23.0051, 89.8266),
            CityLocation("মাদারীপুর", "Madaripur", "ঢাকা বিভাগ", 23.1641, 90.1897),
            CityLocation("রাজবাড়ী", "Rajbari", "ঢাকা বিভাগ", 23.7574, 89.6445),
            CityLocation("শরীয়তপুর", "Shariatpur", "ঢাকা বিভাগ", 23.2423, 90.3540),

            // চট্টগ্রাম বিভাগ (১১টি)
            CityLocation("চট্টগ্রাম", "Chattogram", "চট্টগ্রাম বিভাগ", 22.3569, 91.7832),
            CityLocation("কক্সবাজার", "Cox's Bazar", "চট্টগ্রাম বিভাগ", 21.4272, 92.0058),
            CityLocation("কুমিল্লা", "Cumilla", "চট্টগ্রাম বিভাগ", 23.4607, 91.1809),
            CityLocation("ফেনী", "Feni", "চট্টগ্রাম বিভাগ", 23.0186, 91.3966),
            CityLocation("ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "চট্টগ্রাম বিভাগ", 23.9571, 91.1119),
            CityLocation("নোয়াখালী", "Noakhali", "চট্টগ্রাম বিভাগ", 22.8696, 91.0998),
            CityLocation("চাঁদপুর", "Chandpur", "চট্টগ্রাম বিভাগ", 23.2333, 90.6667),
            CityLocation("লক্ষ্মীপুর", "Lakshmipur", "চট্টগ্রাম বিভাগ", 22.9425, 90.8412),
            CityLocation("রাঙ্গামাটি", "Rangamati", "চট্টগ্রাম বিভাগ", 22.7324, 92.2985),
            CityLocation("খাগড়াছড়ি", "Khagrachhari", "চট্টগ্রাম বিভাগ", 23.1193, 91.9847),
            CityLocation("বান্দরবান", "Bandarban", "চট্টগ্রাম বিভাগ", 22.1953, 92.2184),

            // সিলেট বিভাগ (৪টি)
            CityLocation("সিলেট", "Sylhet", "সিলেট বিভাগ", 24.8949, 91.8687),
            CityLocation("মৌলভীবাজার", "Moulvibazar", "সিলেট বিভাগ", 24.4829, 91.7774),
            CityLocation("হবিগঞ্জ", "Habiganj", "সিলেট বিভাগ", 24.3749, 91.4155),
            CityLocation("সুনামগঞ্জ", "Sunamganj", "সিলেট বিভাগ", 25.0658, 91.3950),

            // রাজশাহী বিভাগ (৮টি)
            CityLocation("রাজশাহী", "Rajshahi", "রাজশাহী বিভাগ", 24.3745, 88.6042),
            CityLocation("বগুড়া", "Bogura", "রাজশাহী বিভাগ", 24.8465, 89.3770),
            CityLocation("পাবনা", "Pabna", "রাজশাহী বিভাগ", 24.0064, 89.2372),
            CityLocation("সিরাজগঞ্জ", "Sirajganj", "রাজশাহী বিভাগ", 24.4534, 89.7008),
            CityLocation("নওগাঁ", "Naogaon", "রাজশাহী বিভাগ", 24.7936, 88.9318),
            CityLocation("নাটোর", "Natore", "রাজশাহী বিভাগ", 24.4206, 88.9324),
            CityLocation("জয়পুরহাট", "Joypurhat", "রাজশাহী বিভাগ", 25.0968, 89.0227),
            CityLocation("চাঁপাইনবাবগঞ্জ", "Chapainawabganj", "রাজশাহী বিভাগ", 24.5965, 88.2775),

            // খুলনা বিভাগ (১০টি)
            CityLocation("খুলনা", "Khulna", "খুলনা বিভাগ", 22.8456, 89.5403),
            CityLocation("যশোর", "Jashore", "খুলনা বিভাগ", 23.1664, 89.2082),
            CityLocation("সাতক্ষীরা", "Satkhira", "খুলনা বিভাগ", 22.7185, 89.0705),
            CityLocation("ঝিনাইদহ", "Jhenaidah", "খুলনা বিভাগ", 23.5448, 89.1539),
            CityLocation("মাগুরা", "Magura", "খুলনা বিভাগ", 23.4873, 89.4198),
            CityLocation("নড়াইল", "Narail", "খুলনা বিভাগ", 23.1725, 89.5127),
            CityLocation("বাগেরহাট", "Bagerhat", "খুলনা বিভাগ", 22.6516, 89.7859),
            CityLocation("কুষ্টিয়া", "Kushtia", "খুলনা বিভাগ", 23.9013, 89.1205),
            CityLocation("চুয়াডাঙ্গা", "Chuadanga", "খুলনা বিভাগ", 23.6402, 88.8418),
            CityLocation("মেহেরপুর", "Meherpur", "খুলনা বিভাগ", 23.7622, 88.6318),

            // বরিশাল বিভাগ (৬টি)
            CityLocation("বরিশাল", "Barishal", "বরিশাল বিভাগ", 22.7010, 90.3535),
            CityLocation("ভোলা", "Bhola", "বরিশাল বিভাগ", 22.6859, 90.6481),
            CityLocation("ঝালকাঠি", "Jhalokathi", "বরিশাল বিভাগ", 22.6406, 90.1987),
            CityLocation("পিরোজপুর", "Pirojpur", "বরিশাল বিভাগ", 22.5841, 89.9720),
            CityLocation("বরগুনা", "Barguna", "বরিশাল বিভাগ", 22.0953, 90.1121),
            CityLocation("পটুয়াখালী", "Patuakhali", "বরিশাল বিভাগ", 22.3596, 90.3299),

            // রংপুর বিভাগ (৮টি)
            CityLocation("রংপুর", "Rangpur", "রংপুর বিভাগ", 25.7439, 89.2752),
            CityLocation("দিনাজপুর", "Dinajpur", "রংপুর বিভাগ", 25.6217, 88.6355),
            CityLocation("গাইবান্ধা", "Gaibandha", "রংপুর বিভাগ", 25.3288, 89.5430),
            CityLocation("কুড়িগ্রাম", "Kurigram", "রংপুর বিভাগ", 25.8054, 89.6362),
            CityLocation("লালমনিরহাট", "Lalmonirhat", "রংপুর বিভাগ", 25.9923, 89.2847),
            CityLocation("নীলফামারী", "Nilphamari", "রংপুর বিভাগ", 25.9318, 88.8560),
            CityLocation("পঞ্চগড়", "Panchagarh", "রংপুর বিভাগ", 26.3411, 88.5542),
            CityLocation("ঠাকুরগাঁও", "Thakurgaon", "রংপুর বিভাগ", 26.0337, 88.4617),

            // ময়মনসিংহ বিভাগ (৪টি)
            CityLocation("ময়মনসিংহ", "Mymensingh", "ময়মনসিংহ বিভাগ", 24.7471, 90.4203),
            CityLocation("জামালপুর", "Jamalpur", "ময়মনসিংহ বিভাগ", 24.9375, 89.9378),
            CityLocation("নেত্রকোণা", "Netrokona", "ময়মনসিংহ বিভাগ", 24.8709, 90.7279),
            CityLocation("শেরপুর", "Sherpur", "ময়মনসিংহ বিভাগ", 25.0205, 90.0153),

            // পবিত্র দুই হারামাইন
            CityLocation("মক্কা মুকাররমা", "Makkah", "সৌদি আরব", 21.4225, 39.8262, 3.0),
            CityLocation("মদিনা মুনাওয়ারা", "Madinah", "সৌদি আরব", 24.5247, 39.5692, 3.0)
        )
    }
}