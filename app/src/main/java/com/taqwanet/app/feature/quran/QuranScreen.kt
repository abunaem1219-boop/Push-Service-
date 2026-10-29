package com.taqwanet.app.feature.quran

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.feature.quran.components.*
import kotlinx.coroutines.launch

enum class QuranMode {
    HAFEZI,
    ONLINE
}

@Composable
fun QuranScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // লাস্ট রিড পেজ সংরক্ষণ ও পুনরুদ্ধার
    val sharedPrefs = remember { context.getSharedPreferences("taqwanet_quran", Context.MODE_PRIVATE) }
    val savedPage = remember { sharedPrefs.getInt("last_page", 2) }

    var currentMode by remember { mutableStateOf(QuranMode.HAFEZI) }
    var readingTheme by remember { mutableStateOf(ReadingTheme.ORIGINAL) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var isNavigationSheetOpen by remember { mutableStateOf(false) }

    val soundHelper = remember { PageFlipSoundHelper(context) }
    DisposableEffect(Unit) {
        onDispose { soundHelper.release() }
    }

    // হাফেজী কুরআন পেজার স্টেট (৬১১ পৃষ্ঠা)
    val pagerState = rememberPagerState(
        initialPage = (savedPage - 1).coerceIn(0, HafeziQuranData.TOTAL_PAGES - 1),
        pageCount = { HafeziQuranData.TOTAL_PAGES }
    )

    // পাতা উল্টালে সাউন্ড ও বুকমার্ক সেভ
    LaunchedEffect(pagerState.currentPage) {
        val currentPageNumber = pagerState.currentPage + 1
        sharedPrefs.edit().putInt("last_page", currentPageNumber).apply()
        soundHelper.playFlipSound(isSoundEnabled)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ১. শীর্ষ ফ্রস্টেড লিকুইড গ্লাস সেগমেন্টেড সুইচ বার
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldSurfaceDark,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // হাফেজী কুরআন ট্যাব
                        SegmentedPillItem(
                            title = "📖 হাফেজী কুরআন",
                            isSelected = currentMode == QuranMode.HAFEZI,
                            onClick = { currentMode = QuranMode.HAFEZI }
                        )

                        // অনলাইন কুরআন ট্যাব
                        SegmentedPillItem(
                            title = "🌐 অনলাইন ও অডিও",
                            isSelected = currentMode == QuranMode.ONLINE,
                            onClick = { currentMode = QuranMode.ONLINE }
                        )
                    }
                }
            }

            // ২. নির্বাচিত মোড অনুযায়ী মূল ভিউ
            when (currentMode) {
                QuranMode.HAFEZI -> {
                    // হাফেজী কুরআনের কুইক টুলবার
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // পারা ও সূরা জাম্প বোতাম
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = EmeraldCardDark,
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { isNavigationSheetOpen = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val currentPg = pagerState.currentPage + 1
                                val currentJuz = HafeziQuranData.getJuzForPage(currentPg)
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "পারা ${currentJuz.number.toString().toBanglaDigits()} • পৃষ্ঠা ${currentPg.toString().toBanglaDigits()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(18.dp))
                            }
                        }

                        // আই-কেয়ার থিম (হোয়াইট / সেপিয়া / ডার্ক) ও সাউন্ড টগল
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    readingTheme = when (readingTheme) {
                                        ReadingTheme.ORIGINAL -> ReadingTheme.SEPIA
                                        ReadingTheme.SEPIA -> ReadingTheme.NIGHT
                                        ReadingTheme.NIGHT -> ReadingTheme.ORIGINAL
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "থিম পরিবর্তন",
                                    tint = when (readingTheme) {
                                        ReadingTheme.ORIGINAL -> Color.White
                                        ReadingTheme.SEPIA -> Color(0xFFE9C46A)
                                        ReadingTheme.NIGHT -> MintBright
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(onClick = { isSoundEnabled = !isSoundEnabled }) {
                                Icon(
                                    imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = "সাউন্ড",
                                    tint = if (isSoundEnabled) MintBright else DarkTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // ৩D পেজ উল্টানোর মূল কুরআন ভিউয়ার
                    HafeziQuranPageViewer(
                        pagerState = pagerState,
                        theme = readingTheme,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 90.dp)
                    )
                }
                QuranMode.ONLINE -> {
                    // অনলাইন ও অডিও তিলাওয়াত ফুলস্ক্রিন ওয়েবভিউ
                    OnlineQuranWebView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 90.dp)
                    )
                }
            }
        }

        // পারা/সূরা/পৃষ্ঠা জাম্পার বটম শীট
        if (isNavigationSheetOpen) {
            QuranNavigationSheet(
                currentPage = pagerState.currentPage + 1,
                onJumpToPage = { targetPage ->
                    coroutineScope.launch {
                        pagerState.scrollToPage((targetPage - 1).coerceIn(0, HafeziQuranData.TOTAL_PAGES - 1))
                    }
                },
                onDismiss = { isNavigationSheetOpen = false }
            )
        }
    }
}

@Composable
private fun SegmentedPillItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgAnim by animateColorAsState(
        targetValue = if (isSelected) MintContainer else Color.Transparent,
        animationSpec = tween(250),
        label = "pillBg"
    )
    val textAnim by animateColorAsState(
        targetValue = if (isSelected) MintSoft else Color.White.copy(alpha = 0.6f),
        animationSpec = tween(250),
        label = "pillText"
    )

    Surface(
        shape = CircleShape,
        color = bgAnim,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textAnim,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}