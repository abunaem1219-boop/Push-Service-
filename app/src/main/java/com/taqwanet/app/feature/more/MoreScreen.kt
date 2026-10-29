package com.taqwanet.app.feature.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.feature.settings.SettingsScreen

enum class MoreSubScreen {
    MAIN,
    DUAS,
    TASBIH,
    MOSQUES,
    FRIDAY,
    SETTINGS
}

@Composable
fun MoreScreen(modifier: Modifier = Modifier) {
    var currentSubScreen by remember { mutableStateOf(MoreSubScreen.MAIN) }

    when (currentSubScreen) {
        MoreSubScreen.SETTINGS -> SettingsScreen(onBack = { currentSubScreen = MoreSubScreen.MAIN })
        MoreSubScreen.DUAS -> DuasScreen(onBack = { currentSubScreen = MoreSubScreen.MAIN })
        MoreSubScreen.TASBIH -> TasbihScreen(onBack = { currentSubScreen = MoreSubScreen.MAIN })
        MoreSubScreen.MOSQUES -> MosquesScreen(onBack = { currentSubScreen = MoreSubScreen.MAIN })
        MoreSubScreen.FRIDAY -> FridayReminderScreen(onBack = { currentSubScreen = MoreSubScreen.MAIN })
        MoreSubScreen.MAIN -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(EmeraldDeepDark),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "আরও ফিচার ও সেটিংস",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "অ্যাপের যাবতীয় কনফিগারেশন ও দ্বীনি সুবিধাসমূহ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // সেটিংস কার্ড
                item {
                    MoreFeatureCard(
                        icon = Icons.Default.Settings,
                        title = "সেটিংস ও সাউন্ড কন্ট্রোল",
                        subtitle = "আজান, নোটিফিকেশন রিংটোন ও ভয়েস নির্বাচন",
                        iconTint = MintBright,
                        onClick = { currentSubScreen = MoreSubScreen.SETTINGS }
                    )
                }

                item {
                    MoreFeatureCard(
                        icon = Icons.Default.MenuBook,
                        title = "মাসনুন দোয়া ও জিকির",
                        subtitle = "দৈনন্দিন জীবনের সহীহ দোয়া ও মোনাজাত",
                        iconTint = AntiqueGold,
                        onClick = { currentSubScreen = MoreSubScreen.DUAS }
                    )
                }

                item {
                    MoreFeatureCard(
                        icon = Icons.Default.TouchApp,
                        title = "ডিজিটাল তসবিহ",
                        subtitle = "ভাইব্রেশন ও রিয়েল-টাইম কাউন্টার সহ জিকির",
                        iconTint = MintSoft,
                        onClick = { currentSubScreen = MoreSubScreen.TASBIH }
                    )
                }

                item {
                    MoreFeatureCard(
                        icon = Icons.Default.Mosque,
                        title = "নিকটস্থ মসজিদ ও হালাল স্থান",
                        subtitle = "গুগল ম্যাপসের মাধ্যমে সঠিক দিকনির্দেশনা",
                        iconTint = SoftGold,
                        onClick = { currentSubScreen = MoreSubScreen.MOSQUES }
                    )
                }

                item {
                    MoreFeatureCard(
                        icon = Icons.Default.Event,
                        title = "জুমার প্রস্তুতি ও সুন্নাত",
                        subtitle = "জুমার দিনের আমল ও বিশেষ করণীয়সমূহ",
                        iconTint = AntiqueGold,
                        onClick = { currentSubScreen = MoreSubScreen.FRIDAY }
                    )
                }
            }
        }
    }
}

@Composable
fun MoreFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EmeraldCardDark,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextSecondary
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = DarkTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}