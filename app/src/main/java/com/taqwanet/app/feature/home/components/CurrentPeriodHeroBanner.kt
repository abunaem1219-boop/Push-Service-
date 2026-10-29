package com.taqwanet.app.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.domain.model.CurrentPeriodState

@Composable
fun CurrentPeriodHeroBanner(
    state: CurrentPeriodState,
    modifier: Modifier = Modifier
) {
    val isRed = state.isProhibited

    val borderStroke = if (isRed) {
        BorderStroke(2.dp, Brush.horizontalGradient(listOf(Color(0xFFFF3333), Color(0xFFFF5252))))
    } else {
        BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AntiqueGold, MintBright.copy(alpha = 0.6f))))
    }

    val cardBg = if (isRed) {
        Brush.verticalGradient(listOf(Color(0xFF380808), Color(0xFF1E0404)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF072B21), Color(0xFF031A14)))
    }

    val spotColor = if (isRed) Color(0xFFFF1744).copy(alpha = 0.6f) else AntiqueGold.copy(alpha = 0.35f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = spotColor
            ),
        shape = RoundedCornerShape(22.dp),
        border = borderStroke
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isRed) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = state.titleBn,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isRed) Color(0xFFFF5252) else MintBright
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = if (isRed) Color(0xFFFF8A80) else AntiqueGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "বাকি: ${state.remainingTimeText}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isRed) Color(0xFFFFCDD2) else DarkTextPrimary
                        )
                    }
                }

                // ডানদিকের আরবি ক্যালিগ্রাফি ব্যাজ
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRed) Color(0xFF5C1010) else EmeraldDeepDark.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, if (isRed) Color(0xFFFF5252).copy(alpha = 0.5f) else AntiqueGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = state.titleAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isRed) Color(0xFFFF8A80) else AntiqueGold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}