package com.taqwanet.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.ProhibitedRed
import com.taqwanet.app.core.theme.ProhibitedRedContainer
import com.taqwanet.app.core.theme.ProhibitedRedText
import com.taqwanet.app.domain.model.ProhibitedTimeState

@Composable
fun ProhibitedTimeBanner(state: ProhibitedTimeState, modifier: Modifier = Modifier) {
    if (!state.isActive) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ProhibitedRedContainer)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "সতর্কতা",
                tint = ProhibitedRed,
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${state.period.titleBn} (নামাজ পড়া নিষিদ্ধ)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProhibitedRed
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "আর মাত্র ${state.remainingMinutes.toString().toBanglaDigits()} মিনিট পর ওয়াক্ত শেষ হবে। ${state.period.descriptionBn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProhibitedRedText
                )
            }
        }
    }
}