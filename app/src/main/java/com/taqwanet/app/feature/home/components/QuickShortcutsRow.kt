package com.taqwanet.app.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.theme.*

@Composable
fun QuickShortcutsRow(
    onNavigateQuran: () -> Unit,
    onNavigateQibla: () -> Unit,
    onNavigateMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ShortcutItem(
            icon = Icons.Default.MenuBook,
            title = "কুরআন",
            tint = MintBright,
            modifier = Modifier.weight(1f),
            onClick = onNavigateQuran
        )
        ShortcutItem(
            icon = Icons.Default.Explore,
            title = "কিবলা",
            tint = AntiqueGold,
            modifier = Modifier.weight(1f),
            onClick = onNavigateQibla
        )
        ShortcutItem(
            icon = Icons.Default.TouchApp,
            title = "তসবিহ",
            tint = MintSoft,
            modifier = Modifier.weight(1f),
            onClick = onNavigateMore
        )
        ShortcutItem(
            icon = Icons.Default.Mosque,
            title = "দোয়া",
            tint = SoftGold,
            modifier = Modifier.weight(1f),
            onClick = onNavigateMore
        )
    }
}

@Composable
private fun ShortcutItem(
    icon: ImageVector,
    title: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
        }
    }
}