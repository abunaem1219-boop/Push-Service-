package com.taqwanet.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val titleBn: String, val icon: ImageVector) {
    object Home : Screen("home", "আজ", Icons.Default.Home)
    object Prayer : Screen("prayer", "নামাজ", Icons.Default.DateRange)
    
    // ৩ নম্বর ট্যাবে কুরআনের বদলে রিলস
    object Reels : Screen("reels", "রিলস", Icons.Default.PlayCircle)
    
    object Qibla : Screen("qibla", "কিবলা", Icons.Default.Explore)
    object More : Screen("more", "আরও", Icons.Default.Menu)
}