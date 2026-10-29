package com.taqwanet.app.feature.quran.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.feature.quran.HafeziQuranData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranNavigationSheet(
    currentPage: Int,
    onJumpToPage: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = পারা, 1 = সূরা, 2 = পৃষ্ঠা নম্বর
    var jumpPageInput by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EmeraldSurfaceDark,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .fillMaxHeight(0.82f)
        ) {
            // হেডার
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "কুরআন নেভিগেশন",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "বন্ধ", tint = DarkTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ৩টি টগল বাটন (পারা / সূরা / পৃষ্ঠা)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = EmeraldCardDark,
                contentColor = MintBright
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("৩০ পারা", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("১১৪ সূরা", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("পৃষ্ঠা জাম্প", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> { // ৩০ পারা তালিকা
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(HafeziQuranData.JUZ_LIST) { juz ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onJumpToPage(juz.startPage)
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "পারা ${juz.number.toString().toBanglaDigits()} • ${juz.nameBn}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkTextPrimary
                                        )
                                        Text(
                                            text = "পৃষ্ঠা: ${juz.startPage.toString().toBanglaDigits()}",
                                            fontSize = 12.sp,
                                            color = MintSoft
                                        )
                                    }
                                    Text(
                                        text = juz.nameAr,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueGold
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> { // সূরা তালিকা
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("সূরার নাম দিয়ে খুঁজুন...", color = DarkTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MintBright) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val filteredSurahs = HafeziQuranData.SURAH_INDEX.filter {
                        it.nameBn.contains(searchQuery, ignoreCase = true) || it.nameAr.contains(searchQuery)
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredSurahs) { surah ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onJumpToPage(surah.startPage)
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${surah.number.toString().toBanglaDigits()}. সূরা ${surah.nameBn}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkTextPrimary
                                        )
                                        Text(
                                            text = "পৃষ্ঠা: ${surah.startPage.toString().toBanglaDigits()} • আয়াত: ${surah.totalAyahs.toString().toBanglaDigits()}",
                                            fontSize = 12.sp,
                                            color = MintSoft
                                        )
                                    }
                                    Text(
                                        text = surah.nameAr,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueGold
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> { // সরাসরি পৃষ্ঠা নম্বর ইনপুট
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "১ থেকে ৬১১ এর মধ্যে পৃষ্ঠা নম্বর দিন",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = jumpPageInput,
                            onValueChange = { if (it.length <= 3 && it.all { ch -> ch.isDigit() }) jumpPageInput = it },
                            placeholder = { Text("পৃষ্ঠা নং (যেমন: ৪৫)", color = DarkTextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(0.7f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                val p = jumpPageInput.toIntOrNull()
                                if (p != null && p in 1..HafeziQuranData.TOTAL_PAGES) {
                                    onJumpToPage(p)
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MintBright),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text("পৃষ্ঠায় যান", fontWeight = FontWeight.Bold, color = EmeraldDeepDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = EmeraldDeepDark)
                        }
                    }
                }
            }
        }
    }
}