package com.taqwanet.app.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.feature.home.components.*

@Composable
fun HomeScreen(
    onNavigateQuran: () -> Unit = {},
    onNavigateQibla: () -> Unit = {},
    onNavigateMore: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
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
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ১. লাইভ বায়ুমণ্ডলীয় আকাশ
            item {
                SkyScene(currentTime = state.currentTime)
            }

            // ২. লাইভ চলমান ওয়াক্ত ও কাউন্টডাউন হিরো ব্যানার
            item {
                CurrentPeriodHeroBanner(
                    state = state.currentPeriod,
                    modifier = Modifier.offset(y = (-14).dp)
                )
            }

            // ৩. জেলা নির্বাচন ও হিজরি তারিখ
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = EmeraldCardDark.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, MintBright.copy(alpha = 0.35f)),
                        modifier = Modifier.clickable { viewModel.showCityPicker() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MintBright,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = state.cityNameBn,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "জেলা পরিবর্তন",
                                tint = MintBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = state.hijriDate.formattedBn,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = AntiqueGold
                    )
                }
            }

            // ৪. ৫টি নামাজের সময়সূচি ও ট্র্যাকার ব্যানার কার্ড
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "নামাজের সময়সূচি ও ট্র্যাকার",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldCardDark
                        ) {
                            Text(
                                text = "৫ ওয়াক্ত সালাত",
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    PrayerCardsRow(
                        prayerList = state.prayerList,
                        currentTime = state.currentTime,
                        onTogglePrayer = { prayer -> viewModel.onTogglePrayer(prayer) }
                    )
                }
            }

            // ৫. কুইক অ্যাকশন শর্টকাট (কুরআন, কিবলা, তসবিহ, দোয়া)
            item {
                QuickShortcutsRow(
                    onNavigateQuran = onNavigateQuran,
                    onNavigateQibla = onNavigateQibla,
                    onNavigateMore = onNavigateMore
                )
            }

            // ৬. আজকের কুরআনিক স্মরণিকা
            item {
                DailyInspirationCard()
            }
        }

        // ৬৪ জেলার বটম শীট পপআপ
        if (state.isCityPickerVisible) {
            CitySelectionBottomSheet(
                currentCityName = state.cityNameBn,
                onCitySelected = { selectedCity -> viewModel.onSelectCity(selectedCity) },
                onDismiss = { viewModel.hideCityPicker() }
            )
        }

        // ফুল ছিটানো সেলিব্রেশন অ্যানিমেশন ওভারলে
        state.celebrationPrayerName?.let { prayerName ->
            CelebrationEffect(
                visible = true,
                prayerNameBn = prayerName,
                modifier = Modifier.fillMaxSize()
            )
        }

        // ফায়ারবেস ভার্সন আপডেট পপআপ (যদি আপডেট থাকে)
        state.updateInfo?.let { updateInfo ->
            VersionUpdateDialog(
                updateInfo = updateInfo,
                onDismiss = { viewModel.dismissUpdateDialog() }
            )
        }
    }
}