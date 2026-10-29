package com.taqwanet.app.core.calculation

import kotlin.math.*

object QiblaMath {
    // পবিত্র কাবা শরীফের ভৌগোলিক স্থানাঙ্ক
    const val KAABA_LATITUDE = 21.4225
    const val KAABA_LONGITUDE = 39.8262

    // কিবলা কোণ (ডিগ্রি হিসেবে ০° থেকে ৩৬০°)
    fun calculateQiblaBearing(userLat: Double, userLon: Double): Double {
        val lat1 = Math.toRadians(userLat)
        val lon1 = Math.toRadians(userLon)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val lon2 = Math.toRadians(KAABA_LONGITUDE)

        val deltaLon = lon2 - lon1
        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing
    }

    // কাবার দূরত্ব (কিলোমিটারে - হাভারসাইন ফর্মুলা)
    fun calculateDistanceToKaabaKm(userLat: Double, userLon: Double): Int {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(KAABA_LATITUDE - userLat)
        val dLon = Math.toRadians(KAABA_LONGITUDE - userLon)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(KAABA_LATITUDE)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadiusKm * c).roundToInt()
    }
}