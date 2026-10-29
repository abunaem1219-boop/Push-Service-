package com.taqwanet.app.feature.more

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.data.repository.DuaRepository
import com.taqwanet.app.domain.model.Dua
import com.taqwanet.app.domain.model.DuaCategory

@Composable
fun DuasScreen(onBack: () -> Unit) {
    val repo = remember { DuaRepository() }
    val categories = DuaCategory.entries
    var selectedCategory by remember { mutableStateOf<DuaCategory?>(null) }

    val filteredDuas = remember(selectedCategory) {
        if (selectedCategory == null) repo.allDuas
        else repo.getDuasByCategory(selectedCategory!!.titleBn)
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = EmeraldDeepDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // টপ বার
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "পিছনে", tint = MintBright)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "মাসনুন দোয়া ও জিকির",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )
            }

            // ক্যাটাগরি চিপস অনুভূমিক স্ক্রোল
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("সকল দোয়া") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MintContainer,
                            selectedLabelColor = MintSoft
                        )
                    )
                }
                items(categories.size) { index ->
                    val cat = categories[index]
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.titleBn) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MintContainer,
                            selectedLabelColor = MintSoft
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // দোয়ার তালিকা
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredDuas.size) { index ->
                    val dua = filteredDuas[index]
                    DuaItemCard(
                        dua = dua,
                        onCopy = {
                            val textToCopy = "${dua.titleBn}\n\n${dua.arabicText}\n\nউচ্চারণ: ${dua.pronunciationBn}\n\nঅর্থ: ${dua.meaningBn}\n\nরেফারেন্স: ${dua.referenceBn}\n(তাকওয়ানেট অ্যাপ)"
                            clipboardManager.setText(AnnotatedString(textToCopy))
                        },
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${dua.titleBn}\n\n${dua.arabicText}\n\nঅর্থ: ${dua.meaningBn}\n\nসূত্র: ${dua.referenceBn}\n[TaqwaNet App]")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "দোয়া শেয়ার করুন"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DuaItemCard(dua: Dua, onCopy: () -> Unit, onShare: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldDeepDark
                ) {
                    Text(
                        text = dua.categoryBn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MintSoft,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "কপি", tint = DarkTextSecondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "শেয়ার", tint = DarkTextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = dua.titleBn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // আরবি হরফ
            Text(
                text = dua.arabicText,
                style = MaterialTheme.typography.headlineSmall.copy(lineHeight = 40.sp),
                textAlign = TextAlign.End,
                color = AntiqueGold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "উচ্চারণ: ${dua.pronunciationBn}",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "অর্থ: ${dua.meaningBn}",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "সূত্র: ${dua.referenceBn}",
                style = MaterialTheme.typography.bodySmall,
                color = MintBright
            )
        }
    }
}