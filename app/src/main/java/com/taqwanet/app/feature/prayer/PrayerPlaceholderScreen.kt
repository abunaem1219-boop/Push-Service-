package com.taqwanet.app.feature.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.theme.MintBright

@Composable
fun PrayerPlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("নামাজ ও অতীত হিস্ট্রি", style = MaterialTheme.typography.headlineMedium, color = MintBright)
            Spacer(modifier = Modifier.height(8.dp))
            Text("পরবর্তী পর্বে পূর্ণাঙ্গভাবে লোড হবে", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}