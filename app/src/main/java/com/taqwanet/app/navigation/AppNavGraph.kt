package com.taqwanet.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taqwanet.app.core.theme.EmeraldDeepDark
import com.taqwanet.app.feature.home.HomeScreen
import com.taqwanet.app.feature.more.MoreScreen
import com.taqwanet.app.feature.prayer.PrayerScreen
import com.taqwanet.app.feature.qibla.QiblaScreen
import com.taqwanet.app.feature.quran.QuranScreen
import com.taqwanet.app.feature.reels.ReelsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = EmeraldDeepDark,
            bottomBar = {
                BottomNavBar(navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateQuran = { navController.navigate("quran_page") },
                        onNavigateQibla = { navController.navigate(Screen.Qibla.route) },
                        onNavigateMore = { navController.navigate(Screen.More.route) }
                    )
                }
                composable(Screen.Prayer.route) {
                    PrayerScreen()
                }
                // ৩ নম্বর ট্যাবে রিলস
                composable(Screen.Reels.route) {
                    ReelsScreen()
                }
                composable(Screen.Qibla.route) {
                    QiblaScreen()
                }
                composable(Screen.More.route) {
                    MoreScreen()
                }
                // হোমপেজ থেকে কুরআন পড়ার জন্য আলাদা সাব-রুট
                composable("quran_page") {
                    QuranScreen()
                }
            }
        }
    }
}