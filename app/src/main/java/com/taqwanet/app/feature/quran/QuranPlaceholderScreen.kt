package com.taqwanet.app.feature.quran

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
fun QuranPlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("আল-কুরআনুল কারীম", style = MaterialTheme.typography.headlineMedium, color = MintBright)
            Spacer(modifier = Modifier.height(8.dp))
            Text("১১৪ সূরার অডিও ও বাংলা অনুবাদ সহ লোড হচ্ছে", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}