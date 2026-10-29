package com.taqwanet.app.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.audio.AudioPlaybackManager
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.domain.model.PrayerType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { AppPreferencesDataSource(context) }
    val audioManager = remember { AudioPlaybackManager.getInstance(context) }

    // সেটিংস স্টেটসমূহ
    var selectedSound by remember { mutableStateOf("notification_default") }
    var selectedAdhan by remember { mutableStateOf("SHORT") }
    var isVoiceEnabled by remember { mutableStateOf(true) }
    var isCelebrationEnabled by remember { mutableStateOf(true) }
    var reminderLeadMinutes by remember { mutableIntStateOf(10) }
    var playingPreview by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        selectedSound = prefs.notificationSound.first()
        selectedAdhan = prefs.adhanType.first()
        isVoiceEnabled = prefs.completionVoiceEnabled.first()
        isCelebrationEnabled = prefs.celebrationAnimationEnabled.first()
        reminderLeadMinutes = prefs.reminderLeadMinutes.first()
    }

    DisposableEffect(Unit) {
        onDispose { audioManager.stopPlayback() }
    }

    val notificationSounds = listOf(
        Pair("notification_default", "ডিফল্ট নোটিফিকেশন টোন"),
        Pair("notification_chime", "মিষ্টি ঘণ্টার সুর (Chime)"),
        Pair("notification_gentle", "ধীরস্থির আধ্যাত্মিক সুর (Gentle)"),
        Pair("notification_soft", "নরম রিংটোন (Soft)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
            .padding(horizontal = 20.dp)
    ) {
        // টপ বার
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "পিছনে", tint = MintBright)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "সেটিংস ও সাউন্ড কন্ট্রোল",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ১. আজান সেটিংস
            item {
                SettingsSectionCard(title = "ওয়াক্ত শুরুর আজান") {
                    listOf(
                        Triple("NONE", "আজান বন্ধ (শুধু সাউন্ড)", null),
                        Triple("SHORT", "সংক্ষিপ্ত আজান", "adhan_short"),
                        Triple("FULL", "পূর্ণাঙ্গ আজান", "adhan_full")
                    ).forEach { (type, label, rawFile) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAdhan = type
                                    coroutineScope.launch { prefs.setAdhanType(type) }
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedAdhan == type,
                                    onClick = {
                                        selectedAdhan = type
                                        coroutineScope.launch { prefs.setAdhanType(type) }
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = MintBright)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, color = DarkTextPrimary, fontSize = 14.sp)
                            }

                            // অডিও প্রিভিউ বাটন
                            if (rawFile != null) {
                                IconButton(
                                    onClick = {
                                        if (playingPreview == rawFile) {
                                            audioManager.stopPlayback()
                                            playingPreview = null
                                        } else {
                                            playingPreview = rawFile
                                            audioManager.playAdhan(isFull = (rawFile == "adhan_full")) {
                                                playingPreview = null
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (playingPreview == rawFile) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                                        contentDescription = "প্রিভিউ",
                                        tint = if (playingPreview == rawFile) AntiqueGold else MintBright
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ২. নোটিফিকেশন রিংটোন সিলেকশন
            item {
                SettingsSectionCard(title = "নোটিফিকেশন রিংটোন নির্বাচন") {
                    notificationSounds.forEach { (soundKey, soundTitle) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSound = soundKey
                                    coroutineScope.launch { prefs.setNotificationSound(soundKey) }
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedSound == soundKey,
                                    onClick = {
                                        selectedSound = soundKey
                                        coroutineScope.launch { prefs.setNotificationSound(soundKey) }
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = MintBright)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(soundTitle, color = DarkTextPrimary, fontSize = 14.sp)
                            }

                            IconButton(
                                onClick = {
                                    if (playingPreview == soundKey) {
                                        audioManager.stopPlayback()
                                        playingPreview = null
                                    } else {
                                        playingPreview = soundKey
                                        audioManager.previewNotificationTone(soundKey) {
                                            playingPreview = null
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (playingPreview == soundKey) Icons.Default.StopCircle else Icons.Default.VolumeUp,
                                    contentDescription = "প্রিভিউ",
                                    tint = if (playingPreview == soundKey) AntiqueGold else MintBright
                                )
                            }
                        }
                    }
                }
            }

            // ৩. সালাত আদায়ের বাংলা ভয়েস ও সেলিব্রেশন
            item {
                SettingsSectionCard(title = "ইন্টারঅ্যাকশন ও অ্যানিমেশন") {
                    // বাংলা কণ্ঠবার্তা
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ওয়াক্ত সমাপ্তির বাংলা ভয়েস", fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                            Text("নামাজে টিক দিলে ওয়াক্তভিত্তিক বাংলা কণ্ঠবার্তা প্লে হবে", fontSize = 12.sp, color = DarkTextSecondary)
                        }
                        Switch(
                            checked = isVoiceEnabled,
                            onCheckedChange = {
                                isVoiceEnabled = it
                                coroutineScope.launch { prefs.setCompletionVoiceEnabled(it) }
                                if (it) audioManager.playPrayerCompletionVoice(PrayerType.DHUHR)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = MintBright)
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 6.dp))

                    // ফুল ছিটানো সেলিব্রেশন
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ফুলের পাপড়ির সেলিব্রেশন", fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                            Text("নামাজ সম্পন্ন করার পর স্ক্রিনে ফুলের পাপড়ি ঝরে পড়ার দৃশ্য", fontSize = 12.sp, color = DarkTextSecondary)
                        }
                        Switch(
                            checked = isCelebrationEnabled,
                            onCheckedChange = {
                                isCelebrationEnabled = it
                                coroutineScope.launch { prefs.setCelebrationEnabled(it) }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = MintBright)
                        )
                    }
                }
            }

            // ৪. ওয়াক্ত সমাপ্তি নোটিফিকেশন মিনিট
            item {
                SettingsSectionCard(title = "ওয়াক্ত সমাপ্তি সতর্কতা মিনিট") {
                    Text("ওয়াক্ত শেষ হওয়ার কত মিনিট পূর্বে নোটিফিকেশন আসবে:", fontSize = 13.sp, color = DarkTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(5, 10, 15, 20).forEach { mins ->
                            val isSel = reminderLeadMinutes == mins
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        reminderLeadMinutes = mins
                                        coroutineScope.launch { prefs.setReminderLeadMinutes(mins) }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) MintContainer else EmeraldDeepDark,
                                border = BorderStroke(1.dp, if (isSel) MintBright else Color.White.copy(alpha = 0.1f))
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins.toString().toBanglaDigits()} মিনিট",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSel) MintSoft else DarkTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AntiqueGold
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}