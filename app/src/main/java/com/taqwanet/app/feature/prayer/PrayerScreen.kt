package com.taqwanet.app.feature.prayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PrayerScreen(
    viewModel: PrayerViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // ১. তারিখ নির্বাচন হেডার
            item {
                DateSelectionHeader(
                    state = state,
                    onPrevDay = { viewModel.onDateSelected(state.selectedDate.minusDays(1)) },
                    onNextDay = { viewModel.onDateSelected(state.selectedDate.plusDays(1)) }
                )
            }

            // ২. সাহরি ও ইফতার হাইলাইট কার্ড (১২ ঘণ্টা ফরম্যাটে)
            item {
                state.additionalTimes?.let { times ->
                    val iftarH12 = if (times.iftar.hour % 12 == 0) 12 else times.iftar.hour % 12
                    val iftarM = String.format("%02d", times.iftar.minute)
                    val iftarStr = "$iftarH12:$iftarM PM".toBanglaDigits()

                    val sahurH12 = if (times.sahurEnd.hour % 12 == 0) 12 else times.sahurEnd.hour % 12
                    val sahurM = String.format("%02d", times.sahurEnd.minute)
                    val sahurStr = "$sahurH12:$sahurM AM".toBanglaDigits()

                    SahurIftarCard(sahur = sahurStr, iftar = iftarStr)
                }
            }

            // ৩. চলতি মাসের সালাতের অগ্রগতি ও মাসিক পরিসংখ্যান
            item {
                MonthlyTrackerCard(stat = state.monthlyStat)
            }

            // ৪. নফল ও অন্যান্য বিশেষ ওয়াক্ত
            item {
                state.additionalTimes?.let { times ->
                    NaflPrayersCard(times = times)
                }
            }

            // ৫. সালাতের গুরুত্ব ও ফজিলত কার্ড
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "জামাতে সালাতের গুরুত্ব",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "রাসূলুল্লাহ (সা.) বলেছেন: “জামাতের সাথে সালাত একাকী সালাতের চেয়ে সাতাশ গুণ বেশি মর্যাদাপূর্ণ।” — (সহীহ বুখারী: ৬৪৫)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelectionHeader(
    state: PrayerScreenUiState,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale.ENGLISH)
    val formattedDate = state.selectedDate.format(formatter)
        .toBanglaDigits()
        .replace("January", "জানুয়ারি")
        .replace("February", "ফেব্রুয়ারি")
        .replace("March", "মার্চ")
        .replace("April", "এপ্রিল")
        .replace("May", "মে")
        .replace("June", "জুন")
        .replace("July", "জুলাই")
        .replace("August", "আগস্ট")
        .replace("September", "সেপ্টেম্বর")
        .replace("October", "অক্টোবর")
        .replace("November", "নভেম্বর")
        .replace("December", "ডিসেম্বর")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        color = EmeraldSurfaceDark,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevDay) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "পূর্ববর্তী দিন", tint = MintBright)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (state.isToday) "আজকের দিন" else "নির্বাচিত দিন",
                    style = MaterialTheme.typography.labelSmall,
                    color = AntiqueGold
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )
            }

            IconButton(onClick = onNextDay) {
                Icon(Icons.Default.ChevronRight, contentDescription = "পরবর্তী দিন", tint = MintBright)
            }
        }
    }
}

@Composable
fun SahurIftarCard(sahur: String, iftar: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
            border = BorderStroke(1.dp, MintBright.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.NightlightRound, contentDescription = null, tint = MintBright, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("সাহরি শেষ", style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
                    Text(sahur, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                }
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AntiqueGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("ইফতার শুরু", style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
                    Text(iftar, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AntiqueGold)
                }
            }
        }
    }
}

@Composable
fun MonthlyTrackerCard(stat: MonthlyPrayerStat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("চলতি মাসের সালাত আদায়", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                    Text(
                        "${stat.completedCount.toString().toBanglaDigits()} / ${stat.totalFardhPrayers.toString().toBanglaDigits()} ওয়াক্ত সম্পন্ন হয়েছে",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                }

                Text(
                    "${stat.completionPercentage.toString().toBanglaDigits()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MintBright
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { stat.completionPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MintBright,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun NaflPrayersCard(times: AdditionalIslamicTimes) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldCardDark.copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "নফল ও অন্যান্য ওয়াক্ত",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AntiqueGold
            )
            Spacer(modifier = Modifier.height(10.dp))

            NaflTimeRow("তাহাজ্জুদ (শেষ তৃতীয়াংশ)", "শেষ সময়: ${times.tahajjudEnd.toString().toBanglaDigits()}")
            NaflTimeRow("ইশরাক সালাত", "শুরু: ${times.ishraq.toString().toBanglaDigits()}")
            NaflTimeRow("চাশত (সালাতুদ দুহা)", "শুরু: ${times.chasht.toString().toBanglaDigits()}")
        }
    }
}

@Composable
fun NaflTimeRow(title: String, timeText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = DarkTextPrimary)
        Text(timeText, style = MaterialTheme.typography.bodyMedium, color = MintSoft)
    }
}