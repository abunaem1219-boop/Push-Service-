package com.taqwanet.app.feature.more

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
fun MorePlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("দোয়া, জিকির ও সেটিংস", style = MaterialTheme.typography.headlineMedium, color = MintBright)
            Spacer(modifier = Modifier.height(8.dp))
            Text("সকল ইসলামিক ফিচার ও অডিও অপশন", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}